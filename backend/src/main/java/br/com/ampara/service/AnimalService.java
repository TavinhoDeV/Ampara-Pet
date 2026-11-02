package br.com.ampara.service;

import br.com.ampara.dto.Dtos.AnimalRequest;
import br.com.ampara.dto.Dtos.AnimalResponse;
import br.com.ampara.exception.RecursoNaoEncontradoException;
import br.com.ampara.model.Animal;
import br.com.ampara.model.enums.StatusAnimal;
import br.com.ampara.model.enums.TipoAtendimento;
import br.com.ampara.repository.AnimalRepository;
import br.com.ampara.repository.AtendimentoRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnimalService {

    private final AnimalRepository animais;
    private final AtendimentoRepository atendimentos;

    public AnimalService(AnimalRepository animais, AtendimentoRepository atendimentos) {
        this.animais = animais;
        this.atendimentos = atendimentos;
    }

    @Transactional(readOnly = true)
    public List<AnimalResponse> listar(StatusAnimal status) {
        List<Animal> lista = (status == null)
                ? animais.findAllByOrderByCriadoEmDesc()
                : animais.findByStatusOrderByCriadoEmDesc(status);
        return lista.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public AnimalResponse buscar(Long id) {
        return toResponse(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public Animal buscarEntidade(Long id) {
        return animais.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Animal não encontrado."));
    }

    @Transactional
    public Animal criarEntidade(AnimalRequest req) {
        Animal a = new Animal();
        preencher(a, req);
        return animais.save(a);
    }

    @Transactional
    public AnimalResponse atualizar(Long id, AnimalRequest req) {
        Animal a = buscarEntidade(id);
        preencher(a, req);
        return toResponse(animais.save(a));
    }

    @Transactional
    public void excluir(Long id) {
        animais.delete(buscarEntidade(id));
    }

    public boolean possuiEtapaValidada(Long animalId, TipoAtendimento tipo) {
        return atendimentos.existsByAnimalIdAndTipoAndValidadoTrue(animalId, tipo);
    }

    public AnimalResponse toResponse(Animal a) {
        return new AnimalResponse(a.getId(), a.getNome(), a.getEspecie(), a.getPorte(), a.getDescricao(),
                a.getStatus(),
                possuiEtapaValidada(a.getId(), TipoAtendimento.VACINACAO),
                possuiEtapaValidada(a.getId(), TipoAtendimento.CASTRACAO),
                a.getCriadoEm());
    }

    private void preencher(Animal a, AnimalRequest req) {
        a.setNome(req.nome().trim());
        a.setEspecie(req.especie());
        a.setPorte(req.porte());
        a.setDescricao(req.descricao());
    }
}

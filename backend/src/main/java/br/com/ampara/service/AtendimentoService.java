package br.com.ampara.service;

import br.com.ampara.dto.Dtos.AtendimentoRequest;
import br.com.ampara.dto.Dtos.AtendimentoResponse;
import br.com.ampara.exception.RegraNegocioException;
import br.com.ampara.model.Animal;
import br.com.ampara.model.Atendimento;
import br.com.ampara.model.Usuario;
import br.com.ampara.model.enums.Perfil;
import br.com.ampara.model.enums.StatusAnimal;
import br.com.ampara.model.enums.TipoAtendimento;
import br.com.ampara.repository.AtendimentoRepository;
import java.util.List;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AtendimentoService {

    private final AtendimentoRepository atendimentos;
    private final AnimalService animalService;
    private final UsuarioService usuarioService;

    public AtendimentoService(AtendimentoRepository atendimentos, AnimalService animalService,
                              UsuarioService usuarioService) {
        this.atendimentos = atendimentos;
        this.animalService = animalService;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public AtendimentoResponse registrar(Long animalId, AtendimentoRequest req, String emailUsuario) {
        Animal animal = animalService.buscarEntidade(animalId);
        Usuario responsavel = usuarioService.buscarPorEmail(emailUsuario);

        if (animal.getStatus() == StatusAnimal.ADOTADO) {
            throw new RegraNegocioException("Animal já adotado: não é possível registrar novos atendimentos.");
        }
        boolean validado = Boolean.TRUE.equals(req.validado());
        if (validado && responsavel.getPerfil() == Perfil.VOLUNTARIO) {
            throw new AccessDeniedException("Somente clínica ou administração pode validar etapas.");
        }

        Atendimento a = new Atendimento();
        a.setAnimal(animal);
        a.setResponsavel(responsavel);
        a.setTipo(req.tipo());
        a.setDescricao(req.descricao().trim());
        a.setValidado(validado);
        atendimentos.save(a);

        atualizarStatus(animal, req.tipo(), validado);
        return toResponse(a);
    }

    @Transactional(readOnly = true)
    public List<AtendimentoResponse> listar(Long animalId) {
        animalService.buscarEntidade(animalId);
        return atendimentos.findByAnimalIdOrderByDataRegistroDesc(animalId).stream()
                .map(this::toResponse).toList();
    }

    private void atualizarStatus(Animal animal, TipoAtendimento tipo, boolean validado) {
        switch (tipo) {
            case TRIAGEM -> animal.setStatus(StatusAnimal.EM_TRIAGEM);
            case TRATAMENTO -> animal.setStatus(StatusAnimal.EM_TRATAMENTO);
            default -> { }
        }
        boolean etapaObrigatoria = tipo == TipoAtendimento.VACINACAO || tipo == TipoAtendimento.CASTRACAO;
        if (etapaObrigatoria && validado
                && animalService.possuiEtapaValidada(animal.getId(), TipoAtendimento.VACINACAO)
                && animalService.possuiEtapaValidada(animal.getId(), TipoAtendimento.CASTRACAO)) {
            animal.setStatus(StatusAnimal.APTO_ADOCAO);
        }
    }

    private AtendimentoResponse toResponse(Atendimento a) {
        return new AtendimentoResponse(a.getId(), a.getAnimal().getId(), a.getTipo(), a.getDescricao(),
                a.isValidado(), a.getResponsavel().getNome(), a.getDataRegistro());
    }
}

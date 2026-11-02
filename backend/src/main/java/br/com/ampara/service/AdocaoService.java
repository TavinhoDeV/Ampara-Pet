package br.com.ampara.service;

import br.com.ampara.dto.Dtos.AdocaoRequest;
import br.com.ampara.dto.Dtos.AdocaoResponse;
import br.com.ampara.exception.RegraNegocioException;
import br.com.ampara.model.Adocao;
import br.com.ampara.model.Animal;
import br.com.ampara.model.Usuario;
import br.com.ampara.model.enums.StatusAnimal;
import br.com.ampara.model.enums.TipoAtendimento;
import br.com.ampara.repository.AdocaoRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdocaoService {

    private final AdocaoRepository adocoes;
    private final AnimalService animalService;
    private final UsuarioService usuarioService;

    public AdocaoService(AdocaoRepository adocoes, AnimalService animalService, UsuarioService usuarioService) {
        this.adocoes = adocoes;
        this.animalService = animalService;
        this.usuarioService = usuarioService;
    }

    /**
     * Regra central do projeto: a adoção só é permitida quando vacinação e castração
     * foram validadas. A validação acontece aqui, na camada de serviço, e não na interface.
     */
    @Transactional
    public AdocaoResponse registrar(AdocaoRequest req, String emailUsuario) {
        Animal animal = animalService.buscarEntidade(req.animalId());

        if (animal.getStatus() == StatusAnimal.ADOTADO) {
            throw new RegraNegocioException("Este animal já foi adotado.");
        }

        List<String> pendencias = new ArrayList<>();
        if (!animalService.possuiEtapaValidada(animal.getId(), TipoAtendimento.VACINACAO)) {
            pendencias.add("vacinação");
        }
        if (!animalService.possuiEtapaValidada(animal.getId(), TipoAtendimento.CASTRACAO)) {
            pendencias.add("castração");
        }
        if (!pendencias.isEmpty()) {
            throw new RegraNegocioException(
                    "Adoção bloqueada: falta validar " + String.join(" e ", pendencias) + ".");
        }

        Usuario responsavel = usuarioService.buscarPorEmail(emailUsuario);
        Adocao a = new Adocao();
        a.setAnimal(animal);
        a.setAdotanteNome(req.adotanteNome().trim());
        a.setAdotanteContato(req.adotanteContato().trim());
        a.setRegistradoPor(responsavel);
        adocoes.save(a);

        animal.setStatus(StatusAnimal.ADOTADO);
        return toResponse(a);
    }

    @Transactional(readOnly = true)
    public List<AdocaoResponse> listar() {
        return adocoes.findAllByOrderByDataAdocaoDesc().stream().map(this::toResponse).toList();
    }

    private AdocaoResponse toResponse(Adocao a) {
        return new AdocaoResponse(a.getId(), a.getAnimal().getId(), a.getAnimal().getNome(),
                a.getAdotanteNome(), a.getAdotanteContato(), a.getDataAdocao());
    }
}

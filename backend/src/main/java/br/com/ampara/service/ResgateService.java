package br.com.ampara.service;

import br.com.ampara.dto.Dtos.ResgateRequest;
import br.com.ampara.dto.Dtos.ResgateResponse;
import br.com.ampara.model.Animal;
import br.com.ampara.model.Resgate;
import br.com.ampara.model.Usuario;
import br.com.ampara.repository.ResgateRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResgateService {

    private final ResgateRepository resgates;
    private final AnimalService animalService;
    private final UsuarioService usuarioService;

    public ResgateService(ResgateRepository resgates, AnimalService animalService, UsuarioService usuarioService) {
        this.resgates = resgates;
        this.animalService = animalService;
        this.usuarioService = usuarioService;
    }

    /** Registra o animal e o resgate na mesma transação. */
    @Transactional
    public ResgateResponse registrar(ResgateRequest req, String emailUsuario) {
        Usuario voluntario = usuarioService.buscarPorEmail(emailUsuario);
        Animal animal = animalService.criarEntidade(req.animal());

        Resgate r = new Resgate();
        r.setAnimal(animal);
        r.setVoluntario(voluntario);
        r.setLocal(req.local().trim());
        r.setObservacoes(req.observacoes());
        return toResponse(resgates.save(r));
    }

    @Transactional(readOnly = true)
    public List<ResgateResponse> listar() {
        return resgates.findAllByOrderByDataResgateDesc().stream().map(this::toResponse).toList();
    }

    private ResgateResponse toResponse(Resgate r) {
        return new ResgateResponse(r.getId(), animalService.toResponse(r.getAnimal()),
                r.getVoluntario().getNome(), r.getLocal(), r.getObservacoes(), r.getDataResgate());
    }
}

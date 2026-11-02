package br.com.ampara.controller;

import br.com.ampara.dto.Dtos.AnimalRequest;
import br.com.ampara.dto.Dtos.AnimalResponse;
import br.com.ampara.dto.Dtos.AtendimentoRequest;
import br.com.ampara.dto.Dtos.AtendimentoResponse;
import br.com.ampara.model.enums.StatusAnimal;
import br.com.ampara.service.AnimalService;
import br.com.ampara.service.AtendimentoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/animais")
public class AnimalController {

    private final AnimalService animais;
    private final AtendimentoService atendimentos;

    public AnimalController(AnimalService animais, AtendimentoService atendimentos) {
        this.animais = animais;
        this.atendimentos = atendimentos;
    }

    @GetMapping
    public List<AnimalResponse> listar(@RequestParam(required = false) StatusAnimal status) {
        return animais.listar(status);
    }

    @GetMapping("/{id}")
    public AnimalResponse buscar(@PathVariable Long id) {
        return animais.buscar(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLINICA','ADMIN')")
    public AnimalResponse atualizar(@PathVariable Long id, @Valid @RequestBody AnimalRequest req) {
        return animais.atualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        animais.excluir(id);
    }

    @GetMapping("/{id}/atendimentos")
    public List<AtendimentoResponse> listarAtendimentos(@PathVariable Long id) {
        return atendimentos.listar(id);
    }

    @PostMapping("/{id}/atendimentos")
    @ResponseStatus(HttpStatus.CREATED)
    public AtendimentoResponse registrarAtendimento(@PathVariable Long id,
                                                    @Valid @RequestBody AtendimentoRequest req,
                                                    Authentication auth) {
        return atendimentos.registrar(id, req, auth.getName());
    }
}

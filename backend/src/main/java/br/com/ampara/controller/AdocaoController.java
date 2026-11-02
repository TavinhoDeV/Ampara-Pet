package br.com.ampara.controller;

import br.com.ampara.dto.Dtos.AdocaoRequest;
import br.com.ampara.dto.Dtos.AdocaoResponse;
import br.com.ampara.service.AdocaoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/adocoes")
@PreAuthorize("hasRole('ADMIN')")
public class AdocaoController {

    private final AdocaoService adocoes;

    public AdocaoController(AdocaoService adocoes) {
        this.adocoes = adocoes;
    }

    @GetMapping
    public List<AdocaoResponse> listar() {
        return adocoes.listar();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AdocaoResponse registrar(@Valid @RequestBody AdocaoRequest req, Authentication auth) {
        return adocoes.registrar(req, auth.getName());
    }
}

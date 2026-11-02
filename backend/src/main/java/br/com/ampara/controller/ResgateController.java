package br.com.ampara.controller;

import br.com.ampara.dto.Dtos.ResgateRequest;
import br.com.ampara.dto.Dtos.ResgateResponse;
import br.com.ampara.service.ResgateService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/resgates")
public class ResgateController {

    private final ResgateService resgates;

    public ResgateController(ResgateService resgates) {
        this.resgates = resgates;
    }

    @GetMapping
    public List<ResgateResponse> listar() {
        return resgates.listar();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResgateResponse registrar(@Valid @RequestBody ResgateRequest req, Authentication auth) {
        return resgates.registrar(req, auth.getName());
    }
}

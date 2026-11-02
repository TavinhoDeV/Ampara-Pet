package br.com.ampara.controller;

import br.com.ampara.dto.Dtos.CadastroRequest;
import br.com.ampara.dto.Dtos.LoginRequest;
import br.com.ampara.dto.Dtos.LoginResponse;
import br.com.ampara.dto.Dtos.UsuarioResponse;
import br.com.ampara.model.enums.Perfil;
import br.com.ampara.service.AuthService;
import br.com.ampara.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UsuarioService usuarioService;

    public AuthController(AuthService authService, UsuarioService usuarioService) {
        this.authService = authService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }

    /** Cadastro público: sempre cria o perfil VOLUNTARIO. Outros perfis só pelo administrador. */
    @PostMapping("/cadastro")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse cadastro(@Valid @RequestBody CadastroRequest req) {
        return usuarioService.toResponse(
                usuarioService.criar(req.nome(), req.email(), req.senha(), Perfil.VOLUNTARIO));
    }
}

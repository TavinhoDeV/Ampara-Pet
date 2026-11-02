package br.com.ampara.service;

import br.com.ampara.config.JwtService;
import br.com.ampara.dto.Dtos.LoginRequest;
import br.com.ampara.dto.Dtos.LoginResponse;
import br.com.ampara.exception.CredenciaisInvalidasException;
import br.com.ampara.model.Usuario;
import br.com.ampara.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthService(UsuarioRepository usuarios, PasswordEncoder encoder, JwtService jwt) {
        this.usuarios = usuarios;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest req) {
        Usuario u = usuarios.findByEmail(req.email().trim().toLowerCase())
                .filter(Usuario::isAtivo)
                .filter(x -> encoder.matches(req.senha(), x.getSenhaHash()))
                .orElseThrow(CredenciaisInvalidasException::new);
        return new LoginResponse(jwt.gerar(u), u.getNome(), u.getEmail(), u.getPerfil());
    }
}

package br.com.ampara.service;

import br.com.ampara.dto.Dtos.UsuarioResponse;
import br.com.ampara.exception.ConflitoException;
import br.com.ampara.exception.RecursoNaoEncontradoException;
import br.com.ampara.model.Usuario;
import br.com.ampara.model.enums.Perfil;
import br.com.ampara.repository.UsuarioRepository;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;

    public UsuarioService(UsuarioRepository usuarios, PasswordEncoder encoder) {
        this.usuarios = usuarios;
        this.encoder = encoder;
    }

    @Transactional
    public Usuario criar(String nome, String email, String senha, Perfil perfil) {
        String emailNormalizado = email.trim().toLowerCase();
        if (usuarios.existsByEmail(emailNormalizado)) {
            throw new ConflitoException("E-mail já cadastrado.");
        }
        Usuario u = new Usuario();
        u.setNome(nome.trim());
        u.setEmail(emailNormalizado);
        u.setSenhaHash(encoder.encode(senha));
        u.setPerfil(perfil);
        return usuarios.save(u);
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorEmail(String email) {
        return usuarios.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return usuarios.findAll().stream().map(this::toResponse).toList();
    }

    public UsuarioResponse toResponse(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNome(), u.getEmail(), u.getPerfil(), u.isAtivo());
    }
}

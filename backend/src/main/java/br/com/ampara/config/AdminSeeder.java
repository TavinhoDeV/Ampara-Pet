package br.com.ampara.config;

import br.com.ampara.model.enums.Perfil;
import br.com.ampara.repository.UsuarioRepository;
import br.com.ampara.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/** Cria o primeiro administrador quando o banco ainda não tem usuários. */
@Component
public class AdminSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

    private final UsuarioRepository usuarios;
    private final UsuarioService usuarioService;
    private final String email;
    private final String senha;

    public AdminSeeder(UsuarioRepository usuarios, UsuarioService usuarioService,
                       @Value("${ampara.admin.email}") String email,
                       @Value("${ampara.admin.senha}") String senha) {
        this.usuarios = usuarios;
        this.usuarioService = usuarioService;
        this.email = email;
        this.senha = senha;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (usuarios.count() == 0) {
            usuarioService.criar("Administrador", email, senha, Perfil.ADMIN);
            log.warn("Administrador inicial criado ({}). Troque a senha padrão em produção.", email);
        }
    }
}

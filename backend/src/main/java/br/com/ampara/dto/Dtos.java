package br.com.ampara.dto;

import br.com.ampara.model.enums.Especie;
import br.com.ampara.model.enums.Perfil;
import br.com.ampara.model.enums.StatusAnimal;
import br.com.ampara.model.enums.TipoAtendimento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.Map;

public final class Dtos {

    private Dtos() {
    }

    // ----- autenticação / usuários -----
    public record LoginRequest(
            @NotBlank(message = "Informe o e-mail.") @Email(message = "E-mail inválido.") String email,
            @NotBlank(message = "Informe a senha.") String senha) {
    }

    public record CadastroRequest(
            @NotBlank(message = "Informe o nome.") String nome,
            @NotBlank(message = "Informe o e-mail.") @Email(message = "E-mail inválido.") String email,
            @NotBlank(message = "Informe a senha.")
            @Size(min = 8, message = "A senha deve ter ao menos 8 caracteres.") String senha) {
    }

    public record UsuarioAdminRequest(
            @NotBlank(message = "Informe o nome.") String nome,
            @NotBlank(message = "Informe o e-mail.") @Email(message = "E-mail inválido.") String email,
            @NotBlank(message = "Informe a senha.")
            @Size(min = 8, message = "A senha deve ter ao menos 8 caracteres.") String senha,
            @NotNull(message = "Informe o perfil.") Perfil perfil) {
    }

    public record LoginResponse(String token, String nome, String email, Perfil perfil) {
    }

    public record UsuarioResponse(Long id, String nome, String email, Perfil perfil, boolean ativo) {
    }

    // ----- animais / resgates -----
    public record AnimalRequest(
            @NotBlank(message = "Informe o nome do animal.") String nome,
            @NotNull(message = "Informe a espécie.") Especie especie,
            String porte,
            String descricao) {
    }

    public record AnimalResponse(Long id, String nome, Especie especie, String porte, String descricao,
                                 StatusAnimal status, boolean vacinado, boolean castrado, Instant criadoEm) {
    }

    public record ResgateRequest(
            @NotNull(message = "Informe os dados do animal.") @Valid AnimalRequest animal,
            @NotBlank(message = "Informe o local do resgate.") String local,
            String observacoes) {
    }

    public record ResgateResponse(Long id, AnimalResponse animal, String voluntario, String local,
                                  String observacoes, Instant dataResgate) {
    }

    // ----- atendimentos / adoções -----
    public record AtendimentoRequest(
            @NotNull(message = "Informe o tipo de atendimento.") TipoAtendimento tipo,
            @NotBlank(message = "Informe a descrição.") String descricao,
            Boolean validado) {
    }

    public record AtendimentoResponse(Long id, Long animalId, TipoAtendimento tipo, String descricao,
                                      boolean validado, String responsavel, Instant dataRegistro) {
    }

    public record AdocaoRequest(
            @NotNull(message = "Informe o animal.") Long animalId,
            @NotBlank(message = "Informe o nome do adotante.") String adotanteNome,
            @NotBlank(message = "Informe o contato do adotante.") String adotanteContato) {
    }

    public record AdocaoResponse(Long id, Long animalId, String animalNome, String adotanteNome,
                                 String adotanteContato, Instant dataAdocao) {
    }

    // ----- erro -----
    public record ErroResponse(int status, String mensagem, Map<String, String> campos, Instant timestamp) {
    }
}

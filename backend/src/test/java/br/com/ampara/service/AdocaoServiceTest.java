package br.com.ampara.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import br.com.ampara.dto.Dtos.AdocaoRequest;
import br.com.ampara.exception.RegraNegocioException;
import br.com.ampara.model.Animal;
import br.com.ampara.model.enums.Especie;
import br.com.ampara.model.enums.StatusAnimal;
import br.com.ampara.repository.AdocaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdocaoServiceTest {

    @Mock
    private AdocaoRepository adocoes;
    @Mock
    private AnimalService animalService;
    @Mock
    private UsuarioService usuarioService;
    @InjectMocks
    private AdocaoService service;

    private Animal animal(StatusAnimal status) {
        Animal a = new Animal();
        a.setNome("Rex");
        a.setEspecie(Especie.CAO);
        a.setStatus(status);
        return a;
    }

    @Test
    void bloqueiaAdocaoSemVacinacaoECastracao() {
        when(animalService.buscarEntidade(1L)).thenReturn(animal(StatusAnimal.EM_TRIAGEM));

        RegraNegocioException ex = assertThrows(RegraNegocioException.class,
                () -> service.registrar(new AdocaoRequest(1L, "Maria", "maria@email.com"), "admin@ampara.local"));

        assertTrue(ex.getMessage().contains("vacinação"));
        assertTrue(ex.getMessage().contains("castração"));
    }

    @Test
    void bloqueiaAdocaoDeAnimalJaAdotado() {
        when(animalService.buscarEntidade(1L)).thenReturn(animal(StatusAnimal.ADOTADO));

        assertThrows(RegraNegocioException.class,
                () -> service.registrar(new AdocaoRequest(1L, "Maria", "maria@email.com"), "admin@ampara.local"));
    }
}

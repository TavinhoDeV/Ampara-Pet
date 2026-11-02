package br.com.ampara.repository;

import br.com.ampara.model.Atendimento;
import br.com.ampara.model.enums.TipoAtendimento;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AtendimentoRepository extends JpaRepository<Atendimento, Long> {
    List<Atendimento> findByAnimalIdOrderByDataRegistroDesc(Long animalId);

    boolean existsByAnimalIdAndTipoAndValidadoTrue(Long animalId, TipoAtendimento tipo);
}

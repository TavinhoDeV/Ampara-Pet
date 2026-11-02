package br.com.ampara.repository;

import br.com.ampara.model.Animal;
import br.com.ampara.model.enums.StatusAnimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnimalRepository extends JpaRepository<Animal, Long> {
    List<Animal> findByStatusOrderByCriadoEmDesc(StatusAnimal status);

    List<Animal> findAllByOrderByCriadoEmDesc();
}

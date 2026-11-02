package br.com.ampara.repository;

import br.com.ampara.model.Resgate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResgateRepository extends JpaRepository<Resgate, Long> {
    List<Resgate> findAllByOrderByDataResgateDesc();
}

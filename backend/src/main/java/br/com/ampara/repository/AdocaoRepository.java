package br.com.ampara.repository;

import br.com.ampara.model.Adocao;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdocaoRepository extends JpaRepository<Adocao, Long> {
    List<Adocao> findAllByOrderByDataAdocaoDesc();
}

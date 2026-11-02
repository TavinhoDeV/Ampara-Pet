package br.com.ampara.model;

import br.com.ampara.model.enums.*;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "animal")
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Especie especie;

    @Column
    private String porte;

    @Column(columnDefinition = "text")
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAnimal status = StatusAnimal.RESGATADO;

    @Column(nullable = false)
    private Instant criadoEm = Instant.now();

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Especie getEspecie() { return especie; }
    public void setEspecie(Especie especie) { this.especie = especie; }
    public String getPorte() { return porte; }
    public void setPorte(String porte) { this.porte = porte; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public StatusAnimal getStatus() { return status; }
    public void setStatus(StatusAnimal status) { this.status = status; }
    public Instant getCriadoEm() { return criadoEm; }
    public void setCriadoEm(Instant criadoEm) { this.criadoEm = criadoEm; }
}

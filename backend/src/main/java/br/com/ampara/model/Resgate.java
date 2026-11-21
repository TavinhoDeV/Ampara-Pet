package br.com.ampara.model;


import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "resgate")
public class Resgate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "animal_id")
    private Animal animal;

    @ManyToOne(optional = false)
    @JoinColumn(name = "voluntario_id")
    private Usuario voluntario;

    @Column(nullable = false)
    private String local;

    @Column(columnDefinition = "text")
    private String observacoes;

    @Column(nullable = false)
    private Instant dataResgate = Instant.now();

    public Long getId() { return id; }
    public Animal getAnimal() { return animal; }
    public void setAnimal(Animal animal) { this.animal = animal; }
    public Usuario getVoluntario() { return voluntario; }
    public void setVoluntario(Usuario voluntario) { this.voluntario = voluntario; }
    public String getLocal() { return local; }
    public void setLocal(String local) { this.local = local; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    public Instant getDataResgate() { return dataResgate; }
    public void setDataResgate(Instant dataResgate) { this.dataResgate = dataResgate; }
}

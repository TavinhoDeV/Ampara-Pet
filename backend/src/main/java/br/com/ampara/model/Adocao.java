package br.com.ampara.model;


import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "adocao")
public class Adocao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "animal_id", unique = true)
    private Animal animal;

    @Column(nullable = false)
    private String adotanteNome;

    @Column(nullable = false)
    private String adotanteContato;

    @ManyToOne(optional = false)
    @JoinColumn(name = "registrado_por_id")
    private Usuario registradoPor;

    @Column(nullable = false)
    private Instant dataAdocao = Instant.now();

    public Long getId() { return id; }
    public Animal getAnimal() { return animal; }
    public void setAnimal(Animal animal) { this.animal = animal; }
    public String getAdotanteNome() { return adotanteNome; }
    public void setAdotanteNome(String adotanteNome) { this.adotanteNome = adotanteNome; }
    public String getAdotanteContato() { return adotanteContato; }
    public void setAdotanteContato(String adotanteContato) { this.adotanteContato = adotanteContato; }
    public Usuario getRegistradoPor() { return registradoPor; }
    public void setRegistradoPor(Usuario registradoPor) { this.registradoPor = registradoPor; }
    public Instant getDataAdocao() { return dataAdocao; }
    public void setDataAdocao(Instant dataAdocao) { this.dataAdocao = dataAdocao; }
}

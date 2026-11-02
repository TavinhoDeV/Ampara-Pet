package br.com.ampara.model;

import br.com.ampara.model.enums.*;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "atendimento")
public class Atendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "animal_id")
    private Animal animal;

    @ManyToOne(optional = false)
    @JoinColumn(name = "responsavel_id")
    private Usuario responsavel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoAtendimento tipo;

    @Column(columnDefinition = "text", nullable = false)
    private String descricao;

    @Column(nullable = false)
    private boolean validado = false;

    @Column(nullable = false)
    private Instant dataRegistro = Instant.now();

    public Long getId() { return id; }
    public Animal getAnimal() { return animal; }
    public void setAnimal(Animal animal) { this.animal = animal; }
    public Usuario getResponsavel() { return responsavel; }
    public void setResponsavel(Usuario responsavel) { this.responsavel = responsavel; }
    public TipoAtendimento getTipo() { return tipo; }
    public void setTipo(TipoAtendimento tipo) { this.tipo = tipo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public boolean isValidado() { return validado; }
    public void setValidado(boolean validado) { this.validado = validado; }
    public Instant getDataRegistro() { return dataRegistro; }
    public void setDataRegistro(Instant dataRegistro) { this.dataRegistro = dataRegistro; }
}

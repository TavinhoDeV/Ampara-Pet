package br.com.ampara.model;

import br.com.ampara.model.enums.*;
import jakarta.persistence.*;


@Entity
@Table(name = "instituicao")
public class Instituicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoInstituicao tipo;

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public TipoInstituicao getTipo() { return tipo; }
    public void setTipo(TipoInstituicao tipo) { this.tipo = tipo; }
}

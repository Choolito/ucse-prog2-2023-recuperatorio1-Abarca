package org.example.model;

import javax.persistence.*;

@Entity
@Table(name = "participantes")
public class Participante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @Column(name = "nombre")
    String nombre;
    @Column(name = "sala_asignada")
    int sala_asignada;
    @ManyToOne
    @JoinColumn(name = "sala_asignada", insertable = false, updatable = false)
    Sala sala;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getSala_asignada() {
        return sala_asignada;
    }

    public void setSala_asignada(int sala_asignada) {
        this.sala_asignada = sala_asignada;
    }

    public Sala getSala() {
        return sala;
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }
}

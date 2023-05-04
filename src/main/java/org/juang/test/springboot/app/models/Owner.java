package org.juang.test.springboot.app.models;


import javax.persistence.*;
import java.util.*;


@Entity
@Table(name = "owners")
public class Owner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String apellido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wine_id") // utiliza "wineID" en lugar de "wine_id"
    private Wine wine;


    // Constructor
    public Owner() {}

    public Owner(String name, String apellido,Long id) {
        this.id = id;
        this.name = name;
        this.apellido = apellido;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }


    public void setWine(Wine wine) {
        this.wine = wine;
    }
}



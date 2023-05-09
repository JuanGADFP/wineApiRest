package org.juang.test.springboot.app.models;


import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.*;
import java.util.*;


@Document(collection = "owners")
public class Owner {

    @Id
    private String id;
    private String name;
    private String apellido;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wine_id")
    private Wine wine;




    // Constructor
    public Owner() {}

    public Owner(String name, String apellido,String id) {
        this.id = id;
        this.name = name;
        this.apellido = apellido;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
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

/*
    public void setWine(Wine wine) {
        this.wine = wine;
    }

 */
}



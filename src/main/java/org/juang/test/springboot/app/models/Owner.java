package org.juang.test.springboot.app.models;


import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.*;

@Document(collection = "owners")
public class Owner {

    @Id
    private Long id;
    private String name;
    private String apellido;

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

}



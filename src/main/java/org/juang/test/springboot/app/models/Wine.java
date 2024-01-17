package org.juang.test.springboot.app.models;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.*;
import java.util.*;


@Document(collection = "wines")
public class Wine {

    @Id
    private String id;
    private String name;
    private String winery;
    private int año;

    @DBRef
    private List<Owner> owners ;

    public Wine() {
    }

    public Wine(String id, String name, String winery, int año, List<Owner> owners) {
        this.id = id;
        this.name = name;
        this.winery = winery;
        this.año = año;
        this.owners = owners;
    }

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

    public String getWinery() {
        return winery;
    }

    public void setWinery(String winery) {
        this.winery = winery;
    }

    public int getAño() {
        return año;
    }

    public void setAño(int año) {
        this.año = año;
    }

    public List<Owner> getOwners() {
        return owners;
    }

    public void setOwners(List<Owner> owners) {
        this.owners = owners;
    }
}

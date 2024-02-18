package com.gradeCalculator.server.Entities;

import jakarta.persistence.*;

import java.util.Set;

@Entity
public class ModuleEntity {
    @Id
    @GeneratedValue
    private Integer id;

    private String name;

    public ModuleEntity(){}

    public ModuleEntity(String name){
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}

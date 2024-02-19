package com.gradeCalculator.server.Entities;

import jakarta.persistence.*;

import java.util.Objects;
import java.util.Set;

@Entity
public class SubjectEntity {
    @Id
    @GeneratedValue
    private Integer id;

    private String name;

    public SubjectEntity(){}

    public SubjectEntity(String name){
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubjectEntity subject = (SubjectEntity) o;
        return Objects.equals(getId(), subject.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
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

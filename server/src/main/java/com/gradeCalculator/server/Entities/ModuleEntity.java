package com.gradeCalculator.server.Entities;

import jakarta.persistence.*;

import java.util.Set;

@Entity
public class ModuleEntity {
    @Id
    @GeneratedValue
    private Integer id;

    private String name;

    private double grade;

    private double gradingFactor;

    public ModuleEntity(){}

    public ModuleEntity(String name, double gradingFactor){
        this.name = name;
        this.gradingFactor = gradingFactor;
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


    public double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        this.grade = grade;
    }

    public double getGradingFactor() {
        return gradingFactor;
    }

    public void setGradingFactor(double gradingFactor) {
        this.gradingFactor = gradingFactor;
    }
}

package com.gradeCalculator.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Entity
public class ModuleEntity {
    @Id
    @GeneratedValue
    private Integer id;

    @NotBlank
    private String name;

    @Max(value = 5)
    @Min(value = 0)
    private double grade;

    @Max(value = 1)
    @Min(value = 0)
    private double gradingFactor;

    @ManyToOne
    private SubjectEntity subject;

    public ModuleEntity(){}

    public ModuleEntity(String name, double gradingFactor, SubjectEntity subject){
        this.name = name;
        this.gradingFactor = gradingFactor;
        this.subject = subject;
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

    public SubjectEntity getSubject() {
        return subject;
    }

    public void setSubject(SubjectEntity subject) {
        this.subject = subject;
    }
}

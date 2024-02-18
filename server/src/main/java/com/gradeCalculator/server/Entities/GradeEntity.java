package com.gradeCalculator.server.Entities;

import jakarta.persistence.*;

@Entity
public class GradeEntity {
    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    private UserEntity user;

    @ManyToOne
    private ModuleEntity module;

    private double grade;

    public GradeEntity(){}

    public GradeEntity(UserEntity user, ModuleEntity module, double grade){
        this.user=user;
        this.module = module;
        this.grade = grade;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public ModuleEntity getModule() {
        return module;
    }

    public void setModule(ModuleEntity module) {
        this.module = module;
    }

    public double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        this.grade = grade;
    }
}

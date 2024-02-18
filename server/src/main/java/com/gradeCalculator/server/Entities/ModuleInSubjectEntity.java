package com.gradeCalculator.server.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class ModuleInSubjectEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    private SubjectEntity subject;

    @ManyToOne
    private ModuleEntity module;

    //factor that the module grade affects the subject grade at.
    private double gradingFactor;

    public ModuleInSubjectEntity(){};

    public ModuleInSubjectEntity(SubjectEntity subject, ModuleEntity module, double gradingFactor){
        this.subject=subject;
        this.module=module;
        this.gradingFactor=gradingFactor;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ModuleEntity getModule() {
        return module;
    }

    public void setModule(ModuleEntity module) {
        this.module = module;
    }

    public SubjectEntity getSubject() {
        return subject;
    }

    public void setSubject(SubjectEntity subject) {
        this.subject = subject;
    }

    public double getGradingFactor() {
        return gradingFactor;
    }

    public void setGradingFactor(double gradingFactor) {
        this.gradingFactor = gradingFactor;
    }
}

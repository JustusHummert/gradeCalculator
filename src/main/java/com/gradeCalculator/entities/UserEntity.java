package com.gradeCalculator.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;

@Entity
public class UserEntity {

    @Id
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<SubjectEntity> subjects;

    public UserEntity(){}
    public UserEntity(String username, String password){
        this.username = username;
        this.password = password;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<SubjectEntity> getSubjects() {
        return subjects;
    }

    public void setSubjects(Set<SubjectEntity> subjects) {
        this.subjects = subjects;
    }
}

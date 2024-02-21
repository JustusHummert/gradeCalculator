package com.gradeCalculator.server.Entities;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class UserEntity {
    @Id
    private String username;

    private String password;

    private String salt;

    @OneToMany(fetch =  FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<SubjectEntity> subjects;

    public UserEntity(){}
    public UserEntity(String username, String password, String salt){
        this.username = username;
        this.password = password;
        this.salt = salt;
        this.subjects = new HashSet<SubjectEntity>();
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

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}

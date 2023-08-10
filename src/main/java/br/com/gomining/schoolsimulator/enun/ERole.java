package br.com.gomining.schoolsimulator.enun;

import lombok.Getter;

@Getter
public enum ERole {
    STUDENT("student"),
    TEACHER("teacher"),
    ADMIN("admin");

    private final String role;

    ERole(String role){
        this.role = role;
    }

}

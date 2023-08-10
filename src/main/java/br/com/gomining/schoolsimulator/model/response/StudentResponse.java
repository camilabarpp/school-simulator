package br.com.gomining.schoolsimulator.model.response;

import br.com.gomining.schoolsimulator.model.entity.activity.Activity;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StudentResponse {
    private String id;
    private String fullName;
    private String cpf;
    private String email;
    private String telephone;
    private String registrationDate;
    private String lastUpdateDate;
    private List<Activity> activities;
}



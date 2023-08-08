package br.com.gomining.schoolsimulator.model.response;

import lombok.Builder;
import lombok.Data;

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
}

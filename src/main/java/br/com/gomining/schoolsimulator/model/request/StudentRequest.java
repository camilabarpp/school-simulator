package br.com.gomining.schoolsimulator.model.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Getter
public class StudentRequest {
    @NotBlank(message = "fullName is mandatory")
    private String fullName;
    @NotBlank(message = "cpf is mandatory")
    private String cpf;
    @NotBlank(message = "email is mandatory")
    private String email;
    @NotBlank(message = "telephone is mandatory")
    private String telephone;
    private String registrationDate;
    private String lastUpdateDate;
}

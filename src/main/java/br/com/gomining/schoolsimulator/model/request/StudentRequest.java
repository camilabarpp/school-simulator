package br.com.gomining.schoolsimulator.model.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@Getter
public class StudentRequest {
    @NotNull
    @NotBlank(message = "fullName is mandatory")
    private String fullName;
    @NotNull
    @NotBlank(message = "cpf is mandatory")
    private String cpf;
    @NotNull
    @NotBlank(message = "email is mandatory")
    private String email;
    @NotNull
    @NotBlank(message = "telephone is mandatory")
    private String telephone;
    private String registrationDate;
    private String lastUpdateDate;
}

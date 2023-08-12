package br.com.gomining.schoolsimulator.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Getter
@AllArgsConstructor
@Builder
public class StudentRequest {
    @NotBlank(message = "fullName is mandatory")
    private String fullName;
    @NotBlank(message = "cpf is mandatory")
    @CPF(message = "invalid CPF")
    private String cpf;
    @NotBlank(message = "email is mandatory")
    @Email(message = "invalid e-mail")
    private String email;
    @NotBlank(message = "telephone is mandatory")
    private String telephone;
    private String registrationDate;
    private String lastUpdateDate;
}

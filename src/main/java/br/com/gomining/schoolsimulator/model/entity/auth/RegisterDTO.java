package br.com.gomining.schoolsimulator.model.entity.auth;

import br.com.gomining.schoolsimulator.enun.ERole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDTO {
    private String username;
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[!@#$%^&*])(?=.{9,}).*$", message = "A senha não atende aos critérios de segurança.")
    private String password;
    private ERole role;
}

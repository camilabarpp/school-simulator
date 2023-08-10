package br.com.gomining.schoolsimulator.model.entity.auth;

import br.com.gomining.schoolsimulator.enun.ERole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDTO {
    private String username;
    private String password;
    private ERole role;
}

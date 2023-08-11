package br.com.gomining.schoolsimulator.controller;

import br.com.gomining.schoolsimulator.common.exception.EmailAlreadyExistsException;
import br.com.gomining.schoolsimulator.common.security.TokenService;
import br.com.gomining.schoolsimulator.model.entity.auth.AuthenticationDTO;
import br.com.gomining.schoolsimulator.model.entity.auth.LoginResponseDTO;
import br.com.gomining.schoolsimulator.model.entity.auth.RegisterDTO;
import br.com.gomining.schoolsimulator.model.entity.user.User;
import br.com.gomining.schoolsimulator.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("auth")
@AllArgsConstructor
@Tag(name = "Authentication", description = "Authentication API")
public class AuthenticationController {
    private AuthenticationManager authenticationManager;
    private UserRepository repository;
    private TokenService tokenService;

    @PostMapping("/login")
    @Operation(summary = "Login")
    public LoginResponseDTO login(@RequestBody @Valid AuthenticationDTO data){
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.getUsername(), data.getPassword());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((User) auth.getPrincipal());

        return new LoginResponseDTO(token);
    }

    @PostMapping("/register")
    @Operation(summary = "Register")
    public RegisterDTO register(@RequestBody @Valid RegisterDTO data){
        if(this.repository.findByUsername(data.getUsername()) != null) {
            throw new EmailAlreadyExistsException("Email already exists"); // Lança uma exceção personalizada
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.getPassword());
        User newUser = new User(data.getUsername(), encryptedPassword, data.getRole());

        this.repository.save(newUser);

        return data; // Retorna o objeto RegisterDTO
    }
}

package br.com.gomining.schoolsimulator.service;

import br.com.gomining.schoolsimulator.common.exception.ApiNotFoundException;
import br.com.gomining.schoolsimulator.enun.ERole;
import br.com.gomining.schoolsimulator.model.entity.user.User;
import br.com.gomining.schoolsimulator.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static br.com.gomining.schoolsimulator.enun.ERole.ADMIN;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class AuthorizationServiceTest {

    @InjectMocks
    private AuthorizationService authorizationService;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("Test loadUserByUsername with valid username")
    void testLoadUserByUsername_ValidUsername_ReturnsUserDetails() {
        // Arrange
        String username = "testuser";
        User user = new User(username, "password", ADMIN);
        when(userRepository.findByUsername(username)).thenReturn(user);

        // Act
        var userDetails = authorizationService.loadUserByUsername(username);

        // Assert
        assertEquals(user, userDetails);
    }
}
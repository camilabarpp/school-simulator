package br.com.gomining.schoolsimulator.common.security;

import br.com.gomining.schoolsimulator.common.exception.InvalidTokenException;
import br.com.gomining.schoolsimulator.enun.ERole;
import br.com.gomining.schoolsimulator.model.entity.user.User;
import br.com.gomining.schoolsimulator.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
@ExtendWith(SpringExtension.class)
class SecurityFilterTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SecurityFilter securityFilter;

    @Test
    void testValidToken() throws Exception {
        User user = new User("user@mail.com", "password", ERole.ADMIN);
        String token = "yourGeneratedToken";

        when(tokenService.validateToken(anyString())).thenReturn(user.getUsername());
        when(userRepository.findByUsername(anyString())).thenReturn(user);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        HttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = new MockFilterChain();

        securityFilter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertEquals(user.getUsername(), authentication.getName());
    }

    @Test
    void testInvalidToken() throws Exception {
        String invalidToken = "invalidToken";

        when(tokenService.validateToken(anyString())).thenReturn("");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + invalidToken);

        assertThrows(InvalidTokenException.class,
                () -> securityFilter.doFilterInternal(request, null, null)
        );
    }

    @Test
    @DisplayName("Should not authenticate if token is missing")
    void testMissingToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = new MockFilterChain();

        securityFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}

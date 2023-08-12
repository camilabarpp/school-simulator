package br.com.gomining.schoolsimulator.controller;

import br.com.gomining.schoolsimulator.common.security.TokenService;
import br.com.gomining.schoolsimulator.enun.ERole;
import br.com.gomining.schoolsimulator.model.entity.auth.RegisterDTO;
import br.com.gomining.schoolsimulator.model.entity.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = "spring.mongodb.embedded.version=3.2.8")
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    ObjectMapper mapper = new ObjectMapper();

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        mongoTemplate.remove(new Query(), User.class);
    }

    @Test
    @DisplayName("Should login with valid credentials")
    void shouldLoginWithValidCredentials() throws Exception {
        User user = new User("admin3@mail.com", "admin", ERole.ADMIN);
        mongoTemplate.insert(user);
        String username = "admin3@mail.com";
        String password = "admin";
        String token = "yourGeneratedToken";

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                username,
                password
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                new User(username, password, null),
                null,
                null
        );

        String body = "{\n" +
                "    \"username\": \"admin3@mail.com\",\n" +
                "    \"password\": \"admin\"\n" +
                "}";

        when(authenticationManager.authenticate(usernamePasswordAuthenticationToken)).thenReturn(authentication);
        when(tokenService.generateToken(any())).thenReturn(token);

        MvcResult result = mvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token))
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assertTrue(json.contains(token));
    }

    @Test
    @DisplayName("Should throws MethodArgumentNotValidException when try to register a new user with invalid password")
    void shouldThrowsMethodArgumentNotValidExceptionWhenToTryRegisterANewUserWithInvalidPassword() throws Exception {
        RegisterDTO registerDTO = new RegisterDTO("admin", "StrongPassword@1", ERole.ADMIN);

        MvcResult result = mvc.perform(post("/auth/register")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(registerDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assertThat(json).contains("admin");
    }

    @Test
    @DisplayName("Should register a new user")
    void shouldRegisterANewUser() throws Exception {
        RegisterDTO registerDTO = new RegisterDTO("admin", "Pass@word123", ERole.ADMIN);

        MvcResult result = mvc.perform(post("/auth/register")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(registerDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assertThat(json).contains("admin");
    }

    @Test
    @DisplayName("Should not register aa new user if email is already registered")
    void shouldNotRegisterANewUserIfEmailIsAlreadyRegistered() throws Exception {
        RegisterDTO registerDTO = new RegisterDTO("admin@mail.com", "admin", ERole.ADMIN);
        mongoTemplate.insert(new User("admin@mail.com", "admin", ERole.ADMIN));

        MvcResult result = mvc.perform(post("/auth/register")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(registerDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assertThat(json).contains("A senha nÃ£o atende aos critÃ©rios de seguranÃ§a.");
        assertThat(json).contains("BAD_REQUEST");
        assertThat(json).contains("MethodArgumentNotValidException");
    }

}
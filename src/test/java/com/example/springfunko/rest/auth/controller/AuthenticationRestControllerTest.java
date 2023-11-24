package com.example.springfunko.rest.auth.controller;

import com.example.springfunko.rest.auth.dto.JwtAuthResponse;
import com.example.springfunko.rest.auth.dto.UserSignInRequest;
import com.example.springfunko.rest.auth.dto.UserSignUpRequest;
import com.example.springfunko.rest.auth.exceptions.AuthSignInInvalid;
import com.example.springfunko.rest.auth.exceptions.UserAuthNameOrEmailExisten;
import com.example.springfunko.rest.auth.exceptions.UserDiferentPassword;
import com.example.springfunko.rest.auth.services.authentication.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class AuthenticationRestControllerTest {
    private final String myEndpoint = "/api/auth";
    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    MockMvc mockMvc;
    @MockBean
    private AuthenticationService authenticationService;

    @Autowired
    public AuthenticationRestControllerTest(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    void signUp() throws Exception {
        UserSignUpRequest userSignUpRequest = new UserSignUpRequest("Test", "Test", "test", "test@test.com", "test12345", "test12345");
        JwtAuthResponse jwtAuthResponse = new JwtAuthResponse("token");

        var myLocalEndpoint = myEndpoint + "/signup";

        when(authenticationService.signUp(any(UserSignUpRequest.class))).thenReturn(jwtAuthResponse);

        MockHttpServletResponse response = mockMvc.perform(
                        post(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(userSignUpRequest)))
                .andReturn().getResponse();

        JwtAuthResponse res = mapper.readValue(response.getContentAsString(), JwtAuthResponse.class);

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals("token", res.getToken())
        );

        verify(authenticationService, times(1)).signUp(any(UserSignUpRequest.class));
    }

    @Test
    void signUpDifferentPasswords() {
        UserSignUpRequest request = new UserSignUpRequest();
        request.setUsername("testuser");
        request.setPassword("password");
        request.setPasswordCheck("password2");
        request.setEmail("test@test.com");
        request.setName("Test");
        request.setSurnames("User");


        when(authenticationService.signUp(any(UserSignUpRequest.class))).thenThrow(new UserDiferentPassword("The passwords are diferents"));

        assertThrows(UserDiferentPassword.class, () -> authenticationService.signUp(request));

        verify(authenticationService, times(1)).signUp(any(UserSignUpRequest.class));
    }

    @Test
    void signUpNameOrEmailExisten() {
        UserSignUpRequest request = new UserSignUpRequest();
        request.setUsername("testuser");
        request.setPassword("password");
        request.setPasswordCheck("password");
        request.setEmail("test@test.com");
        request.setName("Test");
        request.setSurnames("User");

        when(authenticationService.signUp(any(UserSignUpRequest.class))).thenThrow(new UserAuthNameOrEmailExisten("The name or email are existen"));

        assertThrows(UserAuthNameOrEmailExisten.class, () -> authenticationService.signUp(request));
    }

    @Test
    void signInBadRequestName() throws Exception {
        var myLocalEndpoint = myEndpoint + "/signup";

        UserSignUpRequest request = new UserSignUpRequest();
        request.setUsername("test");
        request.setPassword("password");
        request.setPasswordCheck("password");
        request.setEmail("test@test.com");
        request.setName("");
        request.setSurnames("User");

        MockHttpServletResponse response = mockMvc.perform(
                        post(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(request)))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(400, response.getStatus()),
                () -> assertEquals("{\"name\":\"Name cannot be empty\"}", response.getContentAsString())
        );
    }

    @Test
    void signUpBadRequestEmptyUsername() throws Exception {
        UserSignUpRequest request = new UserSignUpRequest();
        request.setUsername("");
        request.setPassword("password");
        request.setPasswordCheck("password");
        request.setEmail("test@test.com");
        request.setName("Test");
        request.setSurnames("User");

        var myLocalEndpoint = myEndpoint + "/signup";

        MockHttpServletResponse response = mockMvc.perform(
                        post(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(request)))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(400, response.getStatus()),
                () -> assertEquals("{\"username\":\"Username cannot be empty\"}", response.getContentAsString())
        );
    }

    @Test
    void signUpBadRequestEmptyEmail() throws Exception {
        UserSignUpRequest request = new UserSignUpRequest();
        request.setUsername("test");
        request.setPassword("password");
        request.setPasswordCheck("password");
        request.setEmail(null);
        request.setName("Test");
        request.setSurnames("User");

        var myLocalEndpoint = myEndpoint + "/signup";

        MockHttpServletResponse response = mockMvc.perform(
                        post(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(request)))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(400, response.getStatus()),
                () -> assertEquals("{\"email\":\"Email cannot be empty\"}", response.getContentAsString())
        );
    }

    @Test
    void signUpBadRequestInvalidEmail() throws Exception {
        UserSignUpRequest request = new UserSignUpRequest();
        request.setUsername("test");
        request.setPassword("password");
        request.setPasswordCheck("password");
        request.setEmail("f");
        request.setName("Test");
        request.setSurnames("User");

        var myLocalEndpoint = myEndpoint + "/signup";

        MockHttpServletResponse response = mockMvc.perform(
                        post(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(request)))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(400, response.getStatus()),
                () -> assertEquals("{\"email\":\"Email must be valid\"}", response.getContentAsString())
        );
    }

    @Test
    void signUpBadRequestPasswordsLenght() throws Exception {
        UserSignUpRequest request = new UserSignUpRequest();
        request.setUsername("test");
        request.setPassword("pepe");
        request.setPasswordCheck("password");
        request.setEmail("test@test.com");
        request.setName("Test");
        request.setSurnames("User");

        var myLocalEndpoint = myEndpoint + "/signup";

        MockHttpServletResponse response = mockMvc.perform(
                        post(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(request)))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(400, response.getStatus()),
                () -> assertEquals("{\"password\":\"Password must be at least 5 characters\"}", response.getContentAsString())
        );
    }

    @Test
    void signUpBadRequestPasswordsCheckLenght() throws Exception {
        UserSignUpRequest request = new UserSignUpRequest();
        request.setUsername("test");
        request.setPassword("password");
        request.setPasswordCheck("pepe");
        request.setEmail("test@test.com");
        request.setName("Test");
        request.setSurnames("User");

        var myLocalEndpoint = myEndpoint + "/signup";

        MockHttpServletResponse response = mockMvc.perform(
                        post(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(request)))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(400, response.getStatus()),
                () -> assertEquals("{\"passwordCheck\":\"Password check must be at least 5 characters\"}", response.getContentAsString())
        );
    }

    @Test
    void signIn() throws Exception {
        var userSignUpRequest = new UserSignUpRequest("Test", "Test", "test", "test@test.com", "test12345", "test12345");
        var jwtAuthResponse = new JwtAuthResponse("token");

        var myLocalEndpoint = myEndpoint + "/signin";

        when(authenticationService.signIn(any(UserSignInRequest.class))).thenReturn(jwtAuthResponse);

        MockHttpServletResponse response = mockMvc.perform(
                        post(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(userSignUpRequest)))
                .andReturn().getResponse();

        JwtAuthResponse res = mapper.readValue(response.getContentAsString(), JwtAuthResponse.class);
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals("token", res.getToken())
        );

        verify(authenticationService, times(1)).signIn(any(UserSignInRequest.class));
    }

    @Test
    void signIn_Invalid() {
        var myLocalEndpoint = myEndpoint + "/signin";

        UserSignInRequest request = new UserSignInRequest();
        request.setUsername("testuser");
        request.setPassword("<PASSWORD>");

        when(authenticationService.signIn(any(UserSignInRequest.class))).thenThrow(new AuthSignInInvalid("Usuario o contraseña incorrectos"));

        assertThrows(AuthSignInInvalid.class, () -> authenticationService.signIn(request));

        verify(authenticationService, times(1)).signIn(any(UserSignInRequest.class));
    }

    @Test
    void signIn_BadRequest_When_Username_Password_Empty_ShouldThrowException() throws Exception {

        var myLocalEndpoint = myEndpoint + "/signin";

        UserSignInRequest request = new UserSignInRequest();
        request.setUsername("");
        request.setPassword("");

        MockHttpServletResponse response = mockMvc.perform(
                        post(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(request)))
                .andReturn().getResponse();

        System.out.println(response.getContentAsString());

        assertAll(
                () -> assertEquals(400, response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("Username cannot be empty"))
        );
    }
}
package com.example.springfunko.rest.auth.services.authentication;

import com.example.springfunko.rest.auth.dto.JwtAuthResponse;
import com.example.springfunko.rest.auth.dto.UserSignInRequest;
import com.example.springfunko.rest.auth.dto.UserSignUpRequest;
import com.example.springfunko.rest.auth.exceptions.AuthSignInInvalid;
import com.example.springfunko.rest.auth.exceptions.UserAuthNameOrEmailExisten;
import com.example.springfunko.rest.auth.exceptions.UserDiferentPassword;
import com.example.springfunko.rest.auth.repositories.AuthUsersRepository;
import com.example.springfunko.rest.auth.services.jwt.JwtService;
import com.example.springfunko.rest.users.models.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {
    @Mock
    private AuthUsersRepository authUsersRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Test
    void signUp() {
        UserSignUpRequest request = new UserSignUpRequest();
        request.setUsername("testuser");
        request.setPassword("password");
        request.setPasswordCheck("password");
        request.setEmail("test@example.com");
        request.setName("Test");
        request.setSurnames("User");

        User userStored = new User();
        when(authUsersRepository.save(any(User.class))).thenReturn(userStored);

        String token = "test_token";
        when(jwtService.generateToken(userStored)).thenReturn(token);

        JwtAuthResponse response = authenticationService.signUp(request);

        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(token, response.getToken()),
                () -> verify(authUsersRepository, times(1)).save(any(User.class)),
                () -> verify(jwtService, times(1)).generateToken(userStored)
        );
    }

    @Test
    void signUpDifferentPassword() {
        UserSignUpRequest request = new UserSignUpRequest();
        request.setUsername("testuser");
        request.setPassword("password");
        request.setPasswordCheck("helo");
        request.setEmail("test@example.com");
        request.setName("Test");
        request.setSurnames("User");

        assertThrows(UserDiferentPassword.class, () -> authenticationService.signUp(request));
    }

    @Test
    void signUpNameOrEmailExisten() {
        UserSignUpRequest request = new UserSignUpRequest();
        request.setUsername("testuser");
        request.setPassword("password");
        request.setPasswordCheck("password");
        request.setEmail("test@example.com");
        request.setName("Test");
        request.setSurnames("User");

        when(authUsersRepository.save(any(User.class))).thenThrow(DataIntegrityViolationException.class);

        assertThrows(UserAuthNameOrEmailExisten.class, () -> authenticationService.signUp(request));
    }

    @Test
    void signIn() {
        UserSignInRequest request = new UserSignInRequest();
        request.setUsername("testuser");
        request.setPassword("password");

        User user = new User();
        when(authUsersRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(user));

        String token = "test_token";
        when(jwtService.generateToken(user)).thenReturn(token);

        JwtAuthResponse response = authenticationService.signIn(request);

        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(token, response.getToken()),
                () -> verify(authenticationManager, times(1)).authenticate(any()),
                () -> verify(authUsersRepository, times(1)).findByUsername(request.getUsername()),
                () -> verify(jwtService, times(1)).generateToken(user)
        );
    }

    @Test
    void signInNotFound() {
        UserSignInRequest request = new UserSignInRequest();
        request.setUsername("testuser");
        request.setPassword("password");

        when(authUsersRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());

        assertThrows(AuthSignInInvalid.class, () -> authenticationService.signIn(request));
    }
}
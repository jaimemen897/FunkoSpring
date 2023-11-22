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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.example.springfunko.rest.users.models.Role.USER;

@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
    private final AuthUsersRepository authUsersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthenticationServiceImpl(AuthUsersRepository authUsersRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.authUsersRepository = authUsersRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public JwtAuthResponse signUp(UserSignUpRequest request) {
        log.info("Signing up user: {}", request);
        if (request.getPassword().contentEquals(request.getPasswordCheck())) {
            User user = User.builder()
                    .username(request.getUsername())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .email(request.getEmail())
                    .name(request.getName())
                    .surnames(request.getSurnames())
                    .roles(Stream.of(USER).collect(Collectors.toSet()))
                    .build();
            try {
                var userStored = authUsersRepository.save(user);
                return JwtAuthResponse.builder().token(jwtService.generateToken(userStored)).build();
            } catch (DataIntegrityViolationException ex) {
                throw new UserAuthNameOrEmailExisten("The user with name " + request.getUsername() + " or email " + request.getEmail() + " already exists");
            }
        } else {
            throw new UserDiferentPassword("The password does not match");
        }
    }

    @Override
    public JwtAuthResponse signIn(UserSignInRequest request) {
        log.info("Signing in user: {}", request);
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        var user = authUsersRepository.findByUsername(request.getUsername()).orElseThrow(() -> new AuthSignInInvalid("User or password invalid"));
        var jwt = jwtService.generateToken(user);
        return JwtAuthResponse.builder().token(jwt).build();
    }
}

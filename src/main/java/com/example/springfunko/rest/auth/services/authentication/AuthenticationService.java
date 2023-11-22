package com.example.springfunko.rest.auth.services.authentication;

import com.example.springfunko.rest.auth.dto.JwtAuthResponse;
import com.example.springfunko.rest.auth.dto.UserSignInRequest;
import com.example.springfunko.rest.auth.dto.UserSignUpRequest;

public interface AuthenticationService {
    JwtAuthResponse signUp(UserSignUpRequest request);

    JwtAuthResponse signIn(UserSignInRequest request);
}

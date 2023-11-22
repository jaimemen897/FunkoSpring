package com.example.springfunko.rest.auth.services.authentication;

import com.example.springfunko.rest.auth.dto.UserSignInRequest;
import com.example.springfunko.rest.auth.dto.UserSignUpRequest;

public interface AuthenticationService {
    JwtAuthReponse signUp(UserSignUpRequest request);

    JwtAuthReponse signIn(UserSignInRequest request);
}

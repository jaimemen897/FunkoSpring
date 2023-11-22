package com.example.springfunko.rest.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSignUpRequest {

    @NotBlank(message = "Name cannot be empty")
    private String name;

    @NotBlank(message = "Surnames cannot be empty")
    private String surnames;

    @NotBlank(message = "Username cannot be empty")
    private String username;

    @Email(regexp = ".*@.*\\..*", message = "Email must be valid")
    @NotBlank(message = "Email cannot be empty")
    private String email;

    @NotBlank(message = "Password cannot be empty")
    @Length(min = 5, message = "Password must be at least 5 characters")
    private String password;

    @NotBlank(message = "Password check cannot be empty")
    @Length(min = 5, message = "Password check must be at least 5 characters")
    private String passwordCheck;

}
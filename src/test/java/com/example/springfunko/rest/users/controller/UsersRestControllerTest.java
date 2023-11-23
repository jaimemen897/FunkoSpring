package com.example.springfunko.rest.users.controller;

import com.example.springfunko.rest.users.dto.UserInfoResponse;
import com.example.springfunko.rest.users.dto.UserRequest;
import com.example.springfunko.rest.users.dto.UserResponse;
import com.example.springfunko.rest.users.models.User;
import com.example.springfunko.rest.users.services.UsersService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@WithMockUser(username = "admin", password = "admin", roles = {"ADMIN", "USER"})
class UsersRestControllerTest {

    private final UserRequest userRequest = UserRequest.builder()
            .name("test")
            .surnames("test")
            .password("test1234")
            .username("test")
            .email("test@test.com")
            .build();
    private final User user = User.builder().id(99L)
            .name("test")
            .surnames("test")
            .password("test1234")
            .username("test")
            .email("test@test.com")
            .build();
    private final UserResponse userResponse = UserResponse.builder()
            .id(99L)
            .name("test")
            .surnames("test")
            .username("test")
            .email("test@test.com")
            .build();
    private final UserInfoResponse userInfoResponse = UserInfoResponse.builder()
            .id(99L)
            .name("test")
            .surnames("test")
            .username("test")
            .email("test@test.com")
            .build();

    private final String myEndpoint = "/api/users";
    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    MockMvc mockMvc;

    @MockBean
    private UsersService usersService;

    @Autowired
    public UsersRestControllerTest(UsersService UsersService) {
        this.usersService = UsersService;
        mapper.registerModule(new JavaTimeModule());
    }


}
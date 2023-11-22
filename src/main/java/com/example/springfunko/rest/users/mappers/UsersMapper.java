package com.example.springfunko.rest.users.mappers;

import com.example.springfunko.rest.users.dto.UserInfoResponse;
import com.example.springfunko.rest.users.dto.UserRequest;
import com.example.springfunko.rest.users.dto.UserResponse;
import com.example.springfunko.rest.users.models.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UsersMapper {
    public User toUser(UserRequest request) {
        return User.builder()
                .name(request.getName())
                .surnames(request.getSurnames())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .roles(request.getRoles())
                .isDeleted(request.getIsDeleted())
                .build();
    }

    public User toUser(UserRequest request, Long id) {
        return User.builder()
                .id(id)
                .name(request.getName())
                .surnames(request.getSurnames())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .roles(request.getRoles())
                .isDeleted(request.getIsDeleted())
                .build();
    }

    public UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .surnames(user.getSurnames())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles())
                .isDeleted(user.getIsDeleted())
                .build();
    }

    public UserInfoResponse toUserInfoResponse(User user, List<String> orders) {
        return UserInfoResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .surnames(user.getSurnames())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles())
                .isDeleted(user.getIsDeleted())
                .orders(orders)
                .build();
    }
}
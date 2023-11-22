package com.example.springfunko.rest.auth.services.users;

import com.example.springfunko.rest.auth.repositories.AuthUsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service("userDetailsService")
public class AuthUsersServiceImpl implements AuthUsersService{
    private final AuthUsersRepository authUsersRepository;

    @Autowired
    public AuthUsersServiceImpl(AuthUsersRepository authUsersRepository) {
        this.authUsersRepository = authUsersRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UserNotFound{
        return authUsersRepository.findByUsername(username).orElseThrow(() -> new UserNotFound("User with username " + username + " not found"));
    }
}

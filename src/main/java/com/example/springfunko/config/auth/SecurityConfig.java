package com.example.springfunko.config.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request.requestMatchers("/api/storage/**").permitAll())
                .authorizeHttpRequests(request -> request.requestMatchers("/css/**").permitAll())
                .authorizeHttpRequests(request -> request.requestMatchers("/error/**").permitAll())
                .authorizeHttpRequests(request -> request.requestMatchers("/funkos/**").permitAll())
                .authorizeHttpRequests(request -> request.anyRequest().authenticated());
        return http.build();
    }
}

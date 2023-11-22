package com.example.springfunko.config.auth;

import com.example.springfunko.rest.auth.services.jwt.JwtService;
import com.example.springfunko.rest.auth.services.users.AuthUsersService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final AuthUsersService authUsersService;

    @Autowired
    public JwtAuthenticationFilter(JwtService jwtService, AuthUsersService authUsersService) {
        this.jwtService = jwtService;
        this.authUsersService = authUsersService;
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        log.info("Starting authentication filter");
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        UserDetails userDetails = null;
        String userName = null;

        if (!StringUtils.hasText(authHeader) || !StringUtils.startsWithIgnoreCase(authHeader, "Bearer ")) {
            log.info("No JWT token found in request headers");
            filterChain.doFilter(request, response);
            return;
        }

        log.info("JWT token found in request headers");
        jwt = authHeader.substring(7);

        try {
            userName = jwtService.extractUserName(jwt);
        } catch (Exception e) {
            log.error("Error while getting user details from JWT token", e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
            return;
        }
        if (StringUtils.hasText(userName)
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            log.info("Checking user and token validity");
            try {
                userDetails = authUsersService.loadUserByUsername(userName);
            } catch (Exception e) {
                log.info("User not found: {}", userName);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not authorized");
                return;
            }
            authUsersService.loadUserByUsername(userName);
            log.info("User found: {}", userDetails);
            if (jwtService.isTokenValid(jwt, userDetails)) {
                log.info("JWT token is valid");
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                context.setAuthentication(authToken);
                SecurityContextHolder.setContext(context);
            }
        }
        filterChain.doFilter(request, response);
    }
}

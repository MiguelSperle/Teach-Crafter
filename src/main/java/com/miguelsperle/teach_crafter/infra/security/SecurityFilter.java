package com.miguelsperle.teach_crafter.infra.security;

import com.miguelsperle.teach_crafter.modules.users.entities.users.UsersEntity;
import com.miguelsperle.teach_crafter.modules.users.repositories.UsersRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class SecurityFilter extends OncePerRequestFilter {
    private final TokenService tokenService;
    private final UsersRepository usersRepository;

    @Qualifier("customAuthenticationEntryPoint")
    private final AuthenticationEntryPoint authenticationEntryPoint;

    public SecurityFilter(final TokenService tokenService, final UsersRepository usersRepository, final AuthenticationEntryPoint authenticationEntryPoint) {
        this.tokenService = tokenService;
        this.usersRepository = usersRepository;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = this.recoverToken(request);

        if (token != null) {
            try {
                String userId = this.tokenService.validateToken(token);
                UsersEntity user = this.usersRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
                var authorities = Collections.singletonList(new SimpleGrantedAuthority(user.getRole()));
                var authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (UsernameNotFoundException exception) {
                authenticationEntryPoint.commence(request, response, exception);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null) return null;

        return authorizationHeader.replace("Bearer ", "");
    }
}
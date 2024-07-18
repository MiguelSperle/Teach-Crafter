package com.miguelsperle.teach_crafter.infra.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miguelsperle.teach_crafter.dtos.general.CustomAuthenticationEntryResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;

@Component("customAuthenticationEntryPoint")
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private static final String MISSING_AUTHORIZATION_TOKEN_MESSAGE = "Authorization token missing in request header";
    private static final String INVALID_AUTHORIZATION_TOKEN_MESSAGE = "Invalid authorization token";
    private static final String DEFAULT_AUTHENTICATION_ERROR_MESSAGE = "Authentication failed";

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authenticationException) throws IOException {
        String message = determineAuthErrorMessage(request, authenticationException);

        CustomAuthenticationEntryResponseDTO customAuthenticationEntryResponseDTO =
                new CustomAuthenticationEntryResponseDTO(message, HttpStatus.FORBIDDEN.value());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        OutputStream responseStream = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(responseStream, customAuthenticationEntryResponseDTO);
        responseStream.flush();
    }

    private String determineAuthErrorMessage(HttpServletRequest request, AuthenticationException authenticationException) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null) {
            return MISSING_AUTHORIZATION_TOKEN_MESSAGE;
        } else if (authenticationException instanceof UsernameNotFoundException) {
            return INVALID_AUTHORIZATION_TOKEN_MESSAGE;
        }

        return DEFAULT_AUTHENTICATION_ERROR_MESSAGE; // CASE OCCUR AN ERROR DIFFERENT OF THE CONDITIONALS THAT I PUT, THIS MESSAGE WILL BE RETURNED
    }
}
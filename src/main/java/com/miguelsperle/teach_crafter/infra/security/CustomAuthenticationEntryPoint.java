package com.miguelsperle.teach_crafter.infra.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miguelsperle.teach_crafter.dtos.general.CustomAuthenticationEntryResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;

@Component("customAuthenticationEntryPoint")
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private static final String MISSING_AUTHORIZATION_TOKEN_MESSAGE = "Authorization token missing in request header";

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        CustomAuthenticationEntryResponseDTO customAuthenticationEntryResponseDTO =
                new CustomAuthenticationEntryResponseDTO(MISSING_AUTHORIZATION_TOKEN_MESSAGE, HttpStatus.FORBIDDEN.value());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        OutputStream responseStream = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(responseStream, customAuthenticationEntryResponseDTO);
        responseStream.flush();
    }
}
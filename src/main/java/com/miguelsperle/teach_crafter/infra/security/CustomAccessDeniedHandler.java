package com.miguelsperle.teach_crafter.infra.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miguelsperle.teach_crafter.dtos.general.CustomAccessDeniedHandlerResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;

@Component("customAccessDeniedHandler")
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private static final String RESOURCE_ACCESS_RESTRICTED_MESSAGE = "Access to this resource is restricted";

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        CustomAccessDeniedHandlerResponseDTO accessDeniedResponseDTO = new CustomAccessDeniedHandlerResponseDTO(RESOURCE_ACCESS_RESTRICTED_MESSAGE, HttpStatus.FORBIDDEN.value());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        OutputStream responseStream = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(responseStream, accessDeniedResponseDTO);
        responseStream.flush();
    }
}

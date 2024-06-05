package com.miguelsperle.teach_crafter.modules.users.controllers;


import com.miguelsperle.teach_crafter.modules.users.dtos.authorization.AuthorizationResponseDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.authorization.AuthorizationUsersDTO;
import com.miguelsperle.teach_crafter.modules.users.services.AuthorizationUsersService;
import com.miguelsperle.teach_crafter.modules.users.services.RequestFieldValidationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthorizationUsersController {
    private final AuthorizationUsersService authorizationUsersService;
    private final RequestFieldValidationService requestFieldValidationService;

    public AuthorizationUsersController(final AuthorizationUsersService authorizationUsersService, final RequestFieldValidationService requestFieldValidationService) {
        this.authorizationUsersService = authorizationUsersService;
        this.requestFieldValidationService = requestFieldValidationService;
    }

    @PostMapping("/login")
    public ResponseEntity<Object> authorizationUsers(@RequestBody @Valid AuthorizationUsersDTO authorizationUsersDTO, BindingResult bindingResult) {
        this.requestFieldValidationService.validationErrors(bindingResult);

        String token = this.authorizationUsersService.authorizationUsers(authorizationUsersDTO);

        return ResponseEntity.ok().body(new AuthorizationResponseDTO(token, HttpStatus.OK.value()));
    }
}

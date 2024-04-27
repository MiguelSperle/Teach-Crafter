package com.miguelsperle.teach_crafter.modules.users.controllers;

import com.miguelsperle.teach_crafter.dtos.general.MessageResponseDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.auth.AuthorizationResponseDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.auth.AuthorizationUsersDTO;
import com.miguelsperle.teach_crafter.modules.users.services.AuthorizationUsersService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthorizationUsersController {
    private final AuthorizationUsersService authorizationUsersService;
    @PostMapping("/login")
    public ResponseEntity<Object> authorizationUsers(@RequestBody @Valid AuthorizationUsersDTO authorizationUsersDTO, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(new MessageResponseDTO(String.valueOf(bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .findFirst().get()), HttpStatus.BAD_REQUEST.value()));
        }

        String token = this.authorizationUsersService.authorizationUsers(authorizationUsersDTO);

        return ResponseEntity.ok().body(new AuthorizationResponseDTO(token, HttpStatus.OK.value()));
    }
}

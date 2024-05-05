package com.miguelsperle.teach_crafter.modules.users.controllers;

import com.miguelsperle.teach_crafter.dtos.general.MessageResponseDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.cloudinary.UploadImageModelDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.users.*;
import com.miguelsperle.teach_crafter.modules.users.services.UsersService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UsersController {
    private final UsersService usersService;

    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@RequestBody @Valid CreateUserDTO createUserDTO, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(new MessageResponseDTO(String.valueOf(bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                            .findFirst().get()), HttpStatus.BAD_REQUEST.value()));
        }

        this.usersService.createUser(createUserDTO);

       return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponseDTO("Account created successfully", HttpStatus.CREATED.value()));
    }



    @PutMapping("/update-name")
    public ResponseEntity<Object> updateNameUser(@RequestBody @Valid UpdateNameUserDTO updateNameUserDTO, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(new MessageResponseDTO(String.valueOf(bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .findFirst().get()), HttpStatus.BAD_REQUEST.value()));
        }

        this.usersService.updateNameUser(updateNameUserDTO);

        return ResponseEntity.ok()
                .body(new MessageResponseDTO("Name updated successfully", HttpStatus.OK.value()));
    }

    @PutMapping("/update-username")
    public ResponseEntity<Object> updateUsernameUser(@RequestBody @Valid UpdateUsernameUserDTO updateUsernameUserDTO, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(new MessageResponseDTO(String.valueOf(bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .findFirst().get()), HttpStatus.BAD_REQUEST.value()));
        }

        this.usersService.updateUsernameUser(updateUsernameUserDTO);

        return ResponseEntity.ok()
                .body(new MessageResponseDTO("Username updated successfully", HttpStatus.OK.value()));
    }

    @PutMapping("/update-email")
    public ResponseEntity<Object> updateEmailUser(@RequestBody @Valid UpdateEmailUserDTO updateEmailUserDTO, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(new MessageResponseDTO(String.valueOf(bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .findFirst().get()), HttpStatus.BAD_REQUEST.value()));
        }

        this.usersService.updateEmailUser(updateEmailUserDTO);

        return ResponseEntity.ok()
                .body(new MessageResponseDTO("Email updated successfully", HttpStatus.OK.value()));
    }

    @PutMapping("/update-password")
    public ResponseEntity<Object> updatePasswordUserLogged(@RequestBody @Valid UpdatePasswordUserLoggedDTO updatePasswordUserLoggedDTO, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(new MessageResponseDTO(String.valueOf(bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .findFirst().get()), HttpStatus.BAD_REQUEST.value()));
        }

        this.usersService.updatePasswordUserLogged(updatePasswordUserLoggedDTO);

        return ResponseEntity.ok()
                .body(new MessageResponseDTO("Password updated successfully", HttpStatus.OK.value()));
    }

    @PutMapping("/update-image")
    public ResponseEntity<Object> updateImageUser(@Valid UploadImageModelDTO uploadImageModelDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(new MessageResponseDTO(String.valueOf(bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .findFirst().get()), HttpStatus.BAD_REQUEST.value()));
        }

        this.usersService.updateImageUser(uploadImageModelDTO);

        return ResponseEntity.ok()
                .body(new MessageResponseDTO("Image updated successfully", HttpStatus.OK.value()));
    }
}

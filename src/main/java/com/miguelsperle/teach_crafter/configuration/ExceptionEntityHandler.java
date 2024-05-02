package com.miguelsperle.teach_crafter.configuration;

import com.miguelsperle.teach_crafter.dtos.general.MessageResponseDTO;
import com.miguelsperle.teach_crafter.modules.users.entities.passwordResetToken.exceptions.MakeTheProcessAgainException;
import com.miguelsperle.teach_crafter.modules.users.entities.passwordResetToken.exceptions.PasswordResetTokenRecoveryIsNotExpiredException;
import com.miguelsperle.teach_crafter.modules.users.entities.passwordResetToken.exceptions.PasswordResetTokenNotFoundException;
import com.miguelsperle.teach_crafter.modules.users.entities.users.exceptions.PasswordNotMatchUserException;
import com.miguelsperle.teach_crafter.modules.users.entities.users.exceptions.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionEntityHandler {
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Object> handleUserAlreadyExists(UserAlreadyExistsException exception){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new MessageResponseDTO(exception.getMessage(), HttpStatus.CONFLICT.value()));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFound(UsernameNotFoundException exception){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponseDTO(exception.getMessage(), HttpStatus.UNAUTHORIZED.value()));
    }

    @ExceptionHandler(PasswordNotMatchUserException.class)
    public ResponseEntity<Object> handlePasswordNotMatchUsers(PasswordNotMatchUserException exception){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponseDTO(exception.getMessage(), HttpStatus.UNAUTHORIZED.value()));
    }

    @ExceptionHandler(PasswordResetTokenNotFoundException.class)
    public ResponseEntity<Object> handlePasswordResetTokenNotFound(PasswordResetTokenNotFoundException exception){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponseDTO(exception.getMessage(), HttpStatus.NOT_FOUND.value()));
    }

    @ExceptionHandler(MakeTheProcessAgainException.class)
    public ResponseEntity<Object> handleMakeTheProcessAgain(MakeTheProcessAgainException exception){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponseDTO(exception.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(PasswordResetTokenRecoveryIsNotExpiredException.class)
    public ResponseEntity<Object> handlePasswordResetTokenRecoveryIsNotExpired(PasswordResetTokenRecoveryIsNotExpiredException exception){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new MessageResponseDTO(exception.getMessage(), HttpStatus.CONFLICT.value()));
    }
}

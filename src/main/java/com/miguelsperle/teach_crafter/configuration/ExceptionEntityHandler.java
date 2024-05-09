package com.miguelsperle.teach_crafter.configuration;

import com.miguelsperle.teach_crafter.configuration.exceptions.general.MissingFieldException;
import com.miguelsperle.teach_crafter.dtos.general.MessageResponseDTO;
import com.miguelsperle.teach_crafter.modules.users.entities.passwordResetToken.exceptions.ActivePasswordResetTokenException;
import com.miguelsperle.teach_crafter.modules.users.entities.passwordResetToken.exceptions.ExpiredPasswordResetTokenException;
import com.miguelsperle.teach_crafter.modules.users.entities.passwordResetToken.exceptions.PasswordResetTokenNotFoundException;
import com.miguelsperle.teach_crafter.modules.users.entities.users.exceptions.PasswordNotMatchUserException;
import com.miguelsperle.teach_crafter.modules.users.entities.users.exceptions.UserAlreadyExistsException;
import com.miguelsperle.teach_crafter.modules.users.entities.users.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionEntityHandler {
    @ExceptionHandler(MissingFieldException.class)
    public ResponseEntity<Object> handleMissingFieldException(MissingFieldException exception){
        return ResponseEntity.badRequest().body(new MessageResponseDTO(exception.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Object> handleUserAlreadyExistsException(UserAlreadyExistsException exception){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new MessageResponseDTO(exception.getMessage(), HttpStatus.CONFLICT.value()));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Object> handleUsernameNotFoundException(UsernameNotFoundException exception){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponseDTO(exception.getMessage(), HttpStatus.UNAUTHORIZED.value()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException exception){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponseDTO(exception.getMessage(), HttpStatus.NOT_FOUND.value()));
    }

    @ExceptionHandler(PasswordNotMatchUserException.class)
    public ResponseEntity<Object> handlePasswordNotMatchUsersException(PasswordNotMatchUserException exception){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponseDTO(exception.getMessage(), HttpStatus.UNAUTHORIZED.value()));
    }

    @ExceptionHandler(PasswordResetTokenNotFoundException.class)
    public ResponseEntity<Object> handlePasswordResetTokenNotFoundException(PasswordResetTokenNotFoundException exception){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponseDTO(exception.getMessage(), HttpStatus.NOT_FOUND.value()));
    }

    @ExceptionHandler(ExpiredPasswordResetTokenException.class)
    public ResponseEntity<Object> handleExpiredPasswordResetTokenException(ExpiredPasswordResetTokenException exception){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponseDTO(exception.getMessage(), HttpStatus.FORBIDDEN.value()));
    }

    @ExceptionHandler(ActivePasswordResetTokenException.class) // TESTE
    public ResponseEntity<Object> handleActivePasswordResetTokenException(ActivePasswordResetTokenException exception){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new MessageResponseDTO(exception.getMessage(), HttpStatus.CONFLICT.value()));
    }
}

package com.miguelsperle.teach_crafter.configuration;

import com.miguelsperle.teach_crafter.dtos.general.MessageResponseDTO;
import com.miguelsperle.teach_crafter.exceptions.general.MissingFieldException;
import com.miguelsperle.teach_crafter.exceptions.general.TaskDeniedException;
import com.miguelsperle.teach_crafter.modules.users.entities.courses.exceptions.CourseNotFoundException;
import com.miguelsperle.teach_crafter.modules.users.entities.coursesContents.exceptions.CourseContentNotFoundException;
import com.miguelsperle.teach_crafter.modules.users.entities.coursesContents.exceptions.InvalidReleaseDateException;
import com.miguelsperle.teach_crafter.modules.users.entities.passwordResetTokens.exceptions.ActivePasswordResetTokenException;
import com.miguelsperle.teach_crafter.modules.users.entities.passwordResetTokens.exceptions.ExpiredPasswordResetTokenException;
import com.miguelsperle.teach_crafter.modules.users.entities.passwordResetTokens.exceptions.PasswordResetTokenNotFoundException;
import com.miguelsperle.teach_crafter.modules.users.entities.enrollments.exceptions.EnrollmentAlreadyExistsException;
import com.miguelsperle.teach_crafter.modules.users.entities.courses.exceptions.NoAvailableSpotsException;
import com.miguelsperle.teach_crafter.modules.users.entities.enrollments.exceptions.EnrollmentNotFoundException;
import com.miguelsperle.teach_crafter.modules.users.entities.users.exceptions.UserPasswordMismatchException;
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
    public ResponseEntity<Object> handleMissingFieldException(MissingFieldException exception) {
        return ResponseEntity.badRequest().body(new MessageResponseDTO(exception.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Object> handleUserAlreadyExistsException(UserAlreadyExistsException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new MessageResponseDTO(exception.getMessage(), HttpStatus.CONFLICT.value()));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Object> handleUsernameNotFoundException(UsernameNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponseDTO(exception.getMessage(), HttpStatus.UNAUTHORIZED.value()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponseDTO(exception.getMessage(), HttpStatus.NOT_FOUND.value()));
    }

    @ExceptionHandler(UserPasswordMismatchException.class)
    public ResponseEntity<Object> handleUserPasswordMismatchException(UserPasswordMismatchException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponseDTO(exception.getMessage(), HttpStatus.UNAUTHORIZED.value()));
    }

    @ExceptionHandler(PasswordResetTokenNotFoundException.class)
    public ResponseEntity<Object> handlePasswordResetTokenNotFoundException(PasswordResetTokenNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponseDTO(exception.getMessage(), HttpStatus.NOT_FOUND.value()));
    }

    @ExceptionHandler(ExpiredPasswordResetTokenException.class)
    public ResponseEntity<Object> handleExpiredPasswordResetTokenException(ExpiredPasswordResetTokenException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponseDTO(exception.getMessage(), HttpStatus.FORBIDDEN.value()));
    }

    @ExceptionHandler(ActivePasswordResetTokenException.class)
    public ResponseEntity<Object> handleActivePasswordResetTokenException(ActivePasswordResetTokenException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new MessageResponseDTO(exception.getMessage(), HttpStatus.CONFLICT.value()));
    }

    @ExceptionHandler(CourseNotFoundException.class)
    public ResponseEntity<Object> handleCourseNotFoundException(CourseNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponseDTO(exception.getMessage(), HttpStatus.NOT_FOUND.value()));
    }

    @ExceptionHandler(TaskDeniedException.class)
    public ResponseEntity<Object> handleTaskDeniedException(TaskDeniedException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponseDTO(exception.getMessage(), HttpStatus.FORBIDDEN.value()));
    }

    @ExceptionHandler(EnrollmentAlreadyExistsException.class)
    public ResponseEntity<Object> handleEnrollmentAlreadyExistsException(EnrollmentAlreadyExistsException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new MessageResponseDTO(exception.getMessage(), HttpStatus.CONFLICT.value()));
    }

    @ExceptionHandler(NoAvailableSpotsException.class)
    public ResponseEntity<Object> handleNoAvailableSpotsException(NoAvailableSpotsException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new MessageResponseDTO(exception.getMessage(), HttpStatus.CONFLICT.value()));
    }

    @ExceptionHandler(EnrollmentNotFoundException.class)
    public ResponseEntity<Object> handleEnrollmentNotFoundException(EnrollmentNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponseDTO(exception.getMessage(), HttpStatus.NOT_FOUND.value()));
    }

    @ExceptionHandler(InvalidReleaseDateException.class)
    public ResponseEntity<Object> handleInvalidReleaseDateException(InvalidReleaseDateException exception) {
        return ResponseEntity.badRequest().body(new MessageResponseDTO(exception.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(CourseContentNotFoundException.class)
    public ResponseEntity<Object> handleCourseContentNotFoundException(CourseContentNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponseDTO(exception.getMessage(), HttpStatus.NOT_FOUND.value()));
    }
}

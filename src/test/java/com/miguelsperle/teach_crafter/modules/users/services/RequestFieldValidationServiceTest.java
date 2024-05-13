package com.miguelsperle.teach_crafter.modules.users.services;

import com.miguelsperle.teach_crafter.exceptions.general.MissingFieldException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RequestFieldValidationServiceTest {
    @InjectMocks
    private RequestFieldValidationService requestFieldValidationService;

    @Test
    @DisplayName("Should be able to throw an exception for empty fields")
    public void should_be_able_to_throw_an_exception_for_empty_fields(){
        BindingResult bindingResult = mock(BindingResult.class);

        when(bindingResult.hasErrors()).thenReturn(true);

        ObjectError error = new ObjectError("FieldName", "Field is required");
        when(bindingResult.getAllErrors()).thenReturn(List.of(error));

        MissingFieldException exception = assertThrows(MissingFieldException.class, () -> {
            this.requestFieldValidationService.validationErrors(bindingResult);
        });

        String resultMessage = "Field is required";

        assertEquals(exception.getMessage(), resultMessage);
    }
}

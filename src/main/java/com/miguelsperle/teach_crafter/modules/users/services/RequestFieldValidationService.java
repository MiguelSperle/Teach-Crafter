package com.miguelsperle.teach_crafter.modules.users.services;

import com.miguelsperle.teach_crafter.exceptions.general.MissingFieldException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Service
public class RequestFieldValidationService {

    public void validationErrors(BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .findFirst()
                    .orElse("Unknown error");

            throw new MissingFieldException(errorMessage);
        }
    }
}

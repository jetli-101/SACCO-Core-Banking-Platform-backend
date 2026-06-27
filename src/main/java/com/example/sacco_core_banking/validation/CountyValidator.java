package com.example.sacco_core_banking.validation;

import com.example.sacco_core_banking.classes.KenyaCounties;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CountyValidator implements ConstraintValidator<ValidCounty, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return KenyaCounties.isValid(value);
    }
}

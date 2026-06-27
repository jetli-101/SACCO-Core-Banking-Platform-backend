package com.example.sacco_core_banking.validation;

import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class KraPinValidator implements ConstraintValidator<KraPin, String> {

    private static final Pattern PATTERN = Pattern.compile("^[A-Za-z]\\d{9}[A-Za-z]$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return false;
        }
        return PATTERN.matcher(value.trim()).matches();
    }
}

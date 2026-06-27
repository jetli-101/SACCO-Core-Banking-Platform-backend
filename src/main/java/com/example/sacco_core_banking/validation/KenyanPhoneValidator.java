package com.example.sacco_core_banking.validation;

import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class KenyanPhoneValidator implements ConstraintValidator<KenyanPhone, String> {

    private static final Pattern PATTERN = Pattern.compile("^(?:\\+254|254|0)(7\\d{8}|1\\d{8})$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return false;
        }
        return PATTERN.matcher(value.trim()).matches();
    }
}

package com.example.sacco_core_banking.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Kenyan national ID numbers are 7-8 digits (older IDs are 7, current issuance is 8).
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NationalIdValidator.class)
public @interface NationalId {
    String message() default "National ID must be 7-8 digits";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

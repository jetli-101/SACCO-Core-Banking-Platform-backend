package com.example.sacco_core_banking.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * KRA PIN format issued by the Kenya Revenue Authority, e.g. A001234567Z
 * (one letter, nine digits, one letter).
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = KraPinValidator.class)
public @interface KraPin {
    String message() default "KRA PIN must match the format A001234567Z";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

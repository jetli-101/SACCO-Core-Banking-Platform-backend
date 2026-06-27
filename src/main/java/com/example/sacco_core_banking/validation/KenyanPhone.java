package com.example.sacco_core_banking.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Accepts Kenyan mobile numbers in either local (07XXXXXXXX / 01XXXXXXXX) or
 * international (+2547XXXXXXXX / +2541XXXXXXXX) form.
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = KenyanPhoneValidator.class)
public @interface KenyanPhone {
    String message() default "Phone number must be a valid Kenyan number, e.g. 0712345678 or +254712345678";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

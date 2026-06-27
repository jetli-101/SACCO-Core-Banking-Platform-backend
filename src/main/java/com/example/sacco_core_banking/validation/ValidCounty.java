package com.example.sacco_core_banking.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Restricts a field to one of Kenya's 47 gazetted counties (see KenyaCounties), case
 * insensitive, so reporting/segmentation by county doesn't fragment on free-text typos.
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CountyValidator.class)
public @interface ValidCounty {
    String message() default "County must be one of Kenya's 47 counties";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

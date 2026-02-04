package com.ipaccessanalyzer.presentation.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = NotEmptyFileValidator.class)
@Target( ElementType.FIELD )
@Retention(RetentionPolicy.RUNTIME)
public @interface NotEmptyFile {
	String message() default "파일을 선택해주세요.";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}

package me.bang9.api.global.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidNicknameValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidNickname {
    String message() default "Nickname must start with a letter and contain only letters, numbers, periods, and underscores (up to 20 characters)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
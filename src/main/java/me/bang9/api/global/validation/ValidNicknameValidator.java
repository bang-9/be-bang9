package me.bang9.api.global.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidNicknameValidator implements ConstraintValidator<ValidNickname, String> {

    private static final String NICKNAME_PATTERN = 
        "^[a-zA-Z][a-zA-Z0-9._]{0,19}$";

    @Override
    public void initialize(ValidNickname constraintAnnotation) {
        // No initialization required
    }

    @Override
    public boolean isValid(String nickname, ConstraintValidatorContext context) {
        if (nickname == null) {
            return false;
        }
        
        return nickname.matches(NICKNAME_PATTERN);
    }
}
package br.com.zup.orange.validator;

import javax.transaction.Transactional;
import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {IsValidImageValidator.class})
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Transactional
public @interface IsValidImage {
    String message() default "{_is_not_valid_image}";
    Class<?> domainClass();
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
package br.com.zup.orange.validator;

import javax.transaction.Transactional;
import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {UniqueValueValidator.class})
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Transactional
public @interface UniqueValue {
    String message() default "{_duplicated_field}";
    String fieldName();
    Class<?> domainClass();
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
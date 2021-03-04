package br.com.zup.orange.validator;


import org.springframework.util.Assert;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class IsValidImageValidator implements ConstraintValidator<IsValidImage, Object> {

    private Class<?> klass;
    
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void initialize(IsValidImage params) {
        klass = params.domainClass();
    }

    @Override
    @Transactional
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
   	
        String link = (String) value;
        
        String imageExtension = link.substring(link.length()-3, link.length()).trim();
        
        //We only accept PNG images.
        if(imageExtension.equals("png")) {
        	return true;
        }
        return false;
    }


}
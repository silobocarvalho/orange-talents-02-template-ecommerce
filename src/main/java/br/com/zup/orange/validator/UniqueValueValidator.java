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

public class UniqueValueValidator implements ConstraintValidator<UniqueValue, Object> {

    private String domainAttribute;
    private Class<?> klass;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void initialize(UniqueValue params) {
        domainAttribute = params.fieldName();
        klass = params.domainClass();
    }

    @Override
    @Transactional
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
   	
        Query query = entityManager.createQuery("select 1 from " + klass.getName() + " where " + domainAttribute + "= :value");
        query.setParameter("value", value);
        List<?> list = query.getResultList();
        Assert.state(list.size() <= 1, "More than one "+klass+" atribute was found "+ domainAttribute+ " = "+ value);

        return list.isEmpty();
    }


}
package es.caib.invoices.validator;

import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

public class ObjectValidatorImpl implements ObjectValidator{

	private Validator validator;
	
	//Constructor
	public ObjectValidatorImpl() {
		ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
		this.validator = validatorFactory.getValidator();
	}
	
	@Override
	public <T> void validateObject(T object) throws ValidationException {
		Set<ConstraintViolation<T>> violations = validator.validate(object);
		if(violations != null && !violations.isEmpty()) {
			String mensaje = 
					violations.stream().map(failure -> failure.getMessage()).collect(Collectors.joining(";"));
				
			throw new ValidationException(mensaje);
		}
		
	}

}

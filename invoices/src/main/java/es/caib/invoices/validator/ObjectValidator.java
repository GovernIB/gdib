package es.caib.invoices.validator;

import javax.validation.ValidationException;

public interface ObjectValidator {

	public <T> void validateObject(T Object) throws ValidationException;
}

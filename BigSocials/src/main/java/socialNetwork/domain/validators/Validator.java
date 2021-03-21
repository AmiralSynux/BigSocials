package socialNetwork.domain.validators;

import socialNetwork.exceptions.ValidationException;

public interface Validator<T> {
    /**
     * Validates an entity logically
     * @throws ValidationException
     *          if the entity isn't logically valid
     */
    void validate(T entity) throws ValidationException;
}
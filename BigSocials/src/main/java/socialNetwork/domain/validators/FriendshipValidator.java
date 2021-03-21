package socialNetwork.domain.validators;

import socialNetwork.domain.Friendship;
import socialNetwork.exceptions.ValidationException;

public class FriendshipValidator implements Validator<Friendship>{

    @Override
    public void validate(Friendship entity) throws ValidationException {
        if(entity==null)throw new ValidationException("Invalid friendship!");
        if(entity.getId()==null) throw new ValidationException("Invalid friendship!");
        if(entity.getId().getLeft()==null) throw new ValidationException("Invalid friendship!");
        if(entity.getId().getRight()==null) throw new ValidationException("Invalid friendship!");
    }
}

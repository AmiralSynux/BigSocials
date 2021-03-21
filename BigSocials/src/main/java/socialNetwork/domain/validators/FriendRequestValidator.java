package socialNetwork.domain.validators;

import socialNetwork.domain.FriendRequest;
import socialNetwork.exceptions.ValidationException;

public class FriendRequestValidator implements Validator<FriendRequest> {

    @Override
    public void validate(FriendRequest entity) throws ValidationException {
        if(entity==null)throw new ValidationException("Invalid friend request!");
        if(entity.getId()==null) throw new ValidationException("Invalid friend request!");
        String err="";
        if(entity.getSender()==null)
            err=err.concat("Invalid sender!\n");
        if(entity.getReceiver()==null)
            err=err.concat("Invalid receiver!\n");
        if(entity.getStatus()==null)
            err=err.concat("Invalid status!\n");
        if(err.length()!=0)
            throw new ValidationException(err);
    }
}

package socialNetwork.domain.validators;

import socialNetwork.domain.Message;
import socialNetwork.domain.User;
import socialNetwork.exceptions.ValidationException;

public class MessageValidator implements Validator<Message>{

    @Override
    public void validate(Message entity) throws ValidationException {
        if(entity==null)
            throw new ValidationException("Message can't be null!");
        String err = "";
        if(entity.getId()==null)
            err = err.concat("Invalid ID!\n");
        if(entity.getData() == null || entity.getData().length() > 300 || entity.getData().length()<1)
            err = err.concat("The message can't be empty!\n");
        if(entity.getDate()==null)
            err = err.concat("Invalid Date!\n");
        if(entity.getFrom()==null || entity.getFrom().getId()==null)
            err = err.concat("Invalid Sender!\n");
        if(entity.getTo()==null)
            err = err.concat("Invalid receiver!\n");
        else
        {
            if(entity.getTo().size()==0)
                err = err.concat("Invalid receiver!\n");
            else
                for(User user : entity.getTo())
                    if(user.getId()==null)
                    {
                        err = err.concat("Invalid receiver!\n");
                        break;
                    }
        }
        if(entity.getReply()!=null && entity.getReply().getId()==null)
            err = err.concat("Invalid Reply!\n");
        if(err.length()!=0)
            throw new ValidationException(err);

    }
}

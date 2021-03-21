package socialNetwork.domain.validators;

import socialNetwork.domain.Event;
import socialNetwork.exceptions.ValidationException;

public class EventValidator implements Validator<Event>{
    @Override
    public void validate(Event entity) throws ValidationException {
        if(entity==null)
            throw new ValidationException("Event can't be null!");
        if(hasNullFields(entity))
            throw new ValidationException("Event can't have null fields!");
        String err="";
        if(entity.getName().length()<2)
            err+="Event name too short!\n";
        if(entity.getName().length()>25)
            err+="Event name too long!\n";
        if(entity.getPlace().length()<2)
            err+="Event place name too short!\n";
        if(entity.getPlace().length()>30)
            err+="Event place name too long!\n";
        if(entity.getDescription().length()<1)
            err+="Event description too short!\n";
        if(entity.getDescription().length()>500)
            err+="Event description too long!\n";
        if(err.length()!=0)
            throw new ValidationException(err);
    }

    private boolean hasNullFields(Event entity) {
        return entity.getId() == null || entity.getIDs() == null || entity.getDateTime() == null || entity.getName() == null || entity.getPlace() == null || entity.getDescription() == null;
    }
}

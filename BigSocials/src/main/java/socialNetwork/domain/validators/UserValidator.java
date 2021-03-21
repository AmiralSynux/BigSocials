package socialNetwork.domain.validators;

import socialNetwork.domain.User;
import socialNetwork.exceptions.ValidationException;

public class UserValidator implements Validator<User> {
    @Override
    public void validate(User entity) throws ValidationException {
        String s;
        if(entity==null)
            throw new ValidationException("User is null!");
        String firstName = entity.getFirstName();
        String lastName = entity.getLastName();
        String email = entity.getEmail();
        String password = entity.getPassword();
        s = checkNullOrEmpty(firstName,lastName,email,password,entity.getId());
        if(!s.equals(""))
            throw new ValidationException(s);
        s = checkNames(firstName,lastName);
        s = s.concat(checkEmail(email));
        s = s.concat(checkPassword(password));
        if(!s.equals(""))
            throw new ValidationException(s);
    }

    private String checkPassword(String password) {
        if(password.length()<3)
            return "Password too short!\n";
        return "";
    }

    private String checkEmail(String email) {
        if(email.split("@").length!=2)
            return "Invalid email!\n";
        if(email.length()>40)
            return "Email too long!\n";
        return "";
    }

    private String checkNullOrEmpty(String firstName, String lastName, String email, String password, Long id) {
        String s ="";
        if(firstName==null || firstName.equals(""))
            s=s.concat("Invalid first name!\n");
        if(lastName==null || lastName.equals(""))
            s=s.concat("Invalid last name!\n");
        if(email==null || email.equals(""))
            s=s.concat("Invalid email!\n");
        if(password==null || password.equals(""))
            s=s.concat("Invalid password!\n");
        if(id==null)
            s=s.concat("Invalid ID!\n");
        return s;
    }

    private String checkNames(String firstName, String lastName){
        String s="";
        if(firstName.length() < 2 || firstName.length()>20)
            s=s.concat("First name must be between 2 and 20 chars\n");
        if(lastName.length() < 2 || lastName.length()>20)
            s=s.concat("Last name must be between 2 and 20 chars\n");
        if(!firstName.matches("[a-zA-Z0-9 -.]*"))
            s=s.concat("First can contain only letters and numbers! \n");
        if(!lastName.matches("[a-zA-Z0-9 -.]*"))
            s=s.concat("Last can contain only letters and numbers! \n");
        return s;
    }

}

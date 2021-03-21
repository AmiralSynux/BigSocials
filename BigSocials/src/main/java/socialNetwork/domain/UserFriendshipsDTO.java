package socialNetwork.domain;

import socialNetwork.utils.Constants;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class UserFriendshipsDTO implements HasDate{
    private final User user;
    private final LocalDateTime date;

    public UserFriendshipsDTO(User user, LocalDateTime date) {
        this.user = user;
        this.date = date;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public LocalDate getOnlyDate(){return date.toLocalDate();}

    public User getUser() {
        return user;
    }

    public String getLastName(){
        return user.getLastName();
    }

    public String getFirstName(){
        return  user.getFirstName();
    }

    public String getEmail(){return user.getEmail();}

    public String getFullName(){return user.getFirstName() + user.getLastName();}

    @Override
    public String toString() {
        return user.getFirstName() + " | " + user.getLastName() + " | " + date.format(Constants.DATE_TIME_FORMATTER);
    }
}

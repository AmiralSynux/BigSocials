package socialNetwork.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class FriendRequest extends Entity<Long>{
    private User sender;
    private User receiver;
    private FriendRequestStatus status;
    private final LocalDateTime dateTime;

    public FriendRequest(long ID, User sender, User receiver, FriendRequestStatus status, LocalDateTime dateTime) {
        this.sender = sender;
        this.receiver = receiver;
        this.status = status;
        this.dateTime = dateTime;
        setId(ID);
    }

    public FriendRequest(User sender, User receiver, FriendRequestStatus status) {
        this.sender = sender;
        this.receiver = receiver;
        this.status = status;
        this.dateTime = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendRequest that = (FriendRequest) o;
        return (sender.equals(that.sender)||(sender.equals(that.receiver)))&&
                (receiver.equals(that.receiver)||receiver.equals(that.sender)) &&
                status.equals(that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sender, receiver);
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public FriendRequestStatus getStatus() {
        return status;
    }

    public void setStatus(FriendRequestStatus status) {
        this.status = status;
    }

    public LocalDateTime getDateTime() { return dateTime; }

    public LocalDate getDate(){return dateTime.toLocalDate();}

}

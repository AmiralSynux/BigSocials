package socialNetwork.ui.dataWrappers;

import javafx.scene.control.Label;
import javafx.scene.text.Font;
import socialNetwork.domain.FriendRequestStatus;

import java.time.LocalDate;

public class RequestData {
    private final UserData sender;
    private final UserData receiver;
    private final Label date;
    private final Label status;
    private final Long id;
    public RequestData(UserData sender, UserData receiver, FriendRequestStatus status, LocalDate date, Long id) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.date = new Label(date.toString());
        this.status = new Label(status.toString());
        this.date.setFont(Font.font("Poor Richard",15));
        this.status.setFont(Font.font("Poor Richard",15));
    }

    public Long getId(){
        return id;
    }

    public UserData getSender() {
        return sender;
    }

    public UserData getReceiver() {
        return receiver;
    }

    public Label getDate() {
        return date;
    }

    public Label getStatus() {
        return status;
    }

    public FriendRequestStatus getFrStatus(){
        return FriendRequestStatus.valueOf( status.getText());
    }

}

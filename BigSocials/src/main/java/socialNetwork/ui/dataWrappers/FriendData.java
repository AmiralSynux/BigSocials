package socialNetwork.ui.dataWrappers;

import javafx.scene.control.Label;
import javafx.scene.text.Font;

import java.time.LocalDate;

public class FriendData {
    private final UserData userData;
    private final Label date;
    public FriendData(UserData userData, LocalDate date) {
        this.userData = userData;
        this.date = new Label(date.toString());
        this.date.setFont(Font.font("Poor Richard",15));
    }

    public UserData getUserNode() {
        return userData;
    }

    public Label getDate() {
        return date;
    }
}

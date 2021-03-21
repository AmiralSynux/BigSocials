package socialNetwork.ui.dataWrappers;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import socialNetwork.domain.User;

public class UserData extends HBox {

    private final User user;
    public UserData(User user, Image image, double imageSize){
        this.user = user;
        VBox vBox = new VBox();
        imageSize = imageSize/2;
        Label firstName = new Label(" - " + user.getFirstName());
        Label lastName = new Label(" - " + user.getLastName());
        Label email = new Label(" - " + user.getEmail());
        firstName.setFont(Font.font("Poor Richard",imageSize/4));
        lastName.setFont(Font.font("Poor Richard",imageSize/4));
        email.setFont(Font.font("Poor Richard",imageSize/4));
        vBox.getChildren().add(firstName);
        vBox.getChildren().add(lastName);
        vBox.getChildren().add(email);
        ImageView imageView = new ImageView();
        imageView.setImage(image);
        imageView.setFitHeight(imageSize);
        imageView.setFitWidth(imageSize);
        this.getChildren().add(imageView);
        this.getChildren().add(vBox);
    }

    public User getUser() {
        return user;
    }
}

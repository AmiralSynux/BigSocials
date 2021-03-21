package socialNetwork.ui.controllers;

import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import socialNetwork.domain.User;
import socialNetwork.ui.stages.RegisterStage;
import socialNetwork.ui.stages.UserInterfaceStage;

public class LoginController extends AbstractController {

    @FXML PasswordField passwordField;
    @FXML Button LargestCommunityButton;
    @FXML Button CommunitiesButton;
    @FXML TextField Email;
    @FXML Button LoginButton;
    @FXML Button RegisterButton;

    @FXML
    public void login() {
        try {
            User user = service.loginUser(Email.getText(), passwordField.getText());
            if(user!=null)
            {
                this.user=user;
                doAnim();
            }
            else
                showError("Couldn't find the given user!");

        }catch (Exception e){
            showError(e);
        }
    }
    private void doAnim() {
        RotateTransition rt = new RotateTransition(Duration.seconds(1.5),this.stage.getScene().getRoot());
        rt.setByAngle(360);
        rt.setCycleCount(1);
        rt.setAutoReverse(true);
        rt.play();
        rt.setOnFinished(x->{
            Stage newS = new UserInterfaceStage(service,user);
            newS.show();
            stage.close();
        });

    }

    @FXML
    public void register() {
        try{
            Stage stage = new RegisterStage(service);
            stage.show();
            this.stage.close();
        }catch (Exception e){
            showError(e);
        }
    }

    @FXML
    public void Communities() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION,"There are currently " + service.communitiesNr() + " communities", ButtonType.OK);
        alert.setHeaderText(null);
        alert.setTitle("Communities");
        alert.show();
    }

    @FXML
    public void LargestCommunity() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "The Largest Community:",ButtonType.OK);
        alert.setHeaderText(null);
        alert.setTitle("The largest Community");
        String text = "";
        for(User x : service.theMostSociableCommunity())
            text = text.concat("First name: " + x.getFirstName() + " | Last name: " + x.getLastName() + "\n");
        alert.setContentText(text);
        alert.show();
    }

}

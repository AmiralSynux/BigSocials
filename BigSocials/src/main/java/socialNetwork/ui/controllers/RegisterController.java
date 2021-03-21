package socialNetwork.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import socialNetwork.domain.User;
import socialNetwork.ui.stages.LoginStage;

public class RegisterController extends AbstractController {

    @FXML TextField EmailText;
    @FXML PasswordField PasswordText;
    @FXML PasswordField ConfirmText;
    @FXML TextField FirstNameText;
    @FXML TextField LastNameText;
    @FXML Button RegisterButton;
    @FXML Button LogInButton;

    public void register() {
        String firstName = FirstNameText.getText();
        String lastName = LastNameText.getText();
        String pass = PasswordText.getText();
        String conf = ConfirmText.getText();
        String email = EmailText.getText();
        if(firstName.length()==0 || lastName.length()==0 || pass.length()==0 || email.length()==0)
        {
            showError("Please complete all fields!");
            return;
        }
        if(!pass.equals(conf))
        {
            showError("Password does not match with the confirm password!");
            return;
        }
        try{
                User user = service.addUser(new User(firstName,lastName,email,pass));
                if(user!=null)
                    showError("User is already registered!");
                else {
                    stubbornNotify("You registered successfully! Welcome to BigSocials!");
                    backToLogIn();
                }
            }catch (Exception e){
            showError(e);
        }
    }

    public void backToLogIn() {
        Stage stage = new LoginStage(service);
        stage.show();
        this.stage.close();
    }

    /**
     * no need
     */
    @Override
    public void executeUpdate() {
        //no need
    }
}

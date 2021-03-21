package socialNetwork.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import socialNetwork.domain.User;

import java.io.File;
import java.io.FileInputStream;

public class SettingsController extends AbstractController{


    @FXML TextField firstName;
    @FXML TextField lastName;
    @FXML TextField email;
    @FXML PasswordField oldPassword;
    @FXML TextField newPassword;
    @FXML ImageView imageView;
    private File chosen;

    @Override
    protected void init() {
        chosen=null;
        showData();
    }

    private void showData() {
        firstName.setText(user.getFirstName());
        lastName.setText(user.getLastName());
        email.setText(user.getEmail());
        imageView.setImage(getImage(user));
        newPassword.setText("");
        oldPassword.setText("");
    }


    public void changePicture() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image file","*.png","*.jpg"));
            File file = fileChooser.showOpenDialog(this.stage);
            if(file!=null){
                FileInputStream stream = new FileInputStream(file.getAbsolutePath());
                imageView.setImage(new Image(stream));
                chosen=file;
            }
        }catch (Exception e){
            showError(e);
        }
    }

    @Override
    public void executeUpdate() {
        this.user = service.loginUser(this.user.getEmail(),this.user.getPassword());
        showData();
    }

    public void updateUser() {
        try {
        if(oldPassword.getText().length()==0){
            showError("For any update to take place, the account password must be entered!");
            return;
        }
        String password;
        if(newPassword.getText().length()>0){
            password = newPassword.getText();
        }
        else{
            password = oldPassword.getText();
        }
        User user = new User(firstName.getText(),lastName.getText(),email.getText(),password);
        if(chosen==null)
            user.setPicture(this.user.getPicture());
        else
            user.setPicture(chosen.getAbsolutePath());
        service.updateUser(this.user,user,oldPassword.getText());
        executeUpdate();
        showNotify("Update successfully!");
        }catch (Exception e){
            showError(e);
        }
    }
}

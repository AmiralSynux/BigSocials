package socialNetwork.ui.controllers;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import socialNetwork.config.ApplicationContext;
import socialNetwork.domain.User;
import socialNetwork.observerPattern.UpdateBehaviour;
import socialNetwork.service.Service;
import socialNetwork.observerPattern.Observer;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Optional;

public abstract class AbstractController implements Controller, Observer {

    protected Service service;
    protected Stage stage;
    protected User user;
    @Override
    public void initialise(Service service, Stage stage, User user) {
        this.service = service;
        this.stage = stage;
        this.user = user;
        init();
    }

    /**
     * gets the image of the user. if no image is given, or if the given image is not found,
     * it will return a basic image
     * @param user - the user
     * @return - the picture of the user if possible
     *         - a basic picture else
     *         - null if the above two fail
     */
    protected Image getImage(User user){
        FileInputStream stream;
        try {
            stream = new FileInputStream(user.getPicture());
            return new Image(stream);
        }catch (Exception e){
            return getImage("");
        }
    }

    /**
     *
     * @param imageName - returns an image with a given name
     * @return - the image requested if possible
     *         - a basic picture else
     *         - null if the above two fail
     */
    protected Image getImage(String imageName){
        if(imageName==null || imageName.length()==0)
            imageName="basePicture";
        String pic = ApplicationContext.getPROPERTIES().getProperty("picturesPath") + imageName;
        InputStream stream;
        stream = getClass().getClassLoader().getResourceAsStream(pic);
        if(stream==null)
        {
            pic=ApplicationContext.getPROPERTIES().getProperty("basePicture");
            stream =  getClass().getClassLoader().getResourceAsStream(pic);
            if(stream==null){
                showError("Couldn't find any picture!");
                return null;
            }
        }
        return new Image(stream);
    }

    /**
     * This should be overwritten by any class that needs extra initializers.
     * This function is automated called in the initialise method.
     */
    protected void init(){}

    /**
     * shows an error
     * @param error - the error shown
     */
    protected void showError(String error){
        System.out.println(error);
        Alert alert = new Alert(Alert.AlertType.ERROR,error, ButtonType.OK);
        alert.setHeaderText(null);
        alert.show();
    }

    /**
     * shows an error
     * @param error - the error shown
     */
    protected void showError(Exception error){
        System.out.println(error.getMessage());
        Alert alert = new Alert(Alert.AlertType.ERROR,error.getMessage(),ButtonType.OK);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.setHeaderText(null);
        alert.show();
    }

    /**
     * shows an alert with the message
     * @param message - the message shown
     */
    protected void showNotify(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION,message,ButtonType.OK);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.setHeaderText(null);
        alert.show();
    }

    /**
     * shows an alert with the message and waits to be clicked or closed
     * @param message - the message shown
     */
    protected void stubbornNotify(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION,message,ButtonType.OK);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    @Override
    public void executeUpdate() {
    }

    @Override
    public void executeUpdate(UpdateBehaviour updateBehaviour) {
    }
}

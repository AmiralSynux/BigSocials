package socialNetwork.ui.stages;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import socialNetwork.ui.controllers.Controller;
import socialNetwork.service.Service;

import java.io.IOException;

public class LoginStage extends Stage {

    public LoginStage(Service service) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/Login.fxml"));
        AnchorPane layout;
        try {
            layout = loader.load();
            this.setScene(new Scene(layout));
            this.setTitle("BigSocials");
            this.setResizable(false);
            Controller controller = loader.getController();
            controller.initialise(service, this, null);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error: + " + e.getMessage());
        }

    }

}

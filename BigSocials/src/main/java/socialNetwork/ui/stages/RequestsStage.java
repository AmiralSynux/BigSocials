package socialNetwork.ui.stages;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import socialNetwork.domain.User;
import socialNetwork.service.Service;
import socialNetwork.ui.controllers.Controller;

import java.io.IOException;

public class RequestsStage extends Stage {

    public RequestsStage(Service service, User user) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/Requests.fxml"));
        Pane layout;
        try {
            layout = loader.load();
            this.setScene(new Scene(layout));
            this.setTitle("BigSocials");
            this.setResizable(false);
            Controller controller = loader.getController();
            controller.initialise(service, this, user);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error: + " + e.getMessage());
        }
    }
}
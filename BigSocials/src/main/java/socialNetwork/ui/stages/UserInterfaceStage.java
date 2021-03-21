package socialNetwork.ui.stages;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;
import socialNetwork.domain.User;
import socialNetwork.service.Service;
import socialNetwork.ui.controllers.Controller;

import java.io.IOException;

public class UserInterfaceStage extends Stage {

    public UserInterfaceStage(Service service, User user) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/UserInterface.fxml"));
        SplitPane layout;
        try {
            layout = loader.load();
            layout.setDividerPositions(0.20f);
            this.setScene(new Scene(layout));
            this.setTitle("BigSocials");
            setMinHeight(425);
            setMinWidth(825);
            //this.setResizable(false);
            Controller controller = loader.getController();
            controller.initialise(service, this, user);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error: + " + e.getMessage());
        }

    }
}

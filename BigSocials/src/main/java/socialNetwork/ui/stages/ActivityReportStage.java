package socialNetwork.ui.stages;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import socialNetwork.domain.User;
import socialNetwork.service.Service;
import socialNetwork.ui.controllers.ActivityReportController;

import java.io.IOException;
import java.time.LocalDate;

public class ActivityReportStage  extends Stage {

    public ActivityReportStage(Service service, User user, LocalDate begin, LocalDate end) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/ActivityReport.fxml"));
        Pane layout;
        try {
            layout = loader.load();
            this.setScene(new Scene(layout));
            this.setTitle(user.getFirstName() + "'s activity report for " + begin.toString() + " to " + end.toString());
            ActivityReportController controller = loader.getController();
            controller.setDate(begin, end);
            controller.initialise(service, this, user);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error: + " + e.getMessage());
        }
    }

}
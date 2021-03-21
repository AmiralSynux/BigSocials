package socialNetwork.ui.stages;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import socialNetwork.domain.User;
import socialNetwork.service.Service;
import socialNetwork.ui.controllers.Controller;
import socialNetwork.ui.controllers.ConversationReportController;

import java.io.IOException;
import java.time.LocalDate;

public class ConversationReportStage extends Stage {
    public ConversationReportStage(Service service, User user, LocalDate begin, LocalDate end, User friend) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/ConversationReport.fxml"));
        Pane layout;
        try {
            layout = loader.load();
            this.setScene(new Scene(layout));
            this.setTitle(user.getFirstName() + "'s conversation report for " + begin.toString() + " to " + end.toString());
            this.setResizable(false);
            ConversationReportController controller = loader.getController();
            controller.setExtraData(begin, end, friend);
            controller.initialise(service, this, user);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error: + " + e.getMessage());
        }
    }
}

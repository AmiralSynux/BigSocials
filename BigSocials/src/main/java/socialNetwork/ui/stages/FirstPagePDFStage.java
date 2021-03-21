package socialNetwork.ui.stages;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import socialNetwork.domain.User;
import socialNetwork.service.Service;
import socialNetwork.ui.controllers.Controller;
import socialNetwork.ui.controllers.FirstPagePDFController;

import java.io.IOException;
import java.time.LocalDate;

public class FirstPagePDFStage extends Stage {
    public FirstPagePDFStage(Service service, User user, String reportType, LocalDate begin, LocalDate end) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/FirstPagePDF.fxml"));
        Pane layout;
        try {
            layout = loader.load();
            this.setScene(new Scene(layout));
            this.setTitle("BigSocials");
            this.setResizable(false);
            FirstPagePDFController controller = loader.getController();
            controller.initialise(service, this, user);
            controller.setReportType(reportType,begin,end);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error: + " + e.getMessage());
        }
    }
}

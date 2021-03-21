package socialNetwork.ui.controllers;

import javafx.stage.Stage;
import socialNetwork.domain.User;
import socialNetwork.service.Service;

public interface Controller {
    /**
     * Initialises the controller
     * A stage should initialise the controller with the service, itself and a user if needed, otherwise null
     * @param service - the service used by the application
     * @param stage - the stage in use by the controller
     * @param user - the logged in user
     */
    void initialise(Service service, Stage stage, User user);
}

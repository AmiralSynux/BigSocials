package socialNetwork;

import javafx.application.Application;
import javafx.stage.Stage;
import socialNetwork.config.ApplicationContext;
import socialNetwork.domain.*;
import socialNetwork.domain.validators.*;
import socialNetwork.repository.PaginateRepository;
import socialNetwork.repository.database.*;
import socialNetwork.service.Service;
import socialNetwork.ui.stages.LoginStage;
import socialNetwork.ui.stages.UserInterfaceStage;

public class MainApp extends Application {

    private static final boolean isTest = false;

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * This function is called if the isTest flag is set to true.
     * This function should be changed for quicker manual testing
     * @param primaryStage used for testing
     */
    private void test(Stage primaryStage){
        Service service = getService();
        primaryStage = new UserInterfaceStage(service,service.loginUser("anonim@anonim.anonim","anonim@anonim.anonim"));
        primaryStage.show();
    }

    @Override
    public void start(Stage primaryStage){
        try {
            if(!isTest){
            primaryStage = new LoginStage(getService());
            primaryStage.show();
            }else{test(primaryStage);}
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    /**
     * creates the repos and the service needed for the application
     * @return the service of the application
     */
    private Service getService(){
        String URLDatabase = ApplicationContext.getPROPERTIES().getProperty("URLDatabase");
        String ID = ApplicationContext.getPROPERTIES().getProperty("IDDatabase");
        String PASS = ApplicationContext.getPROPERTIES().getProperty("PASSDatabase");

        PaginateRepository<Long, User> userRepository;
        PaginateRepository<Long, Message> messageRepository;
        PaginateRepository<Tuple<Long, Long>, Friendship> friendshipRepository;
        PaginateRepository<Long, FriendRequest> friendRequestRepository;
        PaginateRepository<Long, Event> eventPaginateRepository;

        int pageSize = 5;
        userRepository = new UserPaginateDatabaseRepo(URLDatabase, ID, PASS, new UserValidator(),pageSize);
        friendshipRepository = new FriendshipPaginateDatabaseRepo(URLDatabase, ID, PASS, new FriendshipValidator(),pageSize);
        messageRepository = new MessagePaginateDatabaseRepo(URLDatabase, ID, PASS, new MessageValidator(),pageSize);
        friendRequestRepository = new FriendRequestPaginateDatabaseRepo(URLDatabase, ID, PASS, new FriendRequestValidator(),pageSize);
        eventPaginateRepository = new EventsPaginateDatabaseRepo(URLDatabase, ID, PASS, new EventValidator(),pageSize+3);
        return new Service(userRepository, friendshipRepository, messageRepository, friendRequestRepository, eventPaginateRepository);
    }

}

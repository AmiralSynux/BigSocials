package socialNetwork.ui.controllers;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import socialNetwork.ui.stages.*;

public class UserInterfaceController extends AbstractController {

    @FXML Button logOutButton;
    @FXML Button eventButton;
    @FXML Button reportButton;
    @FXML Button usersButton;
    @FXML Button mailButton;
    @FXML Button settingsButton;
    @FXML VBox userDetails;
    @FXML TabPane tabPane;

    private Button currentButton;

    @Override
    protected void init() {
        loadPictures();
        showUserData();
        loadFirstPage();
        Platform.runLater(() -> service.notifyEvent());
    }

    private void loadPictures() {
        Image settings = getImage("settings.png");
        Image reports = getImage("reports.png");
        Image logOut=getImage("logOut.png");
        Image mail=getImage("mail.png");
        Image users = getImage("users.png");
        Image events = getImage("events.png");
        setPicture(settings,settingsButton);
        setPicture(reports,reportButton);
        setPicture(logOut,logOutButton);
        setPicture(mail,mailButton);
        setPicture(users, usersButton);
        setPicture(events,eventButton);
    }

    private void setPicture(Image image, Button button){
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(button.getPrefWidth()-5);
        imageView.setFitHeight(button.getPrefHeight()-5);
        imageView.setPreserveRatio(true);
        button.setGraphic(imageView);
    }

    private void loadFirstPage() {
        currentButton = eventButton;
        eventPressed();
    }

    private void showUserData() {
        userDetails.getChildren().clear();
        Label name = new Label(user.getFirstName());
        name.setFont(Font.font("Poor Richard",18));
        Label last = new Label(user.getLastName());
        last.setFont(Font.font("Poor Richard",18));
        Label email = new Label(user.getEmail());
        email.setFont(Font.font("Poor Richard",18));
        ImageView imageView = new ImageView();
        imageView.setImage(getImage(user));
        imageView.setFitHeight(userDetails.getPrefHeight()/3*2);
        imageView.setFitWidth(userDetails.getPrefHeight()/3*2);
        userDetails.getChildren().addAll(imageView,name,last,email);
    }

    public void LogOut() {
        service.removeAllObservers();
        Stage stage = new LoginStage(service);
        stage.show();
        this.stage.close();
    }

    private void switchButton(Button button){
        service.removeAllObservers();
        tabPane.getTabs().clear();
        currentButton.setDisable(false);
        currentButton = button;
        currentButton.setDisable(true);
//        RotateTransition rt = new RotateTransition(Duration.seconds(0.5),tabPane);
//        rt.setByAngle(360);
//        rt.setCycleCount(1);
//        rt.setAutoReverse(true);
//        rt.play();
        FadeTransition ft = new FadeTransition(Duration.millis(750), tabPane);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.setCycleCount(1);
        ft.setAutoReverse(true);

        ft.play();
    }

    public void homePressed() {
        switchButton(usersButton);
        Tab firstTab = new Tab("Friends");
        firstTab.setContent(new FriendsStage(service,user).getScene().getRoot());
        Tab secondTab = new Tab("Users");
        secondTab.setContent(new UsersStage(service,user).getScene().getRoot());
        Tab thirdTab = new Tab("Requests");
        thirdTab.setContent(new RequestsStage(service,user).getScene().getRoot());
        tabPane.getTabs().addAll(firstTab,secondTab,thirdTab);
//        TranslateTransition tt = new TranslateTransition(Duration.millis(2000), firstTab.getGraphic());
//        tt.setByX(200f);
//        tt.setCycleCount(4);
//        tt.setAutoReverse(true);
//
//        tt.play();
    }

    public void mailPressed() {
        switchButton(mailButton);
        Tab firstTab = new Tab("Inbox");
        firstTab.setContent(new InboxStage(service,user).getScene().getRoot());
        Tab secondTab = new Tab("Delivered");
        secondTab.setContent(new DeliveredStage(service,user).getScene().getRoot());
        Tab thirdTab = new Tab("Compose");
        thirdTab.setContent(new ComposeStage(service,user).getScene().getRoot());
        tabPane.getTabs().addAll(firstTab, secondTab, thirdTab);
    }

    @Override
    public void executeUpdate() {
        this.user = service.loginUser(this.user.getEmail(),this.user.getPassword());
        showUserData();
    }

    public void settingsPressed() {
        switchButton(settingsButton);
        service.addObserver(this);
        Tab firstTab = new Tab("Settings");
        firstTab.setContent(new SettingsStage(service,user).getScene().getRoot());
        tabPane.getTabs().add(firstTab);
    }

    public void reportPressed() {
        switchButton(reportButton);
        Tab firstTab = new Tab("Reports");
        firstTab.setContent(new ReportsStage(service,user).getScene().getRoot());
        tabPane.getTabs().add(firstTab);
    }

    public void eventPressed(){
        switchButton(eventButton);
        Tab firstTab = new Tab("Followed events");
        firstTab.setContent(new FollowedEvents(service,user).getScene().getRoot());
        Tab secondTab = new Tab("All events");
        secondTab.setContent(new EventStage(service,user).getScene().getRoot());
        Tab thirdTab = new Tab("Create Event");
        thirdTab.setContent(new CreateEventStage(service,user).getScene().getRoot());
        tabPane.getTabs().addAll(firstTab,secondTab,thirdTab);
    }
}

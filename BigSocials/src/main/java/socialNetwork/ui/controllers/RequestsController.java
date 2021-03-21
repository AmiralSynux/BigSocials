package socialNetwork.ui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import socialNetwork.domain.FriendRequest;
import socialNetwork.domain.FriendRequestStatus;
import socialNetwork.observerPattern.UpdateBehaviour;
import socialNetwork.repository.Paginator;
import socialNetwork.ui.dataWrappers.RequestData;
import socialNetwork.ui.dataWrappers.UserData;

import java.time.LocalDate;

public class RequestsController extends AbstractController{

    @FXML Label pageCounter;
    @FXML ChoiceBox<String> senderBox;
    @FXML ChoiceBox<FriendRequestStatus> statusBox;
    @FXML TextField searchField;
    @FXML TableView<RequestData> requestsTable;
    @FXML TableColumn<RequestData, UserData> senderColumn;
    @FXML TableColumn<RequestData, UserData> receiverColumn;
    @FXML TableColumn<RequestData, Label> statusColumn;
    @FXML TableColumn<RequestData, Label> dateColumn;

    ObservableList<RequestData> requests;
    Paginator<FriendRequest> paginator;

    @Override
    protected void init() {
        service.addObserver(this);
        requests = FXCollections.observableArrayList();
        initChoiceBox();
        initItems();
        initTable();
        initConnections();
    }

    private void initChoiceBox() {
        this.statusBox.getItems().addAll(FriendRequestStatus.ALL,FriendRequestStatus.PENDING,FriendRequestStatus.ACCEPTED,FriendRequestStatus.REJECTED,FriendRequestStatus.CANCELED);
        this.statusBox.setStyle("-fx-font-family: 'Poor Richard'; -fx-font-size: 15");
        this.statusBox.setValue(FriendRequestStatus.PENDING);
        this.senderBox.getItems().addAll("All senders","Me","Others");
        this.senderBox.setStyle("-fx-font-family: 'Poor Richard'; -fx-font-size: 15");
        this.senderBox.setValue("All senders");
    }

    private void initItems() {
        paginator = service.filterMyFriendRequest(this.user,searchField.getText(),senderBox.getValue(),statusBox.getValue());
        loadPage();
    }

    private void initConnections() {
        searchField.textProperty().addListener(x -> initItems());
        statusBox.valueProperty().addListener(x -> initItems());
        senderBox.valueProperty().addListener(x -> initItems());
    }

    private void loadPage(){
        requests.clear();
        double imageSize = senderColumn.getPrefWidth()/2;
        Image myImage = getImage(user);
        paginator.getCurrentPage().forEach(friendRequest -> {
            UserData sender;
            UserData receiver;
            if(friendRequest.getSender().equals(user)){
                sender = new UserData(friendRequest.getSender(), myImage,imageSize);
                receiver = new UserData(friendRequest.getReceiver(), getImage(friendRequest.getReceiver()),imageSize);
            }
            else {
                sender = new UserData(friendRequest.getSender(), getImage(friendRequest.getSender()), imageSize);
                receiver = new UserData(friendRequest.getReceiver(), myImage,imageSize);
            }
            RequestData requestData = new RequestData(sender,receiver,friendRequest.getStatus(),friendRequest.getDate(),friendRequest.getId());
            this.requests.add(requestData);
        });
        pageCounter.setText((paginator.getPageNumber()+1) + "/" + (paginator.getTotalPages()+1));
    }

    private void initTable() {
        senderColumn.setCellValueFactory(new PropertyValueFactory<>("sender"));
        receiverColumn.setCellValueFactory(new PropertyValueFactory<>("receiver"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateColumn.setComparator((o1, o2) -> {
            LocalDate one = LocalDate.parse(o1.getText());
            LocalDate two = LocalDate.parse(o2.getText());
            return one.compareTo(two);
        });
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        requestsTable.setItems(requests);
    }

    @Override
    public void executeUpdate() {
        initItems();
    }

    @Override
    public void executeUpdate(UpdateBehaviour updateBehaviour) {
        if(updateBehaviour.equals(UpdateBehaviour.sentRequest)
                || updateBehaviour.equals(UpdateBehaviour.declineRequest)
                || updateBehaviour.equals(UpdateBehaviour.newFriend))
            executeUpdate();
    }

    public void acceptFriendRequest() {
        RequestData requestData = requestsTable.getSelectionModel().getSelectedItem();
        if(requestData==null)
        {
            showError("Please select a request!");
            return;
        }
        try{
            service.respondToFriendRequest(this.user, requestData.getId(),FriendRequestStatus.ACCEPTED);
            showNotify("You and " + requestData.getSender().getUser().getFirstName() + " are now friends!");
        }catch (Exception e){
            showError(e);
        }
    }

    public void rejectFriendRequest() {
        RequestData requestData = requestsTable.getSelectionModel().getSelectedItem();
        if(requestData==null)
        {
            showError("Please select a request!");
            return;
        }
        try{
            service.respondToFriendRequest(this.user, requestData.getId(),FriendRequestStatus.REJECTED);
            showNotify("You rejected " + requestData.getSender().getUser().getFirstName() + " friend request!");
        }catch (Exception e){
            showError(e);
        }
    }

    public void cancelFriendRequest() {
        RequestData requestData = requestsTable.getSelectionModel().getSelectedItem();
        if(requestData==null)
        {
            showError("Please select a request!");
            return;
        }
        try{
            service.respondToFriendRequest(this.user, requestData.getId(),FriendRequestStatus.CANCELED);
            showNotify("You canceled your friend request!");
        }catch (Exception e){
            showError(e);
        }
    }

    public void prevPage() {
        if(paginator.previousPage())
            loadPage();
    }



    public void nextPage() {
        if(paginator.nextPage())
            loadPage();
    }
}

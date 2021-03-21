package socialNetwork.ui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import socialNetwork.domain.User;
import socialNetwork.observerPattern.UpdateBehaviour;
import socialNetwork.repository.Paginator;
import socialNetwork.ui.dataWrappers.UserData;

public class UsersController extends AbstractController {

    public Label pageCounter;
    @FXML ListView<UserData> listView;
    @FXML TextField filterField;
    ObservableList<UserData> nodes;
    Paginator<User> paginator;
    @Override
    protected void init() {
        service.addObserver(this);
        nodes = FXCollections.observableArrayList();
        initItems();
        filterField.textProperty().addListener(x->initItems());
    }

    private void initItems(){
        paginator = service.getFilteredUsers(this.user,filterField.getText());
        loadPage();
    }

    private void loadPage() {
        nodes.clear();
        for (User user : paginator.getCurrentPage())
        {
            UserData userData = new UserData(user,getImage(user),listView.getMinWidth()/2);
            this.nodes.add(userData);
        }
        listView.setItems(nodes);
        pageCounter.setText((paginator.getPageNumber()+1) + "/" + (paginator.getTotalPages()+1));
    }

    @Override
    public void executeUpdate() {
        initItems();
    }

    @Override
    public void executeUpdate(UpdateBehaviour updateBehaviour) {
        if(updateBehaviour.equals(UpdateBehaviour.declineRequest) || updateBehaviour.equals(UpdateBehaviour.removeFriend))
            executeUpdate();
    }

    public void sendRequest() {
        UserData userData = listView.getSelectionModel().getSelectedItem();
        if(userData == null){
            showError("Please select an User from the list!");
            return;
        }
        try {
            service.sendFriendRequest(this.user, userData.getUser());
            executeUpdate();
            showNotify("You successfully sent a friend request to " + userData.getUser().getFirstName() + "!");
        }catch (Exception e)
        {
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

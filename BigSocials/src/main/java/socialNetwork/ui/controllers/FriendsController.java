package socialNetwork.ui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import socialNetwork.domain.User;
import socialNetwork.domain.UserFriendshipsDTO;
import socialNetwork.observerPattern.UpdateBehaviour;
import socialNetwork.repository.Paginator;
import socialNetwork.ui.dataWrappers.FriendData;
import socialNetwork.ui.dataWrappers.UserData;

import java.time.LocalDate;

public class FriendsController extends AbstractController {

    @FXML Label pageCounter;
    @FXML TextField searchField;
    @FXML TableView<FriendData> friendsTable;
    @FXML TableColumn<FriendData, UserData> friendsColumn;
    @FXML TableColumn<FriendData, Label> dateColumn;

    ObservableList<FriendData> dataList;
    Paginator<UserFriendshipsDTO> paginator;

    @Override
    protected void init() {
        service.addObserver(this);
        dataList = FXCollections.observableArrayList();
        initItems();
        initTable();
        initConnections();
    }

    private void initConnections() {
        searchField.textProperty().addListener(x -> initItems());
    }

    private void initTable() {
        friendsColumn.setCellValueFactory(new PropertyValueFactory<>("userNode"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateColumn.setComparator((o1, o2) -> {
            LocalDate one = LocalDate.parse(o1.getText());
            LocalDate two = LocalDate.parse(o2.getText());
            return one.compareTo(two);
        });
        friendsTable.setItems(dataList);
    }
    private void initItems() {
        paginator = service.getUserFriendships(this.user,searchField.getText());
        loadPage();
    }

    private void loadPage() {
        dataList.clear();
        paginator.getCurrentPage().forEach(userFriendshipsDTO -> {
            User friend = userFriendshipsDTO.getUser();
            FriendData friendData = new FriendData(new UserData(friend,getImage(friend),friendsColumn.getPrefWidth()/2),userFriendshipsDTO.getOnlyDate());
            dataList.add(friendData);
        });
        pageCounter.setText((paginator.getPageNumber()+1) + "/" + (paginator.getTotalPages()+1));
    }

    @Override
    public void executeUpdate() {
        initItems();
    }

    @Override
    public void executeUpdate(UpdateBehaviour updateBehaviour) {
        if(updateBehaviour.equals(UpdateBehaviour.newFriend))
            executeUpdate();
    }

    public void deleteFriend() {
        FriendData selectedItem = friendsTable.getSelectionModel().getSelectedItem();
        if(selectedItem==null){
            showError("Please select a friend!");
            return;
        }
        try {
            User friend = selectedItem.getUserNode().getUser();
            service.removeFriend(user,friend);
            executeUpdate();
            showNotify("You are no longer friend with " + friend.getFirstName() + "!");
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

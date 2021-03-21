package socialNetwork.ui.controllers;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import socialNetwork.domain.Event;
import socialNetwork.observerPattern.UpdateBehaviour;
import socialNetwork.repository.Paginator;

import java.time.format.DateTimeFormatter;

public class EventController extends AbstractController{

    @FXML Label pageCounter;
    @FXML TableColumn<Event,Integer> peopleColumn;
    @FXML TableColumn<Event,String> descriptionColumn;
    @FXML TableColumn<Event,String> dateColumn;
    @FXML TableColumn<Event,String> locationColumn;
    @FXML TableColumn<Event,String> nameColumn;
    @FXML TableView<Event> tableView;
    @FXML TextField searchField;

    private Paginator<Event> paginator;
    ObservableList<Event> events;

    @Override
    protected void init() {
        service.addObserver(this);
        events = FXCollections.observableArrayList();
        initItems();
        initTable();
        searchField.textProperty().addListener(x->initItems());
    }

    private void initTable() {
        peopleColumn.setCellValueFactory(x->new ReadOnlyObjectWrapper<>(x.getValue().getIDs().size()));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        dateColumn.setCellValueFactory(x->new ReadOnlyObjectWrapper<>(x.getValue().getDateTime().toLocalDate().toString() + "\n  - " + x.getValue().getDateTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")) + " - "));
        locationColumn.setCellValueFactory(x-> new ReadOnlyObjectWrapper<>(x.getValue().getPlace()));
        nameColumn.setCellValueFactory(x-> new ReadOnlyObjectWrapper<>(x.getValue().getName()));
        tableView.setItems(events);
    }

    private void initItems() {
        paginator = service.getFilteredEvents(searchField.getText());
        loadPage();
    }

    private void loadPage() {
        events.clear();
        paginator.getCurrentPage().forEach(events::add);
        pageCounter.setText((paginator.getPageNumber()+1) + "/" + (paginator.getTotalPages()+1));
    }

    public void prevPage() {
        if(paginator.previousPage())
            loadPage();
    }

    public void nextPage() {
        if(paginator.nextPage())
            loadPage();
    }

    @Override
    public void executeUpdate() {
        initItems();
    }

    @Override
    public void executeUpdate(UpdateBehaviour updateBehaviour) {
        if(updateBehaviour.equals(UpdateBehaviour.followEvent))
            loadPage();
        else
            if(updateBehaviour.equals(UpdateBehaviour.unfollowEvent) || updateBehaviour.equals(UpdateBehaviour.createEvent))
                executeUpdate();
    }

    public void followEvent() {
        Event event = tableView.getSelectionModel().getSelectedItem();
        if(event==null){
            showError("Please select an event!");
            return;
        }
        try {
            service.followEvent(user,event);
            showNotify("You are now following " + event.getName());
        }catch (Exception e){
            showError(e);
        }
    }
}

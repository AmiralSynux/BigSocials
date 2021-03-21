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

import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class FollowedEventsController extends AbstractController{

    @FXML
    Label pageCounter;
    @FXML
    TableColumn<Event,Integer> peopleColumn;
    @FXML TableColumn<Event,String> descriptionColumn;
    @FXML TableColumn<Event,String> dateColumn;
    @FXML TableColumn<Event,String> locationColumn;
    @FXML TableColumn<Event,String> nameColumn;
    @FXML
    TableView<Event> tableView;
    @FXML
    TextField searchField;

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

    private String getString(int nr,String str){
        if(nr==1)
            return nr + " " + str;
        return nr + " " + str + "s";
    }

    public void notifyUser() {
        if(events.size()==0)
            return;
        Event event = events.get(0);
        if(event==null)
            return;
        if(event.getDateTime().isBefore(LocalDateTime.now()))
            showNotify("Event '"+event.getName()+"' started!");
        else
        {
            LocalDateTime remaining = event.getDateTime();
            remaining = remaining.minusSeconds(LocalDateTime.now().getSecond());
            remaining = remaining.minusMinutes(LocalDateTime.now().getMinute());
            remaining = remaining.minusHours(LocalDateTime.now().getHour());
            Period period = Period.between(LocalDateTime.now().toLocalDate(),remaining.toLocalDate());
            String timeLapse="The closest event: `" + event.getName() + "`.\nTime remaining: ";
            if(period.getYears()>0) {
                timeLapse += getString(period.getYears(),"year") + ", " +
                        getString(period.getMonths(), "month") + ", " +
                        getString(period.getDays(), "day") + ", ";
            }
            else{
                if(period.getMonths()>0) {
                    timeLapse += getString(period.getMonths(), "month") + ", " +
                            getString(period.getDays(), "day") + ", ";
                }
                else{
                    if (period.getDays()>0){
                        timeLapse += getString(period.getDays(), "day") + ", ";
                    }
                }
            }
            timeLapse += getString(remaining.getHour(),"hour") + ", "+
                    getString(remaining.getMinute(),"minute") + ", " +
                    getString(remaining.getSecond(),"second") + ". ";

            showNotify(timeLapse);
        }

    }

    private void initTable() {
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        dateColumn.setCellValueFactory(x->new ReadOnlyObjectWrapper<>(x.getValue().getDateTime().toLocalDate().toString() + "\n  - " + x.getValue().getDateTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")) + " - "));
        peopleColumn.setCellValueFactory(x->new ReadOnlyObjectWrapper<>(x.getValue().getIDs().size()));
        locationColumn.setCellValueFactory(x-> new ReadOnlyObjectWrapper<>(x.getValue().getPlace()));
        nameColumn.setCellValueFactory(x-> new ReadOnlyObjectWrapper<>(x.getValue().getName()));
        tableView.setItems(events);
    }

    private void initItems() {
        paginator = service.getFollowedFilteredEvents(this.user,searchField.getText());
        loadPage();
    }

    private void loadPage() {
        events.clear();
        paginator.getCurrentPage().forEach(events::add);
        pageCounter.setText((paginator.getPageNumber()+1) + "/" + (paginator.getTotalPages()+1));
    }

    @Override
    public void executeUpdate() {
        initItems();
    }

    @Override
    public void executeUpdate(UpdateBehaviour updateBehaviour) {
        if(updateBehaviour.equals(UpdateBehaviour.notifyClosestEvent))
            notifyUser();
        else
            if(!updateBehaviour.equals(UpdateBehaviour.createEvent))
                executeUpdate();

    }

    public void prevPage() {
        if(paginator.previousPage())
            loadPage();
    }

    public void nextPage() {
        if(paginator.nextPage())
            loadPage();
    }

    public void unfollowEvent() {
        Event event = tableView.getSelectionModel().getSelectedItem();
        if(event==null){
            showError("Please select an event!");
            return;
        }
        try {
            service.unfollowEvent(user,event);
            showNotify("You have unfollowed " + event.getName());
        }catch (Exception e){
            showError(e);
        }
    }
}

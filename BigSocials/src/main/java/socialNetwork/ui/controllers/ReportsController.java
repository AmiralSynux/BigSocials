package socialNetwork.ui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import socialNetwork.domain.User;
import socialNetwork.exceptions.UIException;
import socialNetwork.ui.stages.ActivityReportStage;
import socialNetwork.ui.stages.ConversationReportStage;

import java.time.LocalDate;
import java.util.List;


public class ReportsController extends AbstractController{
    @FXML ContextMenu contextMenu;
    @FXML DatePicker endDateIR;
    @FXML DatePicker beginDateIR;
    @FXML DatePicker endDateAC;
    @FXML DatePicker beginDateAC;
    @FXML TextField mailField;

    private List<User> friends;
    ObservableList<String> suggestions;
    private static final int SUGGESTIONS_LIMIT = 10;

    @Override
    protected void init() {
        initItems();
        initConnections();
        initDates();
    }

    private void initDates() {
        endDateIR.setValue(LocalDate.now());
        endDateAC.setValue(LocalDate.now());
        beginDateIR.setValue(LocalDate.now().minusDays(LocalDate.now().getDayOfMonth()));
        beginDateAC.setValue(LocalDate.now().minusDays(LocalDate.now().getDayOfMonth()));
    }

    private void initItems() {
        friends = user.getFriends();
        suggestions = FXCollections.observableArrayList();
    }

    private void initConnections() {
        mailField.textProperty().addListener(x->showSuggestions());
        mailField.setOnMouseClicked(x->showSuggestions());
    }

    private void showSuggestions() {
        contextMenu.hide();
        makeSuggestions();
        contextMenu.show(mailField, Side.BOTTOM,0,0);
    }

    private void makeSuggestions(){
        String data = mailField.getText();
        suggestions.clear();
        friends.forEach(x->{
            if(x.getEmail().contains(data))
                suggestions.add(x.getEmail());
        });
        contextMenu.getItems().clear();
        for(String suggestion : suggestions)
        {
            MenuItem item = new MenuItem(suggestion);
            item.setOnAction(x-> mailField.setText(suggestion));
            contextMenu.getItems().add(item);
            if(contextMenu.getItems().size()>=SUGGESTIONS_LIMIT)
                break;
        }
    }

    private LocalDate readDate(DatePicker picker){
        if(picker.getValue()==null)
            throw new UIException("Invalid Date!");
        return picker.getValue();
    }

    private User readMail(TextField mailField){
        String mail = mailField.getText().toLowerCase();
        for (User friend : this.user.getFriends())
            if(friend.getEmail().toLowerCase().equals(mail))
                return friend;
        throw new UIException("Invalid mail!\nPlease enter a mail of one of your friends!");
    }

    @FXML
    public void activityReport() {
        try {
            LocalDate begin = readDate(beginDateAC);
            LocalDate end = readDate(endDateAC);
            if(begin.isAfter(end))
            {
                showError("Begin date can't be after end date!");
                return;
            }
            if(end.minusYears(1).isAfter(begin))
            {
                showError("Time lapse can't be bigger than one year!");
                return;
            }
            Stage stage = new ActivityReportStage(service,user,begin,end);
            stage.show();
        }catch (Exception e){showError(e);}
    }

    @FXML
    public void inboxReport() {
        try{
            LocalDate begin = readDate(beginDateIR);
            LocalDate end = readDate(endDateIR);
            if(begin.isAfter(end))
            {
                showError("Begin date can't be after end date!");
                return;
            }
            Stage stage = new ConversationReportStage(service,user,begin,end,readMail(mailField));
            stage.show();
        }catch (Exception e){showError(e);}
    }
}

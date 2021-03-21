package socialNetwork.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import socialNetwork.domain.Event;
import socialNetwork.exceptions.UIException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class CreateEventController extends AbstractController{

    @FXML TextField eventName;
    @FXML DatePicker datePicker;
    @FXML TextField timeField;
    @FXML TextField eventPlace;
    @FXML TextArea descriptionField;

    private LocalTime getTime(){
        String time = timeField.getText();
        if(time==null || time.length()==0)
            throw new UIException("Please enter the time of the event!");
        String[] splitter = time.split(":");
        if(splitter.length != 2)
            throw new UIException("Invalid time format!\nTime format should be: HH:MM\n Ex: 17:45");
        int hour = Integer.parseInt(splitter[0]);
        int minute = Integer.parseInt(splitter[1]);
        if(hour< 0 || hour > 23)
            throw new UIException("Invalid hour! Hours are between 0 and 23");
        if(minute< 0 || minute > 59)
            throw new UIException("Invalid hour! Hours are between 0 and 59");

        return LocalTime.of(hour,minute);

    }

    public void createEvent() {
        try {
            Event event = new Event(eventName.getText(),getDateTime(),eventPlace.getText(),descriptionField.getText());
            service.createEvent(event);
            showNotify("Event created successfully!");
        }
        catch (NumberFormatException e){
            showError("Invalid time!");
        }
        catch (Exception e){
            showError(e);
        }
    }

    private LocalDateTime getDateTime() {
        LocalDate date = datePicker.getValue();
        if(date==null)
            throw new UIException("Please select the date of the event!");
        LocalTime time = getTime();
        return LocalDateTime.of(date,time);
    }
}

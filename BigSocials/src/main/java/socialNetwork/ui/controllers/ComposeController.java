package socialNetwork.ui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.*;
import socialNetwork.domain.User;

import java.util.ArrayList;
import java.util.List;

public class ComposeController extends AbstractController{

    @FXML ContextMenu contextMenu;
    @FXML Label labelCharCount;
    @FXML ProgressBar progressBar;
    @FXML TextField sendToField;
    @FXML TextArea messageField;

    private List<User> friends;
    private ObservableList<String> suggestions;
    //used to place the autocomplete bar in the right spot
    private final static int PXMultiply = 5;
    //limit for autocomplete
    private final static int SUGGESTIONS_LIMIT = 10;

    @Override
    protected void init() {
        initItems();
        innitConnections();
    }

    private void initItems() {
        friends = user.getFriends();
        suggestions = FXCollections.observableArrayList();
        friends.forEach(x ->suggestions.add(x.getEmail()));
    }

    private void innitConnections() {
        messageField.textProperty().addListener(x->countChar());
        sendToField.textProperty().addListener(x->showSuggestions());
        sendToField.setOnMouseClicked(x->showSuggestions());
    }

    private void showSuggestions() {
        contextMenu.hide();
        makeSuggestions();
        contextMenu.show(sendToField, Side.BOTTOM,sendToField.getText().length() * PXMultiply,0);
    }


    private void makeSuggestions() {
        String data = sendToField.getText();
        String[] splitter = data.split(", ");
        if(splitter.length==0)
            data="";
        else if(data.endsWith(", "))
                data="";
            else
                data=splitter[splitter.length-1];
        setSuggestions(data);
        contextMenu.getItems().clear();
        for(String suggestion : suggestions)
        {
            MenuItem item = new MenuItem(suggestion);
            item.setOnAction(x-> autocomplete(item.getText()));
            contextMenu.getItems().add(item);
            if(contextMenu.getItems().size()>=SUGGESTIONS_LIMIT)
                break;
        }
    }

    private void setSuggestions(String beginning){
        suggestions.clear();
        friends.forEach(x->{
            if(x.getEmail().contains(beginning))
                suggestions.add(x.getEmail());
        });
    }

    @FXML
    public void sendMessage() {
        try {
            List<User> to = new ArrayList<>();
            for (User friend : friends)
                if (sendToField.getText().contains(friend.getEmail()))
                    to.add(friend);
            service.sendMessage(messageField.getText(), this.user, to, null);
            emptyFields();
            showNotify("Message sent!");
        }catch (Exception e){
            showError(e);
        }
    }

    private void emptyFields() {
        sendToField.clear();
        messageField.clear();
        contextMenu.hide();
    }

    private void autocomplete(String mail) {
        String fullData = sendToField.getText();
        if(fullData.endsWith(", "))
        {
            sendToField.setText(fullData + mail + ", ");
        }
        else{
            String[] splitter = fullData.split(", ");
            if(splitter.length==0) {
                splitter = new String[1];
            }
            if(splitter[splitter.length - 1].endsWith(", "))
                splitter[splitter.length - 1]+=mail;
            else
                splitter[splitter.length - 1]=mail;
            StringBuilder mails = new StringBuilder();
            for(String aMail : splitter) {
                mails.append(aMail).append(", ");
            }
            sendToField.setText(mails.toString());
        }
        sendToField.positionCaret(sendToField.getText().length());
    }

    private void countChar() {
        double size = messageField.getText().length();
        while(size>300){
            messageField.setText(messageField.getText().substring(0,300));
            size=300;
        }
        String labelString = (int)size + "/300";
        labelCharCount.setText(labelString);
        progressBar.setProgress(size/300);
    }

}

package socialNetwork.ui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import socialNetwork.domain.Message;
import socialNetwork.domain.User;
import socialNetwork.observerPattern.UpdateBehaviour;
import socialNetwork.repository.Paginator;
import socialNetwork.ui.dataWrappers.MessageData;
import socialNetwork.utils.Constants;

public class DeliveredController extends AbstractController{

    @FXML  Label pageCounter;
    @FXML TextField searchField;
    @FXML ListView<MessageData> listView;
    @FXML TextArea messageField;
    @FXML TextArea toField;
    @FXML TextArea replyField;
    @FXML TextField fromField;
    @FXML TextField dateField;

    ObservableList<MessageData> messages;
    Paginator<Message> paginator;

    private Image unreadPicture;
    private Image readPicture;

    @Override
    protected void init() {
        initPictures();
        messages = FXCollections.observableArrayList();
        initItems();
        listView.setItems(messages);
        initConnections();
        service.addObserver(this);
    }

    @Override
    public void executeUpdate() {
        initItems();
    }

    @Override
    public void executeUpdate(UpdateBehaviour updateBehaviour) {
        if(updateBehaviour.equals(UpdateBehaviour.sentMessage))
            executeUpdate();
    }

    private void initPictures() {
        unreadPicture = getImage("closeMessage.png");
        readPicture = getImage("openMessage.png");
    }

    private void initConnections() {
        searchField.textProperty().addListener(x->initItems());
        listView.getSelectionModel().selectedItemProperty().addListener(x -> showData());
    }

    private void showData(){
        MessageData msgNode = listView.getSelectionModel().getSelectedItem();
        if(msgNode==null)
            return;
        Message msg = msgNode.getMessage();
        StringBuilder mails = new StringBuilder();
        for(User user : msg.getTo())
            mails.append(user.getEmail()).append(", ");
        messageField.setText(msg.getData());
        dateField.setText(msg.getDate().format(Constants.DATE_TIME_FORMATTER));
        toField.setText(mails.substring(0,mails.length()-2));
        fromField.setText(msg.getFrom().getEmail());
        if(msg.getReply()!=null)
            replyField.setText(msg.getReply().getData());
        else replyField.setText("");
    }

    private void initItems() {
        paginator = service.getSendPage(this.user, searchField.getText());
        loadPage();
    }

    private void loadPage(){
        messages.clear();
        paginator.getCurrentPage().forEach(m -> {
            if(m.isRead())
                messages.add(new MessageData(m,readPicture,listView.getPrefWidth()/2));
            else
                messages.add(new MessageData(m,unreadPicture,listView.getPrefWidth()/2));
        });
        pageCounter.setText((paginator.getPageNumber()+1) + "/" + (paginator.getTotalPages()+1));
    }

    public void previousPage() {
        if (paginator.previousPage())
            loadPage();
    }

    public void nextPage() {
        if(paginator.nextPage())
            loadPage();
    }
}

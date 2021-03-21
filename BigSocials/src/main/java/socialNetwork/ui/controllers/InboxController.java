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
import socialNetwork.repository.Paginator;
import socialNetwork.ui.dataWrappers.MessageData;
import socialNetwork.utils.Constants;

import java.util.ArrayList;

public class InboxController extends AbstractController{

    @FXML Label pageCounter;
    @FXML TextArea newMsgField;
    @FXML TextField searchField;
    @FXML ListView<MessageData> listView;
    @FXML TextArea messageField;
    @FXML TextArea toField;
    @FXML TextArea replyField;
    @FXML TextField fromField;
    @FXML TextField dateField;

    Paginator<Message> paginator;
    ObservableList<MessageData> messages;

    private Image unreadPicture;
    private Image readPicture;

    private Message currentMsg;

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
        msgNode.setImage(readPicture);
        Message msg = msgNode.getMessage();
        service.readMessage(msg);
        StringBuilder mails = new StringBuilder();
        for(User user : msg.getTo())
            mails.append(user.getEmail()).append(", ");
        toField.setText(mails.substring(0,mails.length()-2));
        fromField.setText(msg.getFrom().getEmail());
        messageField.setText(msg.getData());
        dateField.setText(msg.getDate().format(Constants.DATE_TIME_FORMATTER));
        if(msg.getReply()!=null)
            replyField.setText(msg.getReply().getData());
        else
            replyField.setText("");
        this.currentMsg = msg;
    }

    private void initItems() {
        paginator = service.getInboxPage(this.user,searchField.getText());
        loadPage();
    }

    private void loadPage(){
        messages.clear();
        pageCounter.setText((paginator.getPageNumber()+1) + "/" + (paginator.getTotalPages()+1));
        paginator.getCurrentPage().forEach(m -> {
            if(m.isRead())
                messages.add(new MessageData(m,readPicture,listView.getPrefWidth()/2));
            else
                messages.add(new MessageData(m,unreadPicture,listView.getPrefWidth()/2));
        });
    }

    public void reply() {
        if(currentMsg==null)
            showError("Please select a message!");
        else{
            try {
                ArrayList<User> arrayList = new ArrayList<>();
                arrayList.add(this.currentMsg.getFrom());
                service.sendMessage(newMsgField.getText(),this.user,arrayList,this.currentMsg);
                showNotify("Reply sent!");
                newMsgField.setText("");
            }catch (Exception e){
                showError(e);
            }
        }
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

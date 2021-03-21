package socialNetwork.ui.dataWrappers;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import socialNetwork.domain.Message;

public class MessageData extends HBox {
    private final Message message;
    private final ImageView imageView;
    public MessageData(Message message, Image image , double imageSize){
        this.message = message;
        VBox vBox = new VBox();
        imageSize = imageSize/2;
        Label email = new Label(" - " + message.getFrom().getEmail());
        Label shortDesc = new Label(" - " + getDesc());
        if(message.getReply()==null)
        {
            shortDesc.setFont(Font.font("Poor Richard",imageSize/4));
            email.setFont(Font.font("Poor Richard",imageSize/4));
            vBox.setSpacing(imageSize/8);
            vBox.getChildren().addAll(email,shortDesc);
        }
        else{
            Label reply = new Label("Reply: " + getReply());
            reply.setFont(Font.font("Poor Richard",imageSize/4));
            shortDesc.setFont(Font.font("Poor Richard",imageSize/4));
            email.setFont(Font.font("Poor Richard",imageSize/4));
            vBox.getChildren().addAll(reply,email,shortDesc);
        }
        imageView = new ImageView();
        imageView.setImage(image);
        imageView.setFitHeight(imageSize);
        imageView.setFitWidth(imageSize);
        this.getChildren().add(imageView);
        this.getChildren().add(vBox);

    }

    public void setImage(Image image){
        imageView.setImage(image);
    }

    public Message getMessage() {
        return message;
    }

    private String getReply() {
        String reply = message.getReply().getData();
        if(reply.contains("\n"))
            reply=reply.split("\n")[0];
        if(message.getData().length() > 25)
            reply+="...";
        if(reply.length()>21)
            reply=reply.substring(0,18) + "...";
        return reply;
    }

    private String getDesc(){
        String desc = message.getData();
        if(desc.contains("\n"))
            desc=desc.split("\n")[0];
        if(message.getData().length() > 25)
            desc+="...";
        if(desc.length()>25)
            desc=desc.substring(0,22) + "...";
        return desc;
    }
}

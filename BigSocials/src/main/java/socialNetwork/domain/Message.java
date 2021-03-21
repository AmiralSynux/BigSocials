package socialNetwork.domain;

import java.time.LocalDateTime;
import java.util.List;

public class Message extends Entity<Long> implements HasDate{

    private String data;
    private User from;
    private List<User> to;
    private LocalDateTime date;
    private Message reply;
    private boolean read;

    public Message(long id, String data, User from, List<User> to, LocalDateTime date, Message reply, boolean read) {
        super.setId(id);
        this.data = data;
        this.from = from;
        this.to = to;
        this.date = date;
        this.reply = reply;
        this.read = read;
    }

    public Message(String data, User from, List<User> to, LocalDateTime date, Message reply) {
        this.data = data;
        this.from = from;
        this.to = to;
        this.date = date;
        this.reply = reply;
    }

    @Override
    public String toString() {
        return "Message{" +
                "data='" + data + '\'' +
                ", from=" + from +
                ", to=" + to +
                ", date=" + date +
                ", reply=" + reply +
                '}';
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public List<User> getTo() {
        return to;
    }

    public void setTo(List<User> to) {
        this.to = to;
    }

    @Override
    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Message getReply() {
        return reply;
    }

    public void setReply(Message reply) {
        this.reply = reply;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}

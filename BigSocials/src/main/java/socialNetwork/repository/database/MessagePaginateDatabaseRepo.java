package socialNetwork.repository.database;

import socialNetwork.domain.Message;
import socialNetwork.domain.User;
import socialNetwork.domain.validators.Validator;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MessagePaginateDatabaseRepo extends AbstractPaginateDatabaseRepo<Long, Message> {
    public MessagePaginateDatabaseRepo(String URL, String ID, String PASS, Validator<Message> validator, int pageSize) {
        super(URL, ID, PASS, validator, pageSize);
    }

    @Override
    protected ResultSet getResultSet(Statement statement) throws SQLException {
        return statement.executeQuery("SELECT * FROM public.messages;");
    }

    @Override
    protected Message readEntity(ResultSet resultSet) {
        try {
            User from = new User(resultSet.getLong("user_from"));
            String users = resultSet.getString("user_to");
            List<User> to = Arrays.stream(users.split(";"))
                    .filter(x->x!=null && !x.equals(""))
                    .map(x -> {
                        return new User(Long.parseLong(x));
                    })
                    .collect(Collectors.toList());
            Long replyID = resultSet.getLong("reply_id");
            Message reply = null;
            if(replyID!=-1) reply = new Message(replyID,null,null ,null,null,null,resultSet.getBoolean("read"));
            return new Message(resultSet.getLong("message_id"),
                    resultSet.getString("data"),
                    from,
                    to,
                    LocalDateTime.parse(resultSet.getString("date")),
                    reply,
                    resultSet.getBoolean("read"));
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

    @Override
    protected Message insertEntity(Message entity, Connection connection) {
        try (PreparedStatement prep = connection.prepareStatement("INSERT INTO public.messages(message_id, data, user_from, date, reply_id, user_to, read) VALUES (?, ?, ?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS))
        {
            prep.setLong(1,entity.getId());
            prep.setString(2,entity.getData());
            prep.setLong(3,entity.getFrom().getId());
            prep.setString(4,entity.getDate().toString());
            if(entity.getReply()==null)
                prep.setLong(5, -1);
            else
                prep.setLong(5,entity.getReply().getId());
            String IDs = "";
            for(User users : entity.getTo())
                IDs = IDs.concat(users.getId().toString() + ";");
            prep.setString(6, IDs);
            prep.setBoolean(7, entity.isRead());
            prep.execute();
            return entity;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

    @Override
    protected Message deleteEntity(Message entity, Connection connection) {
        try {
            connection.createStatement().execute("DELETE FROM public.messages WHERE message_id = " + entity.getId() + ";");
            return entity;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }
}

package socialNetwork.repository.database;

import socialNetwork.domain.FriendRequest;
import socialNetwork.domain.FriendRequestStatus;
import socialNetwork.domain.User;
import socialNetwork.domain.validators.Validator;

import java.sql.*;
import java.time.LocalDateTime;

public class FriendRequestPaginateDatabaseRepo extends AbstractPaginateDatabaseRepo<Long, FriendRequest> {
    public FriendRequestPaginateDatabaseRepo(String URL, String ID, String PASS, Validator<FriendRequest> validator, int pageSize) {
        super(URL, ID, PASS, validator, pageSize);
    }

    @Override
    protected ResultSet getResultSet(Statement statement) throws SQLException {
        return statement.executeQuery("SELECT * FROM public.friend_request");
    }

    FriendRequestStatus getStatus(String status){
        return FriendRequestStatus.valueOf(status);
    }

    @Override
    protected FriendRequest readEntity(ResultSet resultSet) {
        try {
            User sender = new User(resultSet.getLong("sender"));
            User receiver = new User(resultSet.getLong("receiver"));
            return new FriendRequest(resultSet.getLong("request_id"),
                    sender,
                    receiver,
                    getStatus(resultSet.getString("status")),
                    LocalDateTime.parse(resultSet.getString("date")));
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return null;
        }
    }

    @Override
    protected FriendRequest insertEntity(FriendRequest entity, Connection connection) {
        try (PreparedStatement prep = connection.prepareStatement( "INSERT INTO public.friend_request( request_id, sender, receiver, status, date) VALUES (?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS))
        {
            prep.setLong(1,entity.getId());
            prep.setLong(2,entity.getSender().getId());
            prep.setLong(3,entity.getReceiver().getId());
            prep.setString(4,entity.getStatus().toString());
            prep.setString(5,entity.getDateTime().toString());
            prep.execute();
            return entity;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

    @Override
    protected FriendRequest deleteEntity(FriendRequest entity, Connection connection) {
        try {
            connection.createStatement().execute("DELETE FROM public.friend_request WHERE request_id = " + entity.getId() + ";");
            return entity;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }
}

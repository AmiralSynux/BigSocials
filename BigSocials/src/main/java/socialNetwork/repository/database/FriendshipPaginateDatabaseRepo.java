package socialNetwork.repository.database;

import socialNetwork.domain.Friendship;
import socialNetwork.domain.Tuple;
import socialNetwork.domain.validators.Validator;

import java.sql.*;
import java.time.LocalDateTime;

public class FriendshipPaginateDatabaseRepo extends AbstractPaginateDatabaseRepo<Tuple<Long,Long>, Friendship> {
    public FriendshipPaginateDatabaseRepo(String URL, String ID, String PASS, Validator<Friendship> validator, int pageSize) {
        super(URL, ID, PASS, validator, pageSize);
    }

    @Override
    protected ResultSet getResultSet(Statement statement) throws SQLException {
        return statement.executeQuery("SELECT * FROM public.friendships");
    }

    @Override
    protected Friendship readEntity(ResultSet resultSet) {
        try {
            return new Friendship(resultSet.getLong("user_1"),resultSet.getLong("user_2"), LocalDateTime.parse(resultSet.getString("date")));
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return null;
        }
    }

    @Override
    protected Friendship insertEntity(Friendship entity, Connection connection) {
        try (PreparedStatement prep = connection.prepareStatement("INSERT INTO public.friendships( user_1, user_2, date) VALUES (?, ?, ?);", Statement.RETURN_GENERATED_KEYS))
        {
            prep.setLong(1,entity.getId().getLeft());
            prep.setLong(2,entity.getId().getRight());
            prep.setString(3,entity.getDate().toString());
            prep.execute();
            return entity;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return null;
        }
    }

    @Override
    protected Friendship deleteEntity(Friendship entity, Connection connection) {
        try {
            connection.createStatement().execute("DELETE FROM public.friendships WHERE user_1=" + entity.getId().getLeft() + " AND user_2="+entity.getId().getRight() + ";");
            return entity;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return null;
        }
    }
}

package socialNetwork.repository.database;

import socialNetwork.domain.User;
import socialNetwork.domain.validators.Validator;

import java.sql.*;

public class UserPaginateDatabaseRepo extends AbstractPaginateDatabaseRepo<Long,User> {
    public UserPaginateDatabaseRepo(String URL, String ID, String PASS, Validator<User> validator, int pageSize) {
        super(URL, ID, PASS, validator, pageSize);
    }

    @Override
    protected ResultSet getResultSet(Statement statement) throws SQLException {
        return statement.executeQuery("SELECT * FROM public.users");
    }

    @Override
    protected User readEntity(ResultSet resultSet) {
        try {
            return new User(resultSet.getLong("cod_user"),
                    resultSet.getString("first_name"),
                    resultSet.getString("last_name"),
                    resultSet.getString("picpath"),
                    resultSet.getString("email"),
                    resultSet.getString("pass")
                    );

        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

    @Override
    protected User insertEntity(User entity, Connection connection) {
        try (PreparedStatement prep = connection.prepareStatement("INSERT INTO public.users( cod_user, first_name, last_name, email, picpath, pass) VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS))
        {
            prep.setLong(1,entity.getId());
            prep.setString(2,entity.getFirstName());
            prep.setString(3,entity.getLastName());
            prep.setString(4,entity.getEmail());
            prep.setString(5,entity.getPicture());
            prep.setString(6,entity.getPassword());
            prep.execute();
            return entity;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return null;
        }
    }

    @Override
    protected User deleteEntity(User entity, Connection connection) {
        try {
            connection.createStatement().execute("DELETE FROM public.users WHERE cod_user = " + entity.getId() + ";");
            return entity;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return null;
        }
    }
}

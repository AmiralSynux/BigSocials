package socialNetwork.repository.database;

import socialNetwork.domain.Event;
import socialNetwork.domain.validators.Validator;
import socialNetwork.repository.Book;
import socialNetwork.repository.Paginator;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class EventsPaginateDatabaseRepo extends AbstractPaginateDatabaseRepo<Long, Event> {

    public EventsPaginateDatabaseRepo(String URL, String ID, String PASS, Validator<Event> validator, int pageSize) {
        super(URL, ID, PASS, validator, pageSize);
    }

    @Override
    protected ResultSet getResultSet(Statement statement) throws SQLException {
        return statement.executeQuery("SELECT * FROM events");
    }

    @Override
    protected Event readEntity(ResultSet resultSet) {
        try(Connection connection = connect())
        {
            Event event = new Event(
                    resultSet.getString("event_name"),
                    LocalDateTime.parse(resultSet.getString("event_date")),
                    resultSet.getString("event_place"),
                    resultSet.getString("event_description")
                    );
            event.setId(resultSet.getLong("event_id"));
            ResultSet res = connection.createStatement().executeQuery("select user_id from events_participants where event_id="+resultSet.getLong("event_id")+";");
            while (res.next()){
                event.addFollower(res.getLong("user_id"));
            }
            return event;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected Event insertEntity(Event entity, Connection connection) {
        try (PreparedStatement prep = connection.prepareStatement("INSERT INTO events(event_id, event_name, event_date, event_place, event_description) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS))
        {
            prep.setLong(1,entity.getId());
            prep.setString(2,entity.getName());
            prep.setString(3,entity.getDateTime().toString());
            prep.setString(4,entity.getPlace());
            prep.setString(5,entity.getDescription());
            prep.execute();
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
            return null;
        }
        try(PreparedStatement prep = connection.prepareStatement("INSERT INTO events_participants(event_id, user_id) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS))
        {
            prep.setLong(1,entity.getId());
            for(Long id : entity.getIDs()){
                prep.setLong(2,id);
                prep.execute();
            }
            return entity;
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override
    protected Event deleteEntity(Event entity, Connection connection) {
        try(PreparedStatement prep = connection.prepareStatement("DELETE FROM events WHERE event_id=" + entity.getId()+";"))
        {
            prep.execute();
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
            return null;
        }
        try(PreparedStatement prep = connection.prepareStatement("DELETE FROM events_participants WHERE event_id=" + entity.getId()+";"))
        {
            prep.execute();
            return entity;
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
            return null;
        }
    }
}

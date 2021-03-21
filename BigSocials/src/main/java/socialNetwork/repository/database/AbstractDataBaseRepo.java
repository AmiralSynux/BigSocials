package socialNetwork.repository.database;

import socialNetwork.domain.Entity;
import socialNetwork.domain.validators.Validator;
import socialNetwork.repository.memory.InMemoryRepository;

import java.sql.*;
import java.util.Collection;
import java.util.List;

public abstract class AbstractDataBaseRepo<ID, E extends Entity<ID>> extends InMemoryRepository<ID,E> {
    
    private final String URL;
    private final String ID;
    private final String PASS;
    /**
     * Creates and returns a connection to the database
     * THE CONNECTION MUST BE CLOSED!
     * @return a connection to the database
     * @throws SQLException if the connection fails
     */
    protected Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, ID, PASS);
    }
    public AbstractDataBaseRepo(String URL,String ID,String PASS, Validator<E> validator) {
        super(validator);
        this.URL = URL;
        this.ID = ID;
        this.PASS = PASS;
        try(Connection c = connect()) {
            loadData(getResultSet(c.createStatement()));
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            System.out.println(throwable.getMessage());
        }

    }


    /**
     * returns the ResultSet used for populating the repo
     * @param statement - the statement that will be used to run the sql command
     * @return - the resultSet of the sql command
     * @throws SQLException - if anything fails
     */
    protected abstract ResultSet getResultSet(Statement statement) throws SQLException;
    /**
     * loads data by a statement
     * @param resultSet - the results given by the query to be loaded in memory
     */
    protected void loadData(ResultSet resultSet) {
        try{
            //entities.clear();
            while (resultSet.next()) {
                E entity = readEntity(resultSet);
                super.save(entity);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
            System.out.println(exception.getMessage());
        }
    }

    @Override
    public Collection<E> findAll() {
//        try (Connection c = connect()){
//            loadData(getResultSet(c.createStatement()));
//        }catch (Exception e){
//            System.out.println(e.getMessage());
//        }
        return super.findAll();
    }

    /**
     * reads one entity from the database
     * @param resultSet - the entity under a resultSet form
     * @return - the read entity
     */
    protected abstract E readEntity(ResultSet resultSet);

    /**
     * inserts and entity into database
     * @param entity - the entity that will be inserted
     * @param connection - the connection used to connect to the database
     * @return null, if it is inserted, the entity back otherwise
     */
    protected abstract E insertEntity(E entity, Connection connection);
    /**
     * deletes and entity from database
     * @param entity - the entity that will be deleted
     * @param connection - the connection used to connect to the database
     * @return the entity if it is deleted, null otherwise
     */
    protected abstract E deleteEntity(E entity, Connection connection);



    @Override
    public E save(E entity) {
        E e=super.save(entity);
        if (e==null)
        {
            try (Connection c = connect()){
                insertEntity(entity,c);
            } catch (SQLException throwable) {
                throwable.printStackTrace();
                System.out.println("couldn't connect! - " + throwable.getMessage());
            }
            return null;
        }
        return e;
    }

    @Override
    public E delete(ID id) {
        E entity =  super.delete(id);
        if(entity==null)return null;
        try (Connection c = connect()){
            deleteEntity(entity,c);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            System.out.println("couldn't connect! - " + throwable.getMessage());
        }
        return entity;
    }

    @Override
    public E update(E entity) {
        if(super.update(entity)==null){
            try (Connection c = connect()){
                deleteEntity(entity,c);
                insertEntity(entity,c);
                return null;
            } catch (SQLException throwable) {
                throwable.printStackTrace();
                System.out.println("couldn't connect! - " + throwable.getMessage());
            }
        }
        return entity;
    }
}


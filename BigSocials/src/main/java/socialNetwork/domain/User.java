package socialNetwork.domain;

import socialNetwork.exceptions.AlreadyExistsException;
import socialNetwork.exceptions.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User extends Entity<Long>{
    private String firstName;
    private String lastName;
    private List<User> friends;
    private String picture;
    private String password;
    private String email;

    /**
     *  Creates an user with the given params
     * @param firstName - first Name
     * @param lastName - last Name
     */
    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.email = email;
        friends = new ArrayList<>();
    }

    public void setFriends(List<User> friends){
        this.friends = friends;
    }

    public User(long ID){
        setId(ID);
        friends=new ArrayList<>();
    }

    public User(long ID, String firstName, String lastName, String picture, String email, String password) {
        setId(ID);
        this.firstName = firstName;
        this.lastName = lastName;
        friends = new ArrayList<>();
        this.picture = picture;
        this.password = password;
        this.email = email;
    }

    public String getEmail(){
        return email;
    }

    public String getPassword(){
        return password;
    }

    /**
     * removes another user from the friend list of this user
     * @param another not null
     * @throws NotFoundException
     *          if the given user isn't in the friend list
     * @throws IllegalArgumentException
     *          if the given user is null
     */
    public void deleteFriend(User another){
        if(another==null)throw new IllegalArgumentException("User can't be null");
        if(!friends.remove(another))
            throw new NotFoundException("The users aren't friends!");
    }


    /**
     * ads another user to the friend list of the user
     * @param another not null
     * @throws AlreadyExistsException
     *          if the given user is already in the list, or if it's this user
     * @throws IllegalArgumentException
     *          if the given user is null
     */
    public void befriend(User another){
        if(another==null)throw new IllegalArgumentException("User can't be null");
        if(friends.contains(another))
            throw new AlreadyExistsException("Users are already friends!");
        if(another == this)
            throw new AlreadyExistsException("You can't befriend yourself!");
        friends.add(another);
    }

    /***
     * @return the first name of the user
     */
    public String getFirstName() {
        return firstName;
    }

    /***
     * sets the first name of the user
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /***
     * @return the last name of the user
     */
    public String getLastName() {
        return lastName;
    }

    /***
     * sets the last name of the user
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return the list of friends of this user
     */
    public List<User> getFriends() {
        return friends;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User that = (User) o;
        return getEmail().equals(that.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmail());
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
package socialNetwork.service;

import socialNetwork.domain.*;
import socialNetwork.exceptions.*;
import socialNetwork.observerPattern.ObservableClass;
import socialNetwork.observerPattern.UpdateBehaviour;
import socialNetwork.repository.Book;
import socialNetwork.repository.PaginateRepository;
import socialNetwork.repository.Paginator;
import socialNetwork.utils.Graph;
import socialNetwork.utils.PasswordMaster;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Service extends ObservableClass {

    private final PaginateRepository<Long, User> userRepository;
    private final PaginateRepository<Tuple<Long, Long>, Friendship> friendshipRepository;
    private final PaginateRepository<Long, Message> messageRepository;
    private final PaginateRepository<Long, FriendRequest> friendRequestRepository;
    private final PaginateRepository<Long, Event> eventPaginateRepository;

    /**
     *  constructor, each param is the "storage" of desired entities
     * @param userRepository must not be null
     * @param friendshipRepository must not be null
     * @param messageRepository must not be null
     * @param friendRequestRepository must not be null
     * @throws IllegalArgumentException
     *                   if at least one of the params is null
     * @throws NotFoundException
     *                   if the links between the repositories aren't good
     */
    public Service(PaginateRepository<Long, User> userRepository,
                   PaginateRepository<Tuple<Long, Long>, Friendship> friendshipRepository,
                   PaginateRepository<Long, Message> messageRepository,
                   PaginateRepository<Long, FriendRequest> friendRequestRepository,
                   PaginateRepository<Long, Event> eventPaginateRepository) {
        if(userRepository==null || friendshipRepository==null || messageRepository==null)
            throw new IllegalArgumentException("Repositories not supplied.");
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
        this.messageRepository = messageRepository;
        this.friendRequestRepository = friendRequestRepository;
        this.eventPaginateRepository = eventPaginateRepository;
        initItems();
    }
    private void initItems() {
        initFriendships();
        initMessages();
        initRequests();
    }

    /**
     * Initialises all friendships
     * Should be called only by the constructor
     */
    private void initFriendships() {
        for(Friendship x : friendshipRepository.findAll())
            addFriend(userRepository.findOne(x.getId().getLeft()), userRepository.findOne(x.getId().getRight()));
    }

    /**
     * Initialises all friend requests
     * Should be called only by the constructor
     */
    private void initRequests() {
        for(FriendRequest request : friendRequestRepository.findAll())
        {
            request.setReceiver(userRepository.findOne(request.getReceiver().getId()));
            request.setSender(userRepository.findOne(request.getSender().getId()));
        }
    }

    /**
     * Initialises all messages
     * Should be called only by the constructor
     */
    private void initMessages() {
        for(Message m : messageRepository.findAll())
        {
            m.setFrom(userRepository.findOne(m.getFrom().getId()));
            ArrayList<User> to = new ArrayList<>();
            for (User user : m.getTo() )
                to.add(userRepository.findOne(user.getId()));
            m.setTo(to);
        }
        for (Message m : messageRepository.findAll())
            if(m.getReply()!=null)
                m.setReply(messageRepository.findOne(m.getReply().getId()));
    }

    /**
     * the number of communities is considered the number of connected components of the friendship connections
     * @return the number of communities
     */
    public int communitiesNr(){
        ArrayList<User> users = new ArrayList<>(userRepository.findAll());
        return Graph.getConnectedComponents(users);
    }

    /**
     * The most sociable community is the longest elementary path in the friends communities
     * @return a list of users of the most sociable community
     */
    public List<User> theMostSociableCommunity(){
        ArrayList<User> users = new ArrayList<>(userRepository.findAll());
        return Graph.getLongestPath(users);
    }

    /**
     *  removes the friendship between two users
     *  this function notifies the observers
     * @param one must not be null
     * @param two must not be null
     * @throws IllegalArgumentException
     *                   if at least one of the params is null
     * @throws NotFoundException
     *                   if the friendship doesn't exists
     */
    public void removeFriend(User one, User two){
        if(one==null || two==null)throw new IllegalArgumentException("Illegal friendship!");
        one.deleteFriend(two);
        two.deleteFriend(one);
        long oneId = one.getId();
        long twoId = two.getId();
        deleteFriendship(oneId,twoId);
        notifyObservers(UpdateBehaviour.removeFriend);
    }

    /**
     *  removes the friendship from friendshipRepository given the users ID-s
     * @param one must not be null
     * @param two must not be null
     * @throws IllegalArgumentException
     *                  if at least one of the params is null
     * @throws NotFoundException
     *                  if there isn;t a friendship between the given ID-s
     */
    private void deleteFriendship(Long one, Long two){
        if(one==null || two == null) throw new IllegalArgumentException("Illegal friendship!");
        if(friendshipRepository.delete(new Tuple<>(Math.min(one, two),Math.max(one, two)))==null)
            throw new NotFoundException("There is no friendship");
    }

    /**
     * finds and returns a friendship with ID made by the 2 given arguments
     * the order of ID-s is irrelevant
     * @param one user ID
     * @param two other user ID
     * @return the friendship
     * @throws IllegalArgumentException
     *                  if at least one of the arguments is null
     * @throws NotFoundException
     *                  if the friendship isn't found
     */
    private Friendship getFriendship(Long one, Long two)
    {
        if(one==null || two == null) throw new IllegalArgumentException("Illegal friendship!");
        Friendship f = friendshipRepository.findOne(new Tuple<>(Math.min(one, two),Math.max(one, two)));
        if(f==null)
            throw new NotFoundException("There is no friendship");
        return f;
    }

    /**
     *  makes a connection between two users (befriends each other)
     * @param one must not be null
     * @param two must not be null
     * @throws IllegalArgumentException
     *                  if at least one of the params is null
     * @throws AlreadyExistsException
     *                  if there already is a connection
     */
    private void addFriend(User one, User two){
        if(one==null || two == null)
            throw new IllegalArgumentException("Users can't be null!");
        one.befriend(two);
        two.befriend(one);
    }

    /**
     *  adds an User in the application
     *  this function notifies the observers
     * @param user must not be null
     * @return null if the user was successfully added,
     *         the existent user otherwise
     * @throws IllegalArgumentException
     *                  if at least one of the params is null
     * @throws ValidationException
     *                  if the user isn't valid
     * @throws AlreadyExistsException
     *                  if the user already exists
     */
    public User addUser(User user) {
        if (user==null) throw new IllegalArgumentException("User can't be null!");
        if (existsUser(user.getEmail()))
            throw new AlreadyExistsException("Email already in use!");
        setID(user, userRepository.findAll());
        PasswordMaster passwordMaster = new PasswordMaster();
        user.setPicture("");
        user.setPassword(passwordMaster.hash(user.getPassword().toCharArray()));
        User aux = userRepository.save(user);
        if(aux==null)
        {
            notifyAllObservers();
            return null;
        }
        return aux;
    }

    /**
     *  removes an User by given ID
     *  this function notifies the observers
     * @param id must not be null
     * @throws IllegalArgumentException
     *                  if at least one of the params is null
     * @throws ValidationException
     *                  if the user isn't valid
     */
    public User removeUser(Long id){
        if(id==null)throw new IllegalArgumentException("ID can't be null");
        User user =  userRepository.delete(id);
        if(user==null) return null;
        detachFriends(user);
        detachMessages(user);
        detachFriendRequests(user);
        notifyAllObservers();
        return user;
    }

    /**
     * removes all the friend requests that belong to a given user
     * @param user - the given user
     */
    private void detachFriendRequests(User user) {
        ArrayList<FriendRequest> requests = new ArrayList<>(friendRequestRepository.findAll());
        for (FriendRequest request : requests)
            if(request.getSender().equals(user) || request.getReceiver().equals(user))
                friendRequestRepository.delete(request.getId());
    }

    /**
     * removes all the messages that belong to a given user
     * if the message is sent to more users, including the given one, the given user is deleted from
     * the receivers list
     * @param user - the given user
     */
    private void detachMessages(User user) {
        ArrayList<Message> messages = new ArrayList<>(messageRepository.findAll());
        for(Message message : messages)
        {
            if(message.getFrom().equals(user))
                messageRepository.delete(message.getId());
            else if(message.getTo().contains(user))
            {
                message.getTo().remove(user);
                if(message.getTo().size()==0)
                    messageRepository.delete(message.getId());
                else
                    messageRepository.update(message);
            }
        }
    }

    /**
     * removes the user from the list of friends of his friends. It also removes all his friendships
     * @param user - the given user
     */
    private void detachFriends(User user) {
        for(User friend : user.getFriends())
        {
            friend.deleteFriend(user);
            Long one = friend.getId();
            Long two = user.getId();
            deleteFriendship(one,two);
        }
    }

    /**
     * updates an user
     * @param logged - the user that wants to be updated
     * @param update - the user with the new updated fields
     * @param password - the password of the logged in user
     * @throws NotFoundException - if the password is invalid
     * @throws AlreadyExistsException - if the new email is already in use
     * @throws ValidationException - if the new user dates are not valid
     */
    public void updateUser(User logged, User update, String password){
        if (!logged.getEmail().equals(update.getEmail())) {
            if (existsUser(update.getEmail()))
                throw new AlreadyExistsException("Email already in use!");
        }
        PasswordMaster master = new PasswordMaster();
        if(master.authenticate(password.toCharArray(),logged.getPassword())){
            password = update.getPassword();
            update.setPassword(master.hash(update.getPassword().toCharArray()));
            update.setId(logged.getId());
            update.setFriends(logged.getFriends());
            userRepository.update(update);
            logged.setPassword(password);
            logged.setEmail(update.getEmail());
            notifyAllObservers();
        }
        else throw new NotFoundException("Invalid password!");
    }

    /**
     * returns an user by email and password
     * @return the user if found, otherwise null
     */
    public User loginUser(String email, String password){
        User log=null;
        for (User x : userRepository.findAll()){
            if(x.getEmail().equals(email))
                log = x;
        }
        if(log!=null)
        {
            PasswordMaster passwordMaster = new PasswordMaster();
            if(passwordMaster.authenticate(password.toCharArray(), log.getPassword()))
            {
                return log;
            }

        }
        return null;
    }

    /**
     * checks if an user exists (the user exists if the email is already in the database)
     * @param email - the identification key for the user
     * @return - true if it exists,
     *         - false otherwise
     */
    private boolean existsUser(String email){
        for (User x : userRepository.findAll()){
            if(x.getEmail().equals(email))
                return true;
        }
        return false;
    }

    /**
     * sets an ID to the entity type Entity<Long>
     * @param entity can't be null
     * @param list - the list with entities
     * @throws IllegalArgumentException
     *             if the entity is null
     */
    private <E extends Entity<Long>> void setID(Entity<Long> entity, Iterable<E> list) {
        if (entity==null)throw new IllegalArgumentException("Entity can't be null!");
        boolean ok=true;
        for(long i=0; ok ; i++)
        {
            boolean found=true;
            for(Entity<Long> x : list)
                if(x.getId()==i){
                    found=false;
                    break;
                }
            if(found){
                entity.setId(i);
                ok=false;
            }
        }
    }

    /**
     * Creates and adds a friendship between two users
     * @param one can't be null
     * @param two can't be null
     * @return null if the friendship was successfully created and added,
     *         the existent friendship otherwise
     * @throws IllegalArgumentException
     *          if at least one of the users is null
     * @throws ValidationException
     *          if the friendship isn't valid
     */
    private Friendship befriend(User one, User two){
        if(one==null || two==null)throw new IllegalArgumentException("Users can't be null");
        Friendship friendship =  new Friendship(Math.min(one.getId(),two.getId()),Math.max(one.getId(),two.getId()));
        friendship = friendshipRepository.save(friendship);
        if (friendship!=null)return friendship;
        addFriend(one,two);
        return null;
    }

    /**
     * Gets all friends and the date when the friendship was made, filtered by name / email
     * @param searchData - used to filter the friends by name / email. If this is null
     *      *                   or empty, all friends of this user will be added
     * @param user - the user that is asking for friendships
     * @return a filtered paginator with the user friends and the date the friendship was made.
     */
    public Paginator<UserFriendshipsDTO> getUserFriendships(User user, String searchData){
        List<UserFriendshipsDTO> elements = new ArrayList<>();
        user.getFriends().forEach(x->{
            String fullData = x.getFirstName()+" "+x.getLastName()+x.getEmail();
            if(fullData.contains(searchData))
            {
                Friendship fr = getFriendship(user.getId(), x.getId());
                elements.add(new UserFriendshipsDTO(x,fr.getDate()));
            }
        });
        return new Book<>(userRepository.getPageSize(),elements.stream().sorted(Comparator.comparing(UserFriendshipsDTO::getDate)).collect(Collectors.toList()));
    }

    /**
     * Gets all friends and the date when the friendship was made, filtered by month and year
     * @param user - the user that is asking for friendships
     * @return the user friends and the date
     */
    public Iterable<UserFriendshipsDTO> getUserFriendshipsFilter(User user, int month, int year) {
        return user.getFriends().stream()
                .map(
                    friend -> {
                        Friendship fr = getFriendship(user.getId(), friend.getId());
                        return new UserFriendshipsDTO(friend,fr.getDate());
                    })
                .filter(
                        friendship-> friendship.getDate().getMonthValue() == month && friendship.getDate().getYear() == year
                )
                .collect(Collectors.toList());

    }

    /**
     * get all Friendships
     * @return all Friendships
     */
    public Iterable<Friendship> getFriendships(){
        return friendshipRepository.findAll();
    }

    /**
     * get all users
     * @return all users
     */
    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * sends a message from @from to @to
     * this function notifies the observers
     * @param data - the data of the message
     * @param from - the sender
     * @param to - the receivers
     * @param reply - if it's a reply to a message, or null otherwise
     * @throws NotFoundException - if the user is trying to reply to a message that does not exists
     *                           - if it's not in his inbox
     * @throws ValidationException - if the message isn't logically valid
     */
    public void sendMessage(String data, User from, List<User> to,  Message reply){
        Message message = new Message(data,from,to, LocalDateTime.now(),reply);
        if(message.getReply()!=null)
            if(!message.getReply().getTo().contains(from))
                throw new NotFoundException("There is no such message in your inbox!");
        setID(message, messageRepository.findAll());
        messageRepository.save(message);
        notifyObservers(UpdateBehaviour.sentMessage);
    }

    /**
     * returns all messages of an user
     * (a message belongs to the user if the user sent the message or received it)
     * @param user the given user - must not be null
     * @return the messages of the given user
     * @throws IllegalArgumentException if the user is null
     */
    public Iterable<Message> getMessages(User user, boolean ascending)
    {
        if(user==null)
            throw new IllegalArgumentException("User can't be null!");
        ArrayList<Message> myMessages = new ArrayList<>();
        for (Message m : messageRepository.findAll())
        {
            if(m.getFrom().getId().equals(user.getId()))
                myMessages.add(m);
            else if(m.getTo().contains(user))
                myMessages.add(m);
        }
        if(ascending)
            myMessages.sort(Comparator.comparing(Message::getDate));
        else
            myMessages.sort((o1, o2) -> -o1.getDate().compareTo(o2.getDate()));
        return myMessages;
    }

    /**
     * used for changing the read state of a message
     * @param message - the message with the new read state
     */
    public void readMessage(Message message){
        message.setRead(true);
        messageRepository.update(message);
    }

    /**
     * Returns a message by ID
     * @param ID - the ID of the message (must not be null)
     * @return the message
     * @throws NotFoundException - if the message is not found
     * @throws IllegalArgumentException - if the ID is null
     */
    public Message getMessage(Long ID){
        Message m = messageRepository.findOne(ID);
        if(m==null)
            throw new NotFoundException("Message not found!\n");
        return m;
    }

    /**
     * Sends a friend request from @sender to @receiver
     * this function notifies the observers
     * @param sender - the sender of the friend request
     * @param receiver - the receiver of the friend request
     * @throws AlreadyExistsException - if a friend request is already in PENDING
     *                                - if the sender is the same as the receiver
     *                                - if the sender and the receiver are already friends
     * @throws ValidationException - if the friend request is not valid
     * @throws IllegalArgumentException - if any of the params is null
     */
    public void sendFriendRequest(User sender, User receiver){
        if(sender==null || receiver==null)
            throw new IllegalArgumentException("Senders and receivers can't be null!");
        if(sender.equals(receiver))
            throw new AlreadyExistsException("You can't send yourself a request");
        for(User friend : sender.getFriends())
            if(friend.equals(receiver))
                throw new AlreadyExistsException("You are already friends!\n");
        FriendRequest friendRequest = new FriendRequest(sender,receiver,FriendRequestStatus.PENDING);
        for(FriendRequest request : friendRequestRepository.findAll())
        {
            if(request.equals(friendRequest))
                throw new AlreadyExistsException("There already exists a friend request!\n");
        }
        setID(friendRequest, friendRequestRepository.findAll());
        friendRequestRepository.save(friendRequest);
        notifyObservers(UpdateBehaviour.sentRequest);
    }

    /**
     * Responds to a friend request with the status PENDING with ACCEPTED or REJECTED
     * if the user accepted the friend request, a friendship between the two is established
     * The status of the friend request is saved
     * this function notifies the observers
     * @param user - the user that responds to the friend request (the receiver)
     * @param friendRequestID - the ID of the friend request, the status of the friend request must be PENDING
     * @param status - the status of the response (ACCEPTED or REJECTED)
     * @throws NotFoundException - if the Friend request is not found
     *                           - if the friend request is found, but does not belong to the given user
     * @throws AlreadyExistsException - if the friend request is already answered
     * @throws ValidationException - if the friend request it's not logically valid
     * @throws IllegalArgumentException - if any of the params is null
     *                                  - if the status is PENDING
     */
    public void respondToFriendRequest(User user, Long friendRequestID, FriendRequestStatus status) {
        if(status==null || status.equals(FriendRequestStatus.PENDING))
            throw new IllegalArgumentException("Respond status can't be " + status + "!");
        FriendRequest friendRequest = friendRequestRepository.findOne(friendRequestID);
        if(friendRequest==null)
            throw new NotFoundException("Friend request not found!");
        if(!friendRequest.getStatus().equals(FriendRequestStatus.PENDING))
            throw new AlreadyExistsException("Friend request already answered!");
        if(friendRequest.getSender().equals(user) && status!=FriendRequestStatus.CANCELED)
            throw new NotFoundException("You can't answer to your own request!");
        if(friendRequest.getReceiver().equals(user) && status==FriendRequestStatus.CANCELED)
            throw new NotFoundException("You can only cancel your sent friend request!");
        if(status.equals(FriendRequestStatus.ACCEPTED))
            if(befriend(friendRequest.getSender(),user)!=null)
                throw new ApplicationException("An error encountered and the friendship couldn't be completed.");
        friendRequest.setStatus(status);
        friendRequestRepository.update(friendRequest);
        if(friendRequest.getStatus().equals(FriendRequestStatus.ACCEPTED))
            notifyObservers(UpdateBehaviour.newFriend);
        else
            notifyObservers(UpdateBehaviour.declineRequest);
    }

    /**
     * Returns all the friend requests in PENDING of a given user
     * (a friend request belongs to an user if he is the sender or the receiver)
     * @param user - the given user, must not be null
     * @return the user's friend requests (in PENDING)
     * @throws IllegalArgumentException - if the user is null
     */
    public List<FriendRequest> getFriendRequests(User user){
        if(user==null)throw new IllegalArgumentException("User can't be null!");
        List<FriendRequest> friendRequests = new ArrayList<>();
        for(FriendRequest request : friendRequestRepository.findAll())
            if((request.getReceiver().equals(user) || request.getSender().equals(user)) && request.getStatus().equals(FriendRequestStatus.PENDING))
                friendRequests.add(request);
        return friendRequests;
    }

    /**
     * Returns all the friend requests of a given user
     * (a friend request belongs to an user if he is the receiver or the sender)
     * @param user - the given user, must not be null
     * @return the user's friend requests
     * @throws IllegalArgumentException - if the user is null
     */
    public List<FriendRequest> getAllMyFriendRequests(User user){
        if(user==null)throw new IllegalArgumentException("User can't be null!");
        List<FriendRequest> friendRequests = new ArrayList<>();
        for(FriendRequest request : friendRequestRepository.findAll())
            if((request.getReceiver().equals(user) || request.getSender().equals(user)))
                friendRequests.add(request);
        return friendRequests;
    }

    /**
     * Returns a paginator of filtered friend request belonging to the given user.
     * A request belongs to a user if the user is the sender or receiver.
     * @param user - the user asking for friend request.
     * @param searchData - filters the request by sender / receiver (name / email).
     * @param sender - filters the request by sender
     *               - "All senders" / null / "" -> all senders
     *               - "Me" -> the sender is the user
     *               - anything else -> the sender is not the user
     * @param status - filters the request by status
     * @return a paginator with filtered friend request belonging to the given user
     * @throws IllegalArgumentException - if the user is null
     */
    public Paginator<FriendRequest> filterMyFriendRequest(User user, String searchData, String sender, FriendRequestStatus status){
        if(user==null)
            throw new IllegalArgumentException("User can't be null!");
        return friendRequestRepository.filter(x->{
            if(!(x.getReceiver().equals(user) || x.getSender().equals(user)))
                return false;
            if(!(filterStatus(x,status) && filterSender(x,sender,user)))
                return false;
            String fullData = x.getReceiver().getFirstName()+ " " + x.getReceiver().getLastName()
                    +   x.getReceiver().getEmail()
                    +   x.getSender().getFirstName()+ " " + x.getSender().getLastName()
                    +   x.getSender().getEmail();
            return fullData.contains(searchData);
        });
    }

    /**
     * checks if a sender matches the filter (sender)
     * @param friendRequest - the friend request that is checked
     * @param sender - filter type
     * @param user - the user the friendRequest's belongs to
     * @return - true, if the filter matches.
     *         - false, otherwise
     */
    private boolean filterSender(FriendRequest friendRequest, String sender, User user) {
        if(sender==null || sender.equals(""))
            return true;
        if(sender.equals("All senders"))
            return true;
        if(sender.equals("Me"))
            return friendRequest.getSender().equals(user);
        else
            return !friendRequest.getSender().equals(user);
    }

    /**
     * checks if a friend request matches the filter (status)
     * @param friendRequest - the friend request that is checked
     * @param status - the status filter
     * @return - true, if the friendRequest matches the filter.
     *         - false, otherwise
     */
    private boolean filterStatus(FriendRequest friendRequest, FriendRequestStatus status) {
        return status == FriendRequestStatus.ALL || friendRequest.getStatus().equals(status);
    }

    /**
     * Returns all the friend requests of a given user
     * (a friend request belongs to an user if he is the receiver)
     * @param user - the given user (the receiver of the requests), must not be null
     * @return the user's friend requests (the given user is the receiver)
     * @throws IllegalArgumentException - if the user is null
     */
    public Iterable<FriendRequest> getFriendRequestToMe(User user){
        if(user==null)throw new IllegalArgumentException("User can't be null!");
        List<FriendRequest> friendRequests = new ArrayList<>();
        friendRequestRepository.findAll().forEach(x->{
            if(x.getReceiver().equals(user))
                friendRequests.add(x);
        });
        return friendRequests;
    }

    /**
     * returns all foreign users of an user, filtered by name and email.
     * @param user - the user asking for filtering
     * @param search - adds the foreign users only if the name or email has this string. If this string is empty or null,
     *               all foreign users will be added
     * @return a paginator of filtered foreign users
     * @throws IllegalArgumentException - if the user is null
     */
    public Paginator<User> getFilteredUsers(User user, String search){
        if(user==null)
            throw new IllegalArgumentException("User can't be null!");
        List<FriendRequest> friendRequests = getFriendRequests(user);
        return userRepository.filter(x->{
            String fullData = x.getFirstName() + " " + x.getLastName() + x.getEmail();
            return !user.equals(x)
                    && fullData.contains(search)
                    && !user.getFriends().contains(x)
                    && !friendRequests.contains(new FriendRequest(user, x, FriendRequestStatus.PENDING));
        });
    }

    /**
     * returns all messages received by an user, filtered by sender(name / email) / content
     * @param user - the user asking for the inbox page
     * @param searchData - used to filter the inbox by sender(name / email) / content. If this is null
     *                   or empty, all received messages of this user will be added
     * @return a paginator of the filtered inbox of an user
     * @throws IllegalArgumentException - if the user is null
     */
    public Paginator<Message> getInboxPage(User user, String searchData){
        if(user==null)
            throw new IllegalArgumentException("User can't be null!");
        List<Message> inbox = new ArrayList<>();
        messageRepository.findAll().forEach(x->{
            User from = x.getFrom();
            String fullData = from.getEmail() + from.getFirstName() + " " + from.getLastName() + x.getData();
            if(fullData.contains(searchData) && x.getTo().contains(user))
                inbox.add(x);
        });
        inbox.sort((o1, o2) -> -o1.getDate().compareTo(o2.getDate()));
        return new Book<>(messageRepository.getPageSize(),inbox);
    }

    /**
     * returns all messages sent by an user, filtered by receivers(name / email) / content
     * @param user - the user asking for the sent page
     * @param searchData - used to filter the sent messages by receivers(name / email) / content. If this is null
     *                   or empty, all sent messages of this user will be added
     * @return a paginator of the filtered sent messages of an user
     * @throws IllegalArgumentException - if the user is null
     */
    public Paginator<Message> getSendPage(User user, String searchData){
        if(user==null)
            throw new IllegalArgumentException("User can't be null!");
        List<Message> sent = new ArrayList<>();
        messageRepository.findAll().forEach(x->{
            if(x.getFrom().equals(user))
            {
                if(!(searchData==null || searchData.length()==0)){
                    StringBuilder mails = new StringBuilder();
                    StringBuilder fullNames = new StringBuilder();
                    for(User userTo : x.getTo()){
                        mails.append(userTo.getEmail());
                        fullNames.append(userTo.getFirstName()).append(" ").append(userTo.getLastName());
                    }
                    String fullData = String.valueOf(mails) +
                            fullNames +
                            x.getData();
                    if(fullData.contains(searchData))
                        sent.add(x);
                }
                else
                    sent.add(x);
            }
        });
        sent.sort((o1, o2) -> -o1.getDate().compareTo(o2.getDate()));
        return new Book<>(messageRepository.getPageSize(),sent);
    }

    /**
     * filters all the events and orders them by date
     * @param filter - the filter used. (place / description / name). if this is empty, the filter is ignored
     * @return a paginator with the events
     */
    public Paginator<Event> getFilteredEvents(String filter) {
        return eventPaginateRepository.filter(x->{
            String fullData = x.getPlace() + x.getDescription() + x.getName();
            return x.getDateTime().isAfter(LocalDateTime.now().minusHours(1)) && fullData.contains(filter);
        },Comparator.comparing(Event::getDateTime));
    }

    /**
     * make an user follow an event
     * @param user - the user that wants to follow the given event
     * @param event - the event that the user wants to follow
     * @throws IllegalArgumentException - if the user or the event are null
     * @throws ValidationException - if the event is not logically valid
     * @throws AlreadyExistsException - if the user is already following the even
     * @throws NotFoundException - if the event is not found
     */
    public void followEvent(User user, Event event) {
        if(user==null)
            throw new IllegalArgumentException("User can't be null!");
        if(event==null)
            throw new IllegalArgumentException("Event can't be null!");
        if(!event.addFollower(user.getId()))
            throw new AlreadyExistsException("Event already followed!");
        if(eventPaginateRepository.update(event)!=null){
            throw new NotFoundException("Event not found!");
        }
        notifyObservers(UpdateBehaviour.followEvent);
    }

    /**
     * filters all the events and orders them by date
     * @param user - the user that is asking for his followed events
     * @param filter - the filter used. (place / description / name). if this is empty, the filter is ignored
     * @return a paginator with the events that a user is following
     */
    public Paginator<Event> getFollowedFilteredEvents(User user, String filter) {
        return eventPaginateRepository.filter(x->{
            String fullData = x.getPlace() + x.getDescription() + x.getName();
            return x.getDateTime().isAfter(LocalDateTime.now().minusHours(1))
                    && x.getIDs().contains(user.getId()) && fullData.contains(filter);
        },Comparator.comparing(Event::getDateTime));
    }

    /**
     * make an user follow an event
     * @param user - the user that wants to follow the given event
     * @param event - the event that the user wants to follow
     * @throws IllegalArgumentException - if the user or the event are null
     * @throws ValidationException - if the event is not logically valid
     * @throws AlreadyExistsException - if the user is already following the even
     * @throws NotFoundException - if the event is not found
     */
    public void unfollowEvent(User user, Event event) {
        if(user==null)
            throw new IllegalArgumentException("User can't be null!");
        if(event==null)
            throw new IllegalArgumentException("Event can't be null!");
        if(!event.removeFollower(user.getId()))
            throw new AlreadyExistsException("Event not followed!");
        if(eventPaginateRepository.update(event)!=null){
            throw new NotFoundException("Event not found!");
        }
        notifyObservers(UpdateBehaviour.unfollowEvent);
    }

    /**
     * modifies, fully creates and adds the event
     * @param e - an event made with the basic constructor
     * @throws IllegalArgumentException - if the event is null
     * @throws AlreadyExistsException - if the event is in past
     *                                - if there already exists such an event
     * @throws ValidationException - if the event is not logically correct
     */
    public void createEvent(Event e){
        if(e==null)
            throw new IllegalArgumentException("Event can't be null!");
        if(e.getDateTime().isBefore(LocalDateTime.now()))
            throw new AlreadyExistsException("You can't create an event in the past!");
        for(Event event : eventPaginateRepository.findAll())
            if(event.equals(e))
                throw new AlreadyExistsException("Event already exists!");
        setID(e,eventPaginateRepository.findAll());
        if(eventPaginateRepository.save(e)!=null)
            throw new AlreadyExistsException("Event already exists!");
        notifyObservers(UpdateBehaviour.createEvent);
    }

    /**
     * used for showing the notification
     */
    public void notifyEvent(){
        notifyObservers(UpdateBehaviour.notifyClosestEvent);
    }
}

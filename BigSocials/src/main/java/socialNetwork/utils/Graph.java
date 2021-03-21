package socialNetwork.utils;

import socialNetwork.domain.User;

import java.util.ArrayList;
import java.util.List;

public class Graph {

    /**
     * Calculates the longest path of an adjacency list (the links are friends)
     * @param users - adjacency list of users
     * @return an ArrayList that contains the users in the longest path
     */
    public static List<User> getLongestPath(List<User> users){
        List<User> longestPath = new ArrayList<>();
        for(User user : users){
            List<User> friends = user.getFriends();
            ArrayList<User> traveled = new ArrayList<>();
            traveled.add(user);
            travelAndSave(friends,traveled,longestPath);
            traveled.clear();
        }
        return longestPath;
    }

    /**
     *  @param friends - adjacency list of users (links are the friends)
     * @param traveled - the path of friends that are already traveled
     * @param longestPath - the longest path
     */
    private static void travelAndSave(List<User> friends, List<User> traveled, List<User> longestPath) {
        ArrayList<User> savePoint = new ArrayList<>(traveled);
        for(User user : friends){
            if(!traveled.contains(user)) {
                List<User> newFriends = user.getFriends();
                ArrayList<User> newTraveled = new ArrayList<>(savePoint);
                newTraveled.add(user);
                travelAndSave(newFriends, newTraveled, longestPath);
                if (longestPath.size() < newTraveled.size()) {
                    longestPath.clear();
                    longestPath.addAll(newTraveled);
                }
            }
        }
    }

    /**
     * Calculates the number of connected components of an adjacency list (the links are friends)
     * @param users - adjacency list of users
     * @return the number of connected components
     */
    public static int getConnectedComponents(List<User> users){
        List<User> traveled = new ArrayList<>();
        int cati=0;
        for(User user : users)
        {
            if(!traveled.contains(user)){
                traveled.add(user);
                List<User> friends = user.getFriends();
                travel(friends,traveled);
                cati++;
            }
        }
        return cati;
    }

    /**
     *
     * @param friends users and friends of the users that will be added to the traveled list
     * @param traveled friends that are already traveled
     */
    private static void travel(List<User> friends,  List<User> traveled){
        for(User user: friends)
        {
            if(!traveled.contains(user)){
                traveled.add(user);
                travel(user.getFriends(), traveled);
            }
        }
    }
}

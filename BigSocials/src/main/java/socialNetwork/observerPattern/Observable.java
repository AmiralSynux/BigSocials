package socialNetwork.observerPattern;

public interface Observable {
    /**
     * adds an observer that can be notified
     * @param obs - the observer needed
     * @throws IllegalArgumentException if the observer is null
     */
    void addObserver(Observer obs);

    /**
     * notify all the observers that a change has been made
     */
    void notifyAllObservers();

    /**
     * notify a specific set of Observers
     */
    void notifyObservers(UpdateBehaviour updateBehaviour);

    /**
     * removes an observer from the notifiable list
     */
    void removeObserver(Observer obs);

    /**
     * removes all observers from the notifiable list
     */
    void removeAllObservers();
}

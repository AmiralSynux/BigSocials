package socialNetwork.observerPattern;

public interface Observer {
    /**
     * executes an update when notified by the observable object
     */
    void executeUpdate();

    /**
     * executes an update only if the updateBehaviour matches the updates needed
     */
    void executeUpdate(UpdateBehaviour updateBehaviour);
}

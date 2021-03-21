package socialNetwork.observerPattern;

import java.util.ArrayList;
import java.util.List;

public class ObservableClass implements Observable{

    List<Observer> observers = new ArrayList<>();

    @Override
    public void addObserver(Observer obs) {
        if(obs==null)
            throw new IllegalArgumentException("Observer can't be null");
        observers.add(obs);
    }

    @Override
    public void notifyAllObservers() {
        observers.forEach(Observer::executeUpdate);
    }

    @Override
    public void notifyObservers(UpdateBehaviour updateBehaviour) {
        observers.forEach(x-> x.executeUpdate(updateBehaviour));
    }


    @Override
    public void removeObserver(Observer obs) {
        observers.remove(obs);
    }

    @Override
    public void removeAllObservers() {
        observers.clear();
    }
}

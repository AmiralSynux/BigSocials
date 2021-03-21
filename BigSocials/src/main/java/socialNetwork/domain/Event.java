package socialNetwork.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Event extends Entity<Long>{
    private List<Long> IDs;
    private LocalDateTime dateTime;
    private String place;
    private String name;
    private String description;

    public Event(String name,LocalDateTime dateTime,String place,String description ) {
        this.dateTime = dateTime;
        this.place = place;
        this.name = name;
        this.description = description;
        IDs = new ArrayList<>();
    }

    public boolean addFollower(Long id){
        if(IDs.contains(id))
            return false;
        IDs.add(id);
        return true;
    }

    public boolean removeFollower(Long id){
        return IDs.remove(id);
    }

    public List<Long> getIDs() {
        return IDs;
    }

    public void setIDs(List<Long> IDs) {
        this.IDs = IDs;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(dateTime, event.dateTime) &&
                Objects.equals(place, event.place) &&
                Objects.equals(name, event.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateTime, place, name);
    }
}

package socialNetwork.domain;

import socialNetwork.utils.Constants;

import java.time.LocalDateTime;
import java.util.Objects;

public class Friendship extends Entity<Tuple<Long,Long>> {

    private final LocalDateTime date;
    public Friendship(User one, User two) {
        super();
        setId(new Tuple<>(one.getId(), two.getId()));
        date = LocalDateTime.now();
    }
    public Friendship(Long one, Long two) {
        super();
        setId(new Tuple<>(one, two));
        date = LocalDateTime.now();
    }

    public Friendship(Long one, Long two, LocalDateTime date) {
        super();
        setId(new Tuple<>(one, two));
        this.date = date;
    }

    @Override
    public String toString() {
        return "{ " + getId().getLeft() + "; " + getId().getRight() + "; " + date.format(Constants.DATE_TIME_FORMATTER) + " }";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Friendship that = (Friendship) o;
        return (this.getId().getLeft().equals(that.getId().getLeft()) && this.getId().getRight().equals(that.getId().getRight())) ||
                (this.getId().getLeft().equals(that.getId().getRight()) && this.getId().getRight().equals(that.getId().getLeft()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(date);
    }

    /**
     * @return the date when the friendship was created
     */
    public LocalDateTime getDate() {
        return date;
    }
}

package socialNetwork.repository;

import socialNetwork.domain.Entity;

import java.util.Comparator;
import java.util.function.Predicate;

public interface PaginateRepository <ID, E extends Entity<ID>> extends Repository<ID,E>, Paginator<E>{

    /**
     * filters the entities and orders them if necessary
     * @param filter - the filter used
     * @param comparator - the comparator used for ordering(if provided)
     * @return a paginator with the filtered (and ordered) entities
     */
    Paginator<E> filter(Predicate<E> filter, Comparator<E>... comparator);
}

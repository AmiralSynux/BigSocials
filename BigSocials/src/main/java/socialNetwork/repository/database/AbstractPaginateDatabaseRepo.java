package socialNetwork.repository.database;

import socialNetwork.domain.Entity;
import socialNetwork.domain.Event;
import socialNetwork.domain.validators.Validator;
import socialNetwork.repository.Book;
import socialNetwork.repository.PaginateRepository;
import socialNetwork.repository.Paginator;

import java.util.Collection;
import java.util.Comparator;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class AbstractPaginateDatabaseRepo<ID, E extends Entity<ID>> extends AbstractDataBaseRepo<ID,E> implements PaginateRepository<ID, E>{

    private final int pageSize;
    private int currentPage;

    @Override
    public Paginator<E> filter(Predicate<E> filter, Comparator<E>... comparator) {
        if(comparator.length==0 || comparator[0]==null)
            return new Book<E>(pageSize,findAll().stream().filter(filter).collect(Collectors.toList()));
        else
            return new Book<E>(pageSize,findAll().stream().filter(filter).sorted(comparator[0]).collect(Collectors.toList()));
    }

    public AbstractPaginateDatabaseRepo(String URL, String ID, String PASS, Validator<E> validator, int pageSize) {
        super(URL, ID, PASS, validator);
        this.pageSize = pageSize;
        currentPage = 0;
    }

    @Override
    public Iterable<E> getPage(int page) {
        if(page<0)
            currentPage=0;
        else
            currentPage = Math.min(currentPage,getTotalPages());
        return getCurrentPage();
    }

    @Override
    public void firstPage() {
        currentPage=0;
    }

    @Override
    public Iterable<E> getCurrentPage() {
        int offset = pageSize * currentPage;
        return entities.values().stream()
                .skip(offset)
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    @Override
    public boolean nextPage() {
        if(currentPage ==getTotalPages())
            return false;
        currentPage++;
        return true;
    }

    @Override
    public boolean previousPage() {
        if(currentPage ==0)
            return false;
        currentPage--;
        return true;
    }

    @Override
    public int getPageNumber() {
        return currentPage;
    }

    @Override
    public int getTotalPages() {
        int enough;
        int totalElements;
        totalElements = entities.size();
        if(totalElements==0 || totalElements % pageSize > 0)
            enough=0;
        else enough=1;
        return (totalElements / pageSize) - enough ;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

}


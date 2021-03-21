package socialNetwork.repository;

import java.util.List;
import java.util.stream.Collectors;

public class Book<E> implements Paginator<E> {
    private final int pageSize;

    private int currentPage;

    private final List<E> elements;

    public Book(int pageSize, List<E> elements) {
        this.pageSize = pageSize;
        this.currentPage = 0;
        this.elements = elements;
    }

    @Override
    public Iterable<E> getCurrentPage() {
        int offset = pageSize * currentPage;
        return elements.stream()
                .skip(offset)
                .limit(pageSize)
                .collect(Collectors.toList());
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
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public int getTotalPages() {
        int enough;
        int totalElements;
        totalElements = elements.size();
        if(totalElements==0 || totalElements % pageSize > 0)
            enough=0;
        else enough=1;
        return (totalElements / pageSize) - enough ;
    }
}

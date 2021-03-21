package socialNetwork.repository;

public interface Paginator<E> {
    /**
     * @return all the elements of the current page
     */
    Iterable<E> getCurrentPage();

    /**
     * If the page index is bigger than the total pages, the index will be changed to the last page.
     * If the page index is smaller than 0, it will be changed to the minimum.
     * @param page - the index of the page
     * @return all the elements of the given page
     */
    Iterable<E> getPage(int page);

    /**
     * resets the repo to the first page
     */
    void firstPage();

    /**
     * takes the repo to the next page, if possible.
     * @return true, if the repo successfully moved to the next page.
     *         false, otherwise.
     */
    boolean nextPage();

    /**
     * takes the repo to the previous page, if possible.
     * @return true, if the repo successfully moved to the previous page.
     *         false, otherwise.
     */
    boolean previousPage();

    /**
     * @return the current page number
     */
    int getPageNumber();

    /**
     * @return the size of the page (how many elements are on one page)
     */
    int getPageSize();

    /**
     * @return the total pages in repo
     */
    int getTotalPages();
}

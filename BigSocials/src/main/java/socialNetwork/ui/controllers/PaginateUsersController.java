package socialNetwork.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import socialNetwork.config.ApplicationContext;
import socialNetwork.domain.validators.UserValidator;
import socialNetwork.repository.database.UserPaginateDatabaseRepo;

public class PaginateUsersController extends AbstractController{

    @FXML Label pageLabel;
    @FXML ListView<String> listView;

    UserPaginateDatabaseRepo repo;

    @Override
    protected void init() {
        String URLDatabase = ApplicationContext.getPROPERTIES().getProperty("URLDatabase");
        String ID = ApplicationContext.getPROPERTIES().getProperty("IDDatabase");
        String PASS = ApplicationContext.getPROPERTIES().getProperty("PASSDatabase");
        repo = new UserPaginateDatabaseRepo(URLDatabase,ID,PASS,new UserValidator(),1);
        initItems();
    }

    private void initItems(){
        listView.getItems().clear();
        repo.getCurrentPage().forEach(x->listView.getItems().add(x.getEmail()));
        pageLabel.setText(repo.getPageNumber() + "");
    }

    @FXML
    public void prevPage() {
        if(repo.previousPage())
            initItems();
    }

    @FXML
    public void nextPage() {
        if(repo.nextPage())
            initItems();
    }
}

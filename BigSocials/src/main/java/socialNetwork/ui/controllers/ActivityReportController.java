package socialNetwork.ui.controllers;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import socialNetwork.domain.Message;
import socialNetwork.domain.UserFriendshipsDTO;
import socialNetwork.repository.Paginator;
import socialNetwork.ui.stages.FirstPagePDFStage;
import socialNetwork.utils.PieChartDrawer;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.List;

public class ActivityReportController extends AbstractController{

    @FXML Label totalFriends;
    @FXML Label totalMessages;
    @FXML ListView<VBox> friendsListView;
    @FXML ListView<VBox> messagesListView;
    private LocalDate beginDate;
    private LocalDate endDate;
    List<UserFriendshipsDTO> friends;
    List<Message> messages;
    Map<Integer, List<UserFriendshipsDTO>> monthsFriends;
    Map<Integer, List<Message>> monthsMessage;
    @Override
    protected void init() {
        initiateParams();
        populatePieCharts();
    }

    private void initiateParams(){
        initFriendsMsg();
        initMonths();
        totalFriends.setText("FRIENDS -> Total of " + friends.size() + " new friend"+getS(friends.size()));
        totalMessages.setText("MESSAGES -> Total of " + messages.size() + " new message"+getS(friends.size()));
    }

    private void initMonths() {
        monthsFriends = new TreeMap<>();
        for(UserFriendshipsDTO friend : friends){
            Integer key = friend.getOnlyDate().getMonthValue();
            if(monthsFriends.containsKey(key))
                monthsFriends.get(key).add(friend);
            else
            {
                List<UserFriendshipsDTO> list = new ArrayList<>();
                list.add(friend);
                monthsFriends.put(key,list);
            }
        }
        monthsMessage = new TreeMap<>();
        for(Message message : messages){
            Integer key = message.getDate().getMonthValue();
            if(monthsMessage.containsKey(key))
                monthsMessage.get(key).add(message);
            else
            {
                List<Message> list = new ArrayList<>();
                list.add(message);
                monthsMessage.put(key,list);
            }
        }
    }

    private void initFriendsMsg() {
        friends = new ArrayList<>();
        Paginator<UserFriendshipsDTO> paginator = service.getUserFriendships(this.user,"");
        do{
            paginator.getCurrentPage().forEach(x->{
                if(x.getDate().toLocalDate().compareTo(beginDate)>=0 && x.getDate().toLocalDate().compareTo(endDate)<=0)
                    friends.add(x);
            });
        }while (paginator.nextPage());
        friends.sort(Comparator.comparing(UserFriendshipsDTO::getDate));
        messages = new ArrayList<>();
        service.getMessages(this.user,true).forEach(x->{
            if(x.getTo().contains(this.user) && x.getDate().toLocalDate().compareTo(beginDate)>=0 && x.getDate().toLocalDate().compareTo(endDate)<=0)
                messages.add(x);
        });
    }

    private void populatePieCharts() {
        PieChartDrawer.drawPies(messagesListView,monthsMessage.values(),"msg","Message");
        PieChartDrawer.drawPies(friendsListView,monthsFriends.values(),"friend","Friend");
    }


    private String getS(double val){
        if(val<2)
            return "";
        else
            return "s";
    }

    public void setDate(LocalDate begin, LocalDate end){
        this.beginDate = begin;
        this.endDate = end;
    }

    public void exportPDF() {
        if(messages.size()==0 && friends.size()==0)
        {
            showError("Nothing to export!");
            return;
        }

        //exports with the printer
        //exportWithPrinter();

        //exports with iText
        export();

    }

    private void exportWithPrinter() {
        PrinterJob job = PrinterJob.createPrinterJob();
        if(job != null){
            printFirstPage(job);
            printFriendsPages(job);
            printMessagesPages(job);
            job.endJob();
        }
    }

    private void printFirstPage(PrinterJob job) {
        Stage stage = new FirstPagePDFStage(service,user,"Activity report",beginDate,endDate);
        job.printPage(stage.getScene().getRoot());
    }

    private void printMessagesPages(PrinterJob job) {
        TableView<List<Message>> table = new TableView<>();
        TableColumn<List<Message>, String> dateColumn = new TableColumn<>("Month");
        TableColumn<List<Message>, String> totalColumn = new TableColumn<>("Total Messages");
        dateColumn.setCellValueFactory(x->new ReadOnlyStringWrapper(x.getValue().get(0).getDate().getMonth().toString()));
        totalColumn.setCellValueFactory(x->new ReadOnlyStringWrapper(x.getValue().size() + ""));
        ObservableList<List<Message>> obs = FXCollections.observableArrayList();
        obs.addAll(monthsMessage.values());
        table.getColumns().add(dateColumn);
        table.getColumns().add(totalColumn);
        dateColumn.setMaxWidth(100);
        totalColumn.setMaxWidth(100);
        dateColumn.setMinWidth(100);
        totalColumn.setMinWidth(100);
        dateColumn.setPrefWidth(100);
        totalColumn.setPrefWidth(100);
        table.setItems(obs);

        job.printPage(getVbox("MESSAGES", this.totalMessages,table));
        messagesListView.getItems().forEach(job::printPage);
    }

    private VBox getVbox(String title, Label total, Node table){
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        Label label = new Label(" --- " + title + " ---\n");
        label.setFont(javafx.scene.text.Font.font("Poor Richard",25));
        Label totalLabel = new Label(total.getText());
        totalLabel.setFont(total.getFont());
        vBox.getChildren().addAll(label,totalLabel,table);
        return vBox;
    }

    private void printFriendsPages(PrinterJob job) {
        TableView<List<UserFriendshipsDTO>> table = new TableView<>();
        TableColumn<List<UserFriendshipsDTO>, String> dateColumn = new TableColumn<>("Month");
        TableColumn<List<UserFriendshipsDTO>, String> totalColumn = new TableColumn<>("New Friends");

        dateColumn.setCellValueFactory(x->new ReadOnlyStringWrapper(x.getValue().get(0).getDate().getMonth().toString()));
        totalColumn.setCellValueFactory(x->new ReadOnlyStringWrapper(x.getValue().size() + ""));

        ObservableList<List<UserFriendshipsDTO>> obs = FXCollections.observableArrayList();
        obs.addAll(monthsFriends.values());
        totalColumn.setMaxWidth(100);
        dateColumn.setMaxWidth(100);
        totalColumn.setMinWidth(100);
        dateColumn.setMinWidth(100);
        totalColumn.setPrefWidth(100);
        dateColumn.setPrefWidth(100);
        table.getColumns().add(dateColumn);
        table.getColumns().add(totalColumn);
        table.setItems(obs);
        table.setPrefWidth( dateColumn.getPrefWidth() + totalColumn.getPrefWidth() + 3);
        job.printPage(getVbox("FRIENDS",totalFriends,table));
        friendsListView.getItems().forEach(job::printPage);
    }

    public void showFriendsInfo() {
        showNotify("The Friend chart are multiple PieCharts, each PieChart for a specific month.\n" +
                "Each PieChart is divided into days of the month.\n" +
                "Each day has an arrow pointing to how many new friends you had in the specified day");
    }

    public void showMessagesInfo() {
        showNotify("The Message chart are multiple PieCharts, each PieChart for a specific month.\n" +
                "Each PieChart is divided into days of the month.\n" +
                "Each day has an arrow pointing to how many new messages you had in the specified day");
    }

    public void helpDate(){
        showNotify("This report is from " + beginDate.toString() + " to " + endDate.toString() + ".");
    }

    public void export() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF file","*.pdf"));
        fileChooser.setInitialDirectory(new File("C:\\Users\\Amiral Synux\\Desktop\\"));
        File file = fileChooser.showSaveDialog(this.stage);
        if(file!=null)
        {
            Document document = new Document();
            try{
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();
                document.addTitle(file.getName());
                addContent(document);
                document.close();
                Desktop desktop = Desktop.getDesktop();
                desktop.open(file);
            }catch (Exception e){
                showError(e);
            }
        }
    }
    private final com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 18,
            com.itextpdf.text.Font.BOLD);
    private final com.itextpdf.text.Font normalFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 12,
            com.itextpdf.text.Font.NORMAL);

    private void addIntroduction(Document document) throws DocumentException {
        String fullName = "Full name: " + this.user.getLastName() + " " + this.user.getFirstName() + "\n";
        String mail ="Email: " + this.user.getEmail() + "\n";
        String report ="Report type: Activity Report\n";
        String date ="Created on: " + LocalDate.now().toString();
        String forDate = "\nReport from " + beginDate.toString() + " to " + endDate.toString();
        Paragraph paragraph = new Paragraph(fullName+mail+report+date+forDate, headerFont);
        document.add(paragraph);
    }

    private void addDescription(Document document) throws DocumentException{
        String desc = "\n\nAn activity report shows all the messages received and friends made over a timeframe. ";
        String Date = "In this case, the time frame is from " +beginDate.toString()+" to " + endDate.toString() + ".\n\n";
        String exp = "The following tables shows a summary of the messages and friends and the next ones show the messages and the friends\n\n";
        Paragraph paragraph = new Paragraph(desc+Date+exp,normalFont);
        document.add(paragraph);
    }

    private void addContent(Document document) throws DocumentException {
        addIntroduction(document);
        addDescription(document);
        addSummaryTable(document);
        document.add(new Paragraph("\n\n"));
        addTable(document);
    }

    private void addSummaryTable(Document document) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        PdfPCell c1 = new PdfPCell(new Phrase("Month"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(BaseColor.GRAY);
        table.addCell(c1);
        table.setHeaderRows(1);
        c1 = new PdfPCell(new Phrase("Total Messages"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(BaseColor.GRAY);
        table.addCell(c1);
        monthsMessage.forEach((x,y)-> addRow(table, Month.of(x),y.size()));
        document.add(table);

        document.add(new Paragraph("\n"));
        PdfPTable friendsTable = new PdfPTable(2);
        c1 = new PdfPCell(new Phrase("Month"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(BaseColor.GRAY);
        friendsTable.addCell(c1);
        c1 = new PdfPCell(new Phrase("Total Friends"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(BaseColor.GRAY);
        friendsTable.addCell(c1);
        friendsTable.setHeaderRows(1);
        monthsFriends.forEach((x,y)->addRow(friendsTable,Month.of(x),y.size()));
        document.add(friendsTable);
    }

    private void addRow(PdfPTable table, Month month, int nr){
        table.addCell(month.toString());
        table.addCell(nr + "");
    }

    private void addTable(Document document) throws DocumentException {
        PdfPTable messageTable = new PdfPTable(3);
        setHeader(messageTable,"From","Date","Message");
        messages.forEach(x-> addRow(x,messageTable));
        document.add(messageTable);
        document.add(new Paragraph("\n"));
        PdfPTable friendsTable = new PdfPTable(3);
        setHeader(friendsTable,"Name","Email","Date");
        friends.forEach(x->addRow(x,friendsTable));
        document.add(friendsTable);

    }

    private void addRow(UserFriendshipsDTO friend, PdfPTable table){
        table.addCell(friend.getLastName() + " " + friend.getFirstName());
        table.addCell(friend.getEmail());
        table.addCell(friend.getDate().toLocalDate().toString());
    }

    private void addRow(Message m, PdfPTable table){
        table.addCell(m.getFrom().getEmail());
        table.addCell(m.getDate().toLocalDate().toString());
        table.addCell(m.getData());
    }

    private void setHeader(PdfPTable table, String... strings) {

        for(String string : strings)
        {
            PdfPCell c1 = new PdfPCell(new Phrase(string));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            c1.setBackgroundColor(BaseColor.GRAY);
            table.addCell(c1);
        }

        table.setHeaderRows(1);
    }



}

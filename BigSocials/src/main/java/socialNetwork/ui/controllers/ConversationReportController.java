package socialNetwork.ui.controllers;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import socialNetwork.domain.Message;
import socialNetwork.domain.User;
import socialNetwork.utils.PieChartDrawer;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class ConversationReportController extends AbstractController{

    @FXML Label tableLabel;
    public ListView<VBox> listView;
    public TableView<Message> tableView;
    public TableColumn<Message,String> mailColumn;
    public TableColumn<Message,LocalDate> dateColumn;
    public TableColumn<Message,String> messageColumn;
    private LocalDate beginDate;
    private LocalDate endDate;
    private User friend;

    Map<Integer, java.util.List<Message>> monthsMessage;

    ObservableList<Message> obs;

    @Override
    protected void init() {
        initItems();
        initTable();
        initList();
        PieChartDrawer.drawPies(listView, monthsMessage.values(), "msg", "Message");
        tableLabel.setText(tableLabel.getText() + " -> Total of " + obs.size() + " message" + getS(obs.size()) + " received" );
    }

    private String getS(double val){
        if(val<2)
            return "";
        else
            return "s";
    }

    private void initList() {
        monthsMessage = new TreeMap<>();
        for(Message message : obs){
            Integer key = message.getDate().getMonthValue();
            if(monthsMessage.containsKey(key))
                monthsMessage.get(key).add(message);
            else
            {
                java.util.List<Message> list = new ArrayList<>();
                list.add(message);
                monthsMessage.put(key,list);
            }
        }
    }

    private void initTable() {
        mailColumn.setCellValueFactory(x->new ReadOnlyStringWrapper(x.getValue().getFrom().getEmail()));
        dateColumn.setCellValueFactory(x->new ReadOnlyObjectWrapper<>(x.getValue().getDate().toLocalDate()));
        messageColumn.setCellValueFactory(new PropertyValueFactory<>("data"));
        tableView.setItems(obs);
    }

    private void initItems() {
        obs = FXCollections.observableArrayList();
        service.getMessages(this.user,true).forEach(x->{
            if(x.getFrom().equals(friend) && x.getTo().contains(this.user) && x.getDate().toLocalDate().compareTo(beginDate)>=0 && x.getDate().toLocalDate().compareTo(endDate)<=0)
                obs.add(x);
        });
    }

    public void setExtraData(LocalDate begin, LocalDate end, User friend){
        this.friend = friend;
        this.beginDate = begin;
        this.endDate = end;
    }

    public void exportPDF() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("C:\\Users\\Amiral Synux\\Desktop\\"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF file","*.pdf"));
        File file = fileChooser.showSaveDialog(this.stage);
        if(file!=null)
        {
            Document document = new Document();
            try{
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
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
    private final Font headerFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
            Font.BOLD);
    private final Font normalFont = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.NORMAL);

    private void addIntroduction(Document document) throws DocumentException{
        String fullName = "Full name: " + this.user.getLastName() + " " + this.user.getFirstName() + "\n";
        String mail ="Email: " + this.user.getEmail() + "\n";
        String report ="Report type: Conversation Report\n";
        String date ="Created on: " + LocalDate.now().toString();
        String forDate = "\nReport from " + beginDate.toString() + " to " + endDate.toString();
        Paragraph paragraph = new Paragraph(fullName+mail+report+date+forDate, headerFont);
        document.add(paragraph);
    }

    private void addDescription(Document document) throws DocumentException{
        String desc = "\n\nA conversation report shows all the messages received from a friend over a timeframe. ";
        String Date = "In this case, the time frame is from " +beginDate.toString()+" to " + endDate.toString() + ".\n\n";
        String friendD = "This report will show the messages received from the user: " + friend.getFirstName()
                + " " + friend.getLastName() + ", using the Email: " + friend.getEmail() + ".\n\n";
        String exp = "The following table shows a summary of the messages and the next one shows the date and the message\n\n";
        Paragraph paragraph = new Paragraph(desc+Date+friendD+exp,normalFont);
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
        c1 = new PdfPCell(new Phrase("Total Messages"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(BaseColor.GRAY);
        table.addCell(c1);
        table.setHeaderRows(1);
        monthsMessage.forEach((x,y)-> addRow(table,Month.of(x),y.size()));
        document.add(table);
    }

    private void addRow(PdfPTable table, Month month, int msg){
        table.addCell(month.toString());
        table.addCell(msg + "");
    }

    private void addTable(Document document) throws DocumentException {
        PdfPTable table = new PdfPTable(3);
        setHeader(table);
        obs.forEach(x-> addRow(x,table));
        document.add(table);
    }

    private void addRow(Message m, PdfPTable table){
        table.addCell(m.getFrom().getEmail());
        table.addCell(m.getDate().toLocalDate().toString());
        table.addCell(m.getData());
    }

    private void setHeader(PdfPTable table) {
        PdfPCell c1 = new PdfPCell(new Phrase("Mail"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(BaseColor.GRAY);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Date"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(BaseColor.GRAY);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Message"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(BaseColor.GRAY);
        table.addCell(c1);

        table.setHeaderRows(1);
    }

    public void helpPieChart() {
        showNotify("The Conversation PieChart is a list of PieCharts of each month that had at least a message received.\n" +
                "At the beginning of each PieChart is the month of the PieChart and the number of messages received in that month.");
    }

    public void helpTable() {
        showNotify("The Conversation Table is a table that contains the mail of the sender, the date and the message. Above the table is shown the total of messages received in the given report time frame");
    }

    public void helpDate(){
        showNotify("This report is from " + beginDate.toString() + " to " + endDate.toString() + ".");
    }
}

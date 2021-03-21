package socialNetwork.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;

import java.time.LocalDate;

public class FirstPagePDFController extends AbstractController{
    @FXML TextArea reportInfo;
    @FXML ImageView userImage;
    @FXML Label userFullName;
    @FXML Label userEmail;
    @FXML Label reportDate;
    @FXML Label reportType;

    @Override
    protected void init() {
        userImage.setImage(getImage(this.user));
        userFullName.setText(this.user.getLastName() + " " + this.user.getFirstName());
        userEmail.setText(this.user.getEmail());
        reportDate.setText("Created on: " + LocalDate.now().toString());
    }

    public void setReportType(String reportType, LocalDate beginDate, LocalDate end){
        this.reportType.setText(reportType);
        String begin = "This " + reportType + " was made by " + userFullName.getText() + " on " + LocalDate.now().toString() + ".\n";
        String intro = "This report is set between the date " + beginDate.toString() + " and " + end.toString() + ".\n\n";
        reportInfo.setText(begin+intro);
        if(reportType.equals("Activity report"))
            writeAboutActivity();
        else
            writeAboutConv();
    }

    private void writeAboutConv() {
        reportInfo.setText(reportInfo.getText() + "");
    }

    private void writeAboutActivity() {
        String context = "This report is about the new friends added and the new messages received.\n";
        context+="This report is split between 2 parts: Friends and Messages.\n";
        context+="For every month that had at least an event happen, there will be a PieChart showing it.\n";
        context+="At the beginning of the Friends chart and the Messages chart, there will be a summary table.\n";
        reportInfo.setText(reportInfo.getText() + context);
    }
}

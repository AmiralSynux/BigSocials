package socialNetwork.utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import socialNetwork.domain.HasDate;
import socialNetwork.domain.Message;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public class PieChartDrawer {

    /**
     * @param val - the quantity
     * @return 's', if the quantity needs plural. '' otherwise
     */
    private static String getS(double val){
        if(val<2)
            return "";
        else
            return "s";
    }

    /**
     * returns the element of the given list, by date
     * @param pieChartData - list of PieChart.Data with names of the date
     * @param element - same date with the element looking for
     * @return - the first PieChart.Data with the same name as the date of the given element
     *         - null if there are not PieChart.Data with the given name
     */
    private static PieChart.Data existsObject(ObservableList<PieChart.Data> pieChartData, HasDate element){
        for(PieChart.Data data : pieChartData)
            if(data.getName().equals(element.getDate().toLocalDate().toString()))
                return data;
        return null;
    }

    /**
     *
     * @param listView - used for the place where the PieCharts will be drawn
     * @param collection - elements separated by month
     * @param shortN - short form identifier of elements (singular)
     * @param longN - long form identifier of elements (singular)
     * @param <E> - the elements. Needed date for comparison
     */
    public static  <E extends HasDate> void drawPies(ListView<VBox> listView, Collection<List<E>> collection,String shortN,String longN) {
        for(List<E> list : collection)
        {
            PieChart pieChart = new PieChart();
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            for(E element : list){
                PieChart.Data data = existsObject(pieChartData,element);
                if(data!=null)
                    data.setPieValue(data.getPieValue()+1);
                else
                    pieChartData.add(new PieChart.Data(element.getDate().toLocalDate().toString(),1));
            }
            double total=0;
            for(PieChart.Data data : pieChartData){
                total+=data.getPieValue();
                data.setName("Day " + LocalDate.parse(data.getName()).getDayOfMonth() + " -> " + (int)data.getPieValue() + " " + shortN + getS(data.getPieValue()));
            }
            pieChart.setData(pieChartData);
            Label label = new Label(list.get(0).getDate().getMonth().toString() + " - " + (int)total + " new " + longN + getS(total));
            label.setFont(javafx.scene.text.Font.font("Poor Richard",15));
            VBox vBox = new VBox();
            vBox.getChildren().addAll(label,pieChart);
            listView.getItems().add(vBox);
        }
    }
}

package groovyfx;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;

import javax.swing.text.TableView;

public class TableFilter {

    private static String text = "";
    private static String categoryText = "";
    private static boolean isPreinstalled = false;
    private static boolean isPrepared = false;

    public static FilteredList<Ticket> filteredTickets;

    public static SortedList<Ticket> createTableFilter(TextField textSearch, ToggleButton toggleButton, ListView listView){

        if(isPrepared)
            return new SortedList<>(filteredTickets);

        //TextSearch
        textSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue == null)
                newValue = "";
            text = newValue.toLowerCase();
            filterTickets();
        });

        //Button
        toggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            isPreinstalled = newValue;
            filterTickets();
        });

        //Listview
        listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                categoryText = newValue.split(" ")[0];
                filterTickets();
            }
        });

        isPrepared = true;
        return new SortedList<>(filteredTickets);
    }

    private static void filterTickets(){
        filteredTickets.setPredicate(ticket -> false);
        filteredTickets.setPredicate(ticket -> {
            //PREINSTALLED FILTER
            if(isPreinstalled){
                if(ticket.getConsoleID().equals("00000000")){
                    if(ticket.getType().equals("eShopApp")){
                        if(text.length() != 0){
                            String name = ticket.getName();
                            String cid = ticket.getConsoleID();
                            String tid = ticket.getTitleID();
                            String region = ticket.getRegion();
                            String serial = ticket.getSerial();
                            String type = ticket.getType();

                            if(name == null)
                                name = "";
                            if(region == null)
                                region = "";
                            if(serial == null)
                                serial = "";

                            name = name.toLowerCase();
                            region = region.toLowerCase();
                            serial = serial.toLowerCase();
                            type = type.toLowerCase();

                            if(name.contains(text) || cid.contains(text) || tid.contains(text) || region.contains(text) || serial.contains(text) || type.contains(text)){
                                return true;
                            }
                        }else{
                            return true;
                        }
                    }else{
                        return false;
                    }
                }else{
                    return false;
                }
            }

            if(categoryText.equals("All"))
                categoryText = "";
            if(categoryText.equals("DSiSystemApp"))
                categoryText = "DSiSysApp";
            if(categoryText.equals("DSiSystemData"))
                categoryText = "DSiSysDat";

            //TEXTSEARCH FILTER
            if(text.length() != 0){
                String name = ticket.getName();
                String cid = ticket.getConsoleID();
                String tid = ticket.getTitleID();
                String region = ticket.getRegion();
                String serial = ticket.getSerial();
                String type = ticket.getType();

                if(name == null)
                    name = "";
                if(region == null)
                    region = "";
                if(serial == null)
                    serial = "";

                name = name.toLowerCase();
                region = region.toLowerCase();
                serial = serial.toLowerCase();
                type = type.toLowerCase();

                if((name.contains(text) || cid.contains(text) || tid.contains(text) || region.contains(text) || serial.contains(text) || type.contains(text))){
                    if(categoryText.length() > 0){
                        if(ticket.getType().equals(categoryText))
                            return true;
                        else
                            return false;
                    }else
                        return true;
                }
            }else{
                if(categoryText.length() > 0){
                    if(ticket.getType().equals(categoryText))
                        return true;
                    else
                        return false;
                }else
                    return true;
            }
            return false;
        });
    }

}

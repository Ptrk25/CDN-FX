package groovyfx;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;

public class TableFilter {

    private static String text;
    private static String oldText;
    private static String categoryText = "";
    private static String oldCategroyText = "";
    private static boolean isPreinstalled = false;
    private static boolean oldisPreinstalled = false;
    private static boolean isPrepared = false;

    public static FilteredList<Ticket> filteredTickets;

    public static SortedList<Ticket> createTableFilter(TextField textSearch, ToggleButton toggleButton, ListView listView){

        if(isPrepared)
            return new SortedList<>(filteredTickets);

        //TextSearch
        textSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            text = newValue;
            filterTickets();
        });

        //Button
        toggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            isPreinstalled = newValue;


        });

        //Listview
        listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                // Your action here
                categoryText = newValue.split(" ")[0];
                filterTickets();
            }
        });

        isPrepared = true;
        return new SortedList<>(filteredTickets);
    }

    private static void filterTickets(){
        filteredTickets.setPredicate(ticket -> {
            if(ticket.getConsoleID().equals("00000000") && isPreinstalled)
                return true;


            if(!categoryText.equals(oldCategroyText)){

            }

            return true;
        });

        oldCategroyText = categoryText;
    }

}

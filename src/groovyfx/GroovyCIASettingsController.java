package groovyfx;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class GroovyCIASettingsController implements Initializable{

    @FXML
    CheckBox chbxDefaultTicket,
            chbxDefaultOutput,
            chbxAllowSystemTitles,
            chbxAllowNonUniqueConsoleID,
            chbxDisableDBSupport;
    @FXML
    Button btnTicketSelect,
            btnOutputSelect,
            btnResetDatabase,
            btnOK,
            btnCancel;

    public void initialize(URL location, ResourceBundle resources){
        //INIT
    }

    @FXML
    protected void ticketSelected(){

    }

    @FXML
    protected void outputSelected(){

    }

    @FXML
    protected void selectTicket(){

    }

    @FXML
    protected void selectOutput(){

    }

    @FXML
    protected void selectSysTitles(){

    }

    @FXML
    protected void resetDatabase(){

    }

    @FXML
    protected void selectNonUniqueTickets(){

    }

    @FXML
    protected void selectDisableDBSupport(){

    }

    @FXML
    protected void pressOK(){

    }

    @FXML
    protected void pressCancel(){
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

}

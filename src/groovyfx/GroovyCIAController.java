package groovyfx;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class GroovyCIAController implements Initializable {
    //Datafields

    //SplitPanes
    @FXML
    SplitPane splitPaneLeft, splitPaneRight;

    // Menubar
    @FXML
    MenuItem menuOpenTicketDB,
            menuSelectOutputFolder,
            menuClose,
            menuSettings,
            menuThread,
            menuAbout;

    //Labels
    @FXML
    Label lblTicketCount,
            lblUniqueTicketCount,
            lblDuplicatesTicketCount,
            lblSystemTicketCount,
            lblEShopTicketCount,
            lblUniqueEShopTicketCount,
            lblEShopDuplicatesCount,
            lblTicketNotFromYourEShopCount,
            lblTitleCount,
            lblAttemptCount,
            lblFailedCount,
            lblTitleID,
            lblTMD,
            lblFilesCount,
            lblDownloadStats;

    //Tickettable - List
    @FXML
    TableView tableTickets;
    @FXML
    ListView listCategory;

    //Search
    @FXML
    TextField textSearch;

    //Button
    @FXML
    public static Button btnDownload;

    //ToggledButton
    @FXML
    ToggleButton tgbtnShowPreinstalledGame;

    //Checkboxes
    @FXML
    CheckBox chbxBuildCIA,
            chbxPatchDLC,
            chbxPatchDemo,
            chbxPersonal;

    //Progressbar
    @FXML
    ProgressBar progressbarDownload;

    //TableColumns
    @FXML
    TableColumn<Ticket, String> columnName;
    @FXML
    TableColumn<Ticket, String> columnRegion;
    @FXML
    TableColumn<Ticket, String> columnSerial;
    @FXML
    TableColumn<Ticket, String> columnType;
    @FXML
    TableColumn<Ticket, String> columnTitleID;
    @FXML
    TableColumn<Ticket, String> columnConsoleID;
    @FXML
    TableColumn<Ticket, Boolean> columnDL;

    //Tickethandler
    TicketHandler thandler;

    public void initialize(URL location, ResourceBundle resources){

        //Load some stuff
        thandler = new TicketHandler();

        //DISABLE STUFF

    }

    @FXML
    protected void openTicket(){

    }

    @FXML
    protected void selectOutput(){

    }

    @FXML
    protected void close(){
        Platform.exit();
    }

    @FXML
    protected void clickedSettings(){

    }

    @FXML
    protected void clickedThread(){

    }

    @FXML
    protected void clickedAbout(){
        Alert about = new Alert(Alert.AlertType.INFORMATION);
        about.setTitle("About");
        about.setHeaderText("Credits");
        about.setContentText("GroovyCIA by Ptrk25\nFunkyCia2 by cearp\n\nTesters:\n\nduwen\nMadridi\nXenosaiga\nihaveamac\nCha0s Em3rald\n\nStarter XML by\n\nCha0s Em3rald\nMadridi\n\nVersion 1.0");

        about.showAndWait();
    }

    @FXML
    protected void selectedList(){

    }

    @FXML
    protected void selectedBuildCIA(){

    }

    @FXML
    protected void selectedDLC(){
  ;
    }

    @FXML
    protected void selectedDemo(){

    }

    @FXML
    protected void selectedPersonal(){

    }

    @FXML
    protected void toggled(){

    }

    @FXML
    protected void clickedDownload(){

    }

    @FXML
    protected void typed(){

    }

    @FXML
    protected void mouseEnterLeft(){

    }

    @FXML
    protected void mouseEnterRight(){

    }

}


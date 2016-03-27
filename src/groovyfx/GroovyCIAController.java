package groovyfx;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.*;

import java.awt.*;
import java.awt.List;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

public class GroovyCIAController implements Initializable {
    //Datafields

    @FXML
    BorderPane mainPane;

    //SplitPanes
    @FXML
    SplitPane splitPaneLeft, splitPaneRight;

    // Menubar
    @FXML
    MenuItem menuOpenTicketDB,
            menuSelectOutputFolder,
            menuClose,
            menuRebuildRawContent,
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

    String dbpath, outputpath;

    public void initialize(URL location, ResourceBundle resources){
        //DISABLE STUFF
        //INIT PROPERTIES
        try{
            PropertiesHandler.createFile();
            if(PropertiesHandler.getInputPath() != null && PropertiesHandler.getInputPath().length() > 1){
                dbpath = PropertiesHandler.getInputPath();
                openTicketDB();
            }
            if(PropertiesHandler.getOutputPath() != null && PropertiesHandler.getOutputPath().length() > 1){
                outputpath = PropertiesHandler.getOutputPath();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void updateCounters(TicketHandler th){
        int i = 0;
        java.util.List<Integer> tickets_c = th.getTicketCount();

        for(int ticketcount:tickets_c){
            switch(i){
                case 0:
                    lblTicketCount.setText(Integer.toString(ticketcount));
                    break;
                case 1:
                    lblUniqueTicketCount.setText(Integer.toString(ticketcount));
                    break;
                case 2:
                    lblDuplicatesTicketCount.setText(Integer.toString(ticketcount));
                    break;
                case 3:
                    lblSystemTicketCount.setText(Integer.toString(ticketcount));
                    break;
                case 4:
                    lblEShopTicketCount.setText(Integer.toString(ticketcount));
                    break;
                case 5:
                    lblUniqueEShopTicketCount.setText(Integer.toString(ticketcount));
                    break;
                case 6:
                    lblEShopDuplicatesCount.setText(Integer.toString(ticketcount));
                    break;
                case 7:
                    lblTicketNotFromYourEShopCount.setText(Integer.toString(ticketcount));
                    break;
            }
            i++;
        }

        i = 0;

        java.util.List<Integer> apptype = th.getApptypeCount();
        listCategory.getItems().clear();

        listCategory.getItems().add("All (" + th.getTicketCount().get(0) + ")");

        for(int apptypecount:apptype){
            switch(i){
                case 0:
                    listCategory.getItems().add("eShopApp (" + apptypecount + ")");
                    break;
                case 1:
                    listCategory.getItems().add("DownloadPlayChild (" + apptypecount + ")");
                    break;
                case 2:
                    listCategory.getItems().add("Demo (" + apptypecount + ")");
                    break;
                case 3:
                    listCategory.getItems().add("UpdatePatch (" + apptypecount + ")");
                    break;
                case 4:
                    listCategory.getItems().add("DLC (" + apptypecount + ")");
                    break;
                case 5:
                    listCategory.getItems().add("DSiWare (" + apptypecount + ")");
                    break;
                case 6:
                    listCategory.getItems().add("DSiSystemApp (" + apptypecount + ")");
                    break;
                case 7:
                    listCategory.getItems().add("DSiSystemData (" + apptypecount + ")");
                    break;
                case 8:
                    listCategory.getItems().add("System (" + apptypecount + ")");
                    break;
                case 9:
                    listCategory.getItems().add("Mystery (" + apptypecount + ")");
                    break;
            }
            i++;
        }
        listCategory.getSelectionModel().select(0);
    }

    private void openTicketDB() throws Exception{
        TicketHandler thandler = new TicketHandler();
        thandler.openFile(dbpath);
        thandler.addToTicketList();
        thandler.countTickets();
        thandler.sortTickets();

        //UPDATE COUNTERS
        updateCounters(thandler);
    }

    @FXML
    protected void openTicket() throws Exception{
        //FILECHOOSER
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open ticket.db");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ticket.db", "*.db"));
        File selectedFile = fileChooser.showOpenDialog(mainPane.getScene().getWindow());

        if(selectedFile != null){
            dbpath = selectedFile.getPath();
            openTicketDB();
        }
    }

    @FXML
    protected void selectOutput(){
        //DIRECTORYCHOOSER
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(mainPane.getScene().getWindow());
        if(selectedDirectory != null){
            outputpath = selectedDirectory.getPath();
        }
    }

    @FXML
    protected void close(){
        Platform.exit();
    }

    @FXML
    protected void clickedSettings(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../gui/GroovyCIASettings.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setTitle("Settings");
            stage.setScene(new Scene(root1));
            stage.show();
        }catch (Exception e){

        }
    }

    @FXML
    protected void selectRebuildRawContent(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../gui/RebuildCIA.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.DECORATED);
            stage.setTitle("Rebuild raw content");
            stage.setScene(new Scene(root1));
            stage.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    protected void clickedThread(){
        try {
            Desktop.getDesktop().browse(new URI("http://gbatemp.net/threads/wip-groovycia.414004/"));
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        }
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

    }

    @FXML
    protected void selectedDemo(){

    }

    @FXML
    protected void selectedPersonal(){
        Alert warning = new Alert(Alert.AlertType.WARNING);
        warning.setTitle("Warning");
        warning.setHeaderText("Create personal CIAs");
        warning.setContentText("Don't use this if you want to install the CIAs on other systems!\n\nNOTE: Doesn't work with current Firmwares/CFW, CIA won't be installable!");
        if(chbxPersonal.isSelected()){
            warning.showAndWait();
        }
    }

    @FXML
    protected void toggled(){
        Alert warning = new Alert(Alert.AlertType.INFORMATION);
        warning.setTitle("Information");
        warning.setHeaderText("Show legit CIAs");
        warning.setContentText("This will show only eShop titles without ConsoleID\nThat doesn't mean that every entry is a legit CIA!");
        if(tgbtnShowPreinstalledGame.isSelected()){
            warning.showAndWait();
        }
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


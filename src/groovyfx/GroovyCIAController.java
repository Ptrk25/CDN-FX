package groovyfx;


import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import javafx.stage.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;

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
            lblTitleName,
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
    Button btnDownload;

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

    private String dbpath, outputpath;
    private XMLHandler xml_handler = new XMLHandler(null);

    public void initialize(URL location, ResourceBundle resources){
        //DISABLE STUFF
        xml_handler.getXMLFileFromServer();
        initProperties();
        addListener();
    }

    private void initProperties(){
        try{
            PropertiesHandler.createFile();
            if(PropertiesHandler.getInputPath() != null && PropertiesHandler.getInputPath().length() > 1){
                dbpath = PropertiesHandler.getInputPath();
                openTicketDB();
                DebugLogger.log("INIT: Ticket.db opened", Level.INFO);
            }
            if(PropertiesHandler.getOutputPath() != null && PropertiesHandler.getOutputPath().length() > 1){
                outputpath = PropertiesHandler.getOutputPath();
                menuRebuildRawContent.setDisable(false);
                DebugLogger.log("INIT: Outputfolder setted", Level.INFO);
            }
        }catch (Exception e){
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            DebugLogger.log(errors.toString(), Level.SEVERE);
        }
    }

    private void addListener(){
        //Listview
        listCategory.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                listCategory.getSelectionModel().getSelectedIndex(); //DO SOMETHING
            }
        });
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
        ObservableList<Ticket> ticketlist;

        thandler.openFile(dbpath);
        thandler.addToTicketList();
        thandler.countTickets();
        thandler.sortTickets();

        //DATABASE
        xml_handler.setTicketList(thandler.getTicketList());
        ticketlist = xml_handler.readXMLFile();

        updateCounters(thandler);

        tableTickets.setEditable(true);
        tableTickets.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        //REMOVE THIS
        tableTickets.getColumns().addListener(new ListChangeListener() {
            @Override
            public void onChanged(Change change) {
                change.next();
                if(change.wasReplaced()) {
                    tableTickets.getColumns().clear();
                    tableTickets.getColumns().addAll(columnName, columnRegion, columnSerial, columnType, columnTitleID, columnConsoleID, columnDL);
                }
            }
        });

        //TABLEFILTER
        TableFilter.filteredTickets = new FilteredList<>(ticketlist, t -> true);
        SortedList<Ticket> sortedTicket = new SortedList<>(TableFilter.createTableFilter(textSearch, tgbtnShowPreinstalledGame, listCategory));
        sortedTicket.comparatorProperty().bind(tableTickets.comparatorProperty());
        tableTickets.setItems(sortedTicket);

        //TABLECOLUMNS
        columnDL.setMinWidth(35.0);
        columnDL.setMaxWidth(35.0);
        columnConsoleID.setMinWidth(80.0);
        columnConsoleID.setMaxWidth(80.0);
        columnTitleID.setMinWidth(120.0);
        columnTitleID.setMaxWidth(120.0);
        columnType.setMinWidth(80.0);
        columnType.setMaxWidth(80.0);
        columnSerial.setMinWidth(80);
        columnSerial.setMaxWidth(80);
        columnRegion.setMinWidth(50);
        columnRegion.setMaxWidth(50);

        columnName.setCellValueFactory(
                new PropertyValueFactory<Ticket, String>("Name")
        );
        columnRegion.setCellValueFactory(
                new PropertyValueFactory<Ticket, String>("Region")
        );
        columnSerial.setCellValueFactory(
                new PropertyValueFactory<Ticket, String>("Serial")
        );
        columnType.setCellValueFactory(
                new PropertyValueFactory<Ticket, String>("Type")
        );
        columnTitleID.setCellValueFactory(
                new PropertyValueFactory<Ticket, String>("TitleID")
        );
        columnConsoleID.setCellValueFactory(
                new PropertyValueFactory<Ticket, String>("ConsoleID")
        );
        columnDL.setCellValueFactory(
                new PropertyValueFactory<Ticket, Boolean>("DL")
        );

        columnName.setEditable(true);

        textSearch.setDisable(false);
        btnDownload.setDisable(false);
        tgbtnShowPreinstalledGame.setDisable(false);
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
            DebugLogger.log("Ticket.db opened", Level.INFO);
        }
    }

    @FXML
    protected void selectOutput(){
        //DIRECTORYCHOOSER
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(mainPane.getScene().getWindow());
        if(selectedDirectory != null){
            outputpath = selectedDirectory.getPath();
            menuRebuildRawContent.setDisable(false);
            DebugLogger.log("Outputfolder setted", Level.INFO);
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
            stage.setResizable(false);
            stage.show();
        }catch (Exception e){
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            DebugLogger.log(errors.toString(), Level.SEVERE);
        }
    }

    @FXML
    protected void selectRebuildRawContent(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../gui/RebuildCIA.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.getIcons().add(new Image("/resources/gciaicon.png"));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.DECORATED);
            stage.setTitle("Rebuild raw content");
            stage.setScene(new Scene(root1));
            stage.setResizable(false);
            stage.show();
        }catch (Exception e){
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            DebugLogger.log(errors.toString(), Level.SEVERE);
        }
    }

    @FXML
    protected void clickedThread(){
        try {
            Desktop.getDesktop().browse(new URI("http://gbatemp.net/threads/wip-groovycia.414004/"));
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (URISyntaxException e1) {
            StringWriter errors = new StringWriter();
            e1.printStackTrace(new PrintWriter(errors));
            DebugLogger.log(errors.toString(), Level.SEVERE);
        }
    }

    @FXML
    protected void clickedAbout(){
        Alert about = new Alert(Alert.AlertType.INFORMATION);
        Stage stage = (Stage)about.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("/resources/gciaicon.png"));
        about.setTitle("About");
        about.setHeaderText("Credits");
        WebView webView = new WebView();
        webView.getEngine().loadContent("<html>\n" +
                "\t<body bgcolor=\"#F4F4F4\">\n" +
                "\t\t<center>\n" +
                "\t\t\t<font face=\"System\">\n" +
                "\t\t\t\t<b>GroovyCIA</b> by Ptrk25<br>\n" +
                "\t\t\t\t<b>FunkyCia2</b> by cearp<br><br>\n" +
                "\t\t\t\t<b>Testers</b><br>\n" +
                "\t\t\t\tduwen<br>\n" +
                "\t\t\t\tMadridi<br>\n" +
                "\t\t\t\tXenosaiga<br>\n" +
                "\t\t\t\tihaveamac<br>\n" +
                "\t\t\t\tCha0s Em3rald<br><br>\n" +
                "\t\t\t\t<b>Community XML</b><br>\n" +
                "\t\t\t\tCha0s Em3rald<br>\n" +
                "\t\t\t\tMadridi<br>\n" +
                "\t\t\t\tJimmsu<br><br>\n" +
                "\t\t\t\t<b>Icon</b><br>\n" +
                "\t\t\t\talirezay<br><br>\n" +
                "\t\t\t\tVersion 0.91 Beta\n" +
                "\t\t\t</font>\n" +
                "\t\t</center>\n" +
                "\t</body>\n" +
                "</html>");
        webView.setPrefSize(300,420);
        about.getDialogPane().setContent(webView);

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
        Stage stage = (Stage)warning.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("/resources/gciaicon.png"));
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


package groovyfx;


import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
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

    private String dbpath = "", outputpath = "";
    private ObservableList<Ticket> downloadList = FXCollections.observableArrayList();
    public ObservableList<Ticket> editedEntries = FXCollections.observableArrayList();
    SortedList<Ticket> sortedTicket;
    private XMLHandler xml_handler = new XMLHandler(null);
    private Downloader dl;

    private MenuItem mnuAddToDownloadList;
    private MenuItem mnuRemoveFromDownloadList;
    private MenuItem mnuAddAllToDownloadList;
    private MenuItem mnuRemoveAllFromDownloadList;

    public void initialize(URL location, ResourceBundle resources){
        XMLUpdater xmlu = new XMLUpdater();
        boolean un = xmlu.checkForUpdates();
        if(un){
            xmlu.update();
        }
        xml_handler.getXMLFileFromServer();
        CustomXMLHandler.createCustomDatabase();
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
        int final_count = 0;
        java.util.List<Integer> tickets_c = th.getTicketCount();

        i = 0;

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

        for(int ticketcount:apptype){
            final_count += ticketcount;
        }

        listCategory.getItems().clear();

        mnuAddAllToDownloadList = new MenuItem("Add all Titles of this category to downloadlist");
        mnuRemoveAllFromDownloadList = new MenuItem("Remove all Titles of this category from downloadlist");

        mnuAddAllToDownloadList.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        String type = "";

                        boolean sys, nonunique;
                        sys = false;
                        nonunique = false;

                        downloadList.clear();

                        if(PropertiesHandler.getProperties("downloadsystemtitles") != null){
                            if(PropertiesHandler.getProperties("downloadsystemtitles").equals("yes")){
                                sys = true;
                            }else{
                                sys = false;
                            }
                        }
                        if(PropertiesHandler.getProperties("downloadnonuniquetitles") != null){
                            if(PropertiesHandler.getProperties("downloadnonuniquetitles").equals("yes")){
                                nonunique = true;
                            }else{
                                nonunique = false;
                            }
                        }

                        switch (listCategory.getSelectionModel().getSelectedIndex()){
                            case 0:
                                type = "All";
                                break;
                            case 1:
                                type = "eShopApp";
                                break;
                            case 2:
                                type = "DownloadPlayChild";
                                break;
                            case 3:
                                type = "Demo";
                                break;
                            case 4:
                                type = "UpdatePatch";
                                break;
                            case 5:
                                type = "DLC";
                                break;
                            case 6:
                                type = "DSiWare";
                                break;
                            case 7:
                                type = "DSiSystemApp";
                                break;
                            case 8:
                                type = "DSiSystemData";
                                break;
                            case 9:
                                type = "System";
                                break;
                            case 10:
                                type = "Mystery";
                                break;
                        }

                        for(Ticket ticket:sortedTicket){
                            if(type.equals("All")){
                                if(ticket.getType().equals("System")){
                                    if(sys){
                                        ticket.setDownload(true);
                                        downloadList.add(ticket);
                                    }
                                }else{
                                    if(ticket.getConsoleID().equals("00000000")){
                                        if(nonunique){
                                            ticket.setDownload(true);
                                            downloadList.add(ticket);
                                        }
                                    }else{
                                        ticket.setDownload(true);
                                        downloadList.add(ticket);
                                    }
                                }
                            }else if(type.equals("System")){
                                if(sys){
                                    ticket.setDownload(true);
                                    downloadList.add(ticket);
                                }
                            }else{
                                if(type.equals(ticket.getType())){
                                    if(nonunique){
                                        ticket.setDownload(true);
                                        downloadList.add(ticket);
                                    }else{
                                        if(!ticket.getConsoleID().equals("00000000")){
                                            ticket.setDownload(true);
                                            downloadList.add(ticket);
                                        }
                                    }
                                }
                            }
                        }

                    }
                });

        mnuRemoveAllFromDownloadList.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        String type = "";

                        switch (listCategory.getSelectionModel().getSelectedIndex()){
                            case 0:
                                type = "All";
                                break;
                            case 1:
                                type = "eShopApp";
                                break;
                            case 2:
                                type = "DownloadPlayChild";
                                break;
                            case 3:
                                type = "Demo";
                                break;
                            case 4:
                                type = "UpdatePatch";
                                break;
                            case 5:
                                type = "DLC";
                                break;
                            case 6:
                                type = "DSiWare";
                                break;
                            case 7:
                                type = "DSiSystemApp";
                                break;
                            case 8:
                                type = "DSiSystemData";
                                break;
                            case 9:
                                type = "System";
                                break;
                            case 10:
                                type = "Mystery";
                                break;
                        }

                        for(Ticket ticket:sortedTicket){
                            if(type.equals(ticket.getType())){
                                downloadList.remove(ticket);
                                ticket.setDownload(false);
                            }else if(type.equals("All")){
                                downloadList.remove(ticket);
                                ticket.setDownload(false);
                            }
                        }

                    }
                });

        listCategory.setContextMenu(new ContextMenu(mnuAddAllToDownloadList, mnuRemoveAllFromDownloadList));
        listCategory.getItems().add("All (" + final_count + ")");

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
        xml_handler.readXMLFile();
        xml_handler.readCommunityXMLFile();
        ticketlist = xml_handler.readCustomXMLFile();

        if(ticketlist == null){
            ticketlist = thandler.getTicketList();
        }

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
                    tableTickets.getColumns().addAll(columnName, columnRegion, columnSerial, columnType, columnTitleID, columnConsoleID);
                }
            }
        });

        //TABLEFILTER
        TableFilter.filteredTickets = new FilteredList<>(ticketlist, t -> true);
        sortedTicket = new SortedList<>(TableFilter.createTableFilter(textSearch, tgbtnShowPreinstalledGame, listCategory));
        sortedTicket.comparatorProperty().bind(tableTickets.comparatorProperty());
        tableTickets.setItems(sortedTicket);

        //CONTEXTMENU
        mnuAddToDownloadList = new MenuItem("Add to downloadlist");
        mnuAddToDownloadList.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                Ticket ticket = sortedTicket.get(tableTickets.getSelectionModel().getSelectedIndex());
                if(ticket != null){
                    boolean sys, nonunique;
                    sys = false;
                    nonunique = false;

                    if(PropertiesHandler.getProperties("downloadsystemtitles") != null){
                        if(PropertiesHandler.getProperties("downloadsystemtitles").equals("yes")){
                            sys = true;
                        }else{
                            sys = false;
                        }
                    }
                    if(PropertiesHandler.getProperties("downloadnonuniquetitles") != null){
                        if(PropertiesHandler.getProperties("downloadnonuniquetitles").equals("yes")){
                            nonunique = true;
                        }else{
                            nonunique = false;
                        }
                    }

                    if(ticket.getType().equals("System")){
                        if(sys){
                            ticket.setDownload(true);
                            downloadList.add(ticket);
                        }
                    }else{
                        if(ticket.getConsoleID().equals("00000000")){
                            if(nonunique){
                                ticket.setDownload(true);
                                downloadList.add(ticket);
                            }
                        }else{
                            ticket.setDownload(true);
                            downloadList.add(ticket);
                        }
                    }

                }
            }
        });

        mnuRemoveFromDownloadList = new MenuItem("Remove from downloadlist");
        mnuRemoveFromDownloadList.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Ticket ticket = sortedTicket.get(tableTickets.getSelectionModel().getSelectedIndex());
                if(ticket != null){
                    if(downloadList.contains(ticket)) {
                        downloadList.remove(ticket);
                        ticket.setDownload(false);
                    }
                }
            }
        });

        tableTickets.setContextMenu(new ContextMenu(mnuAddToDownloadList, mnuRemoveFromDownloadList));

        //TABLECOLUMNS

        if(DetectOS.isMac()){
            columnConsoleID.setMinWidth(81.57);
            columnConsoleID.setMaxWidth(81.57);
            columnTitleID.setMinWidth(144.57);
            columnTitleID.setMaxWidth(144.57);
            columnType.setMinWidth(82.57);
            columnType.setMaxWidth(82.57);
            columnSerial.setMinWidth(89.57);
            columnSerial.setMaxWidth(89.57);
            columnRegion.setMinWidth(44.57);
            columnRegion.setMaxWidth(44.57);
            columnDL.setMaxWidth(25.57);
            columnDL.setMinWidth(25.57);
            columnName.setPrefWidth(149.57);
            columnName.setMinWidth(149.57);
        }else{
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
            columnDL.setMaxWidth(20);
            columnDL.setMinWidth(20);
        }


        columnName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        columnRegion.setCellValueFactory(cellData -> cellData.getValue().regionProperty());
        columnSerial.setCellValueFactory(cellData -> cellData.getValue().serialProperty());
        columnType.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
        columnTitleID.setCellValueFactory(cellData -> cellData.getValue().titleidProperty());
        columnConsoleID.setCellValueFactory(cellData -> cellData.getValue().consoleidProperty());
        columnDL.setCellValueFactory(cellData -> cellData.getValue().downloadProperty().asObject());


        columnName.setCellFactory(TextFieldTableCell.forTableColumn());
        columnRegion.setCellFactory(TextFieldTableCell.forTableColumn());
        columnSerial.setCellFactory(TextFieldTableCell.forTableColumn());
        columnTitleID.setCellFactory(TextFieldTableCell.forTableColumn());
        columnDL.setCellFactory(column -> {
            return new TableCell<Ticket, Boolean>() {
                @Override
                protected void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);

                    setText(null);

                    TableRow<Ticket> currentRow = getTableRow();

                    HBox box= new HBox();
                    box.setSpacing(0);
                    ImageView imageview = new ImageView();
                    imageview.setFitHeight(14);
                    imageview.setFitWidth(14);
                    imageview.setImage(new Image(Main.class.getResource("/resources/success-icon.png").toString()));
                    box.getChildren().add(imageview);
                    setGraphic(null);

                    if (!isEmpty()) {
                        if(getItem().toString() == "true"){
                            setGraphic(box);
                        }
                    }
                }
            };
        });

        columnName.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Ticket, String>>() {
                    public void handle(TableColumn.CellEditEvent<Ticket, String> t) {
                        Ticket ticket = ((Ticket) t.getTableView().getItems().get(t.getTablePosition().getRow()));
                        ((Ticket) t.getTableView().getItems().get(t.getTablePosition().getRow())
                        ).setName(t.getNewValue());

                        editedEntries.add(new Ticket(t.getNewValue(), ticket.getRegion(), ticket.getSerial(), ticket.getTitleID()));
                    }
                }
        );

        columnRegion.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Ticket, String>>() {
                    public void handle(TableColumn.CellEditEvent<Ticket, String> t) {
                        Ticket ticket = ((Ticket) t.getTableView().getItems().get(t.getTablePosition().getRow()));
                        ((Ticket) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setRegion(t.getNewValue());

                        editedEntries.add(new Ticket(ticket.getName(), t.getNewValue(), ticket.getSerial(), ticket.getTitleID()));
                    }
                }
        );

        columnSerial.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Ticket, String>>() {
                    public void handle(TableColumn.CellEditEvent<Ticket, String> t) {
                        Ticket ticket = ((Ticket) t.getTableView().getItems().get(t.getTablePosition().getRow()));
                        ((Ticket) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setSerial(t.getNewValue());

                        editedEntries.add(new Ticket(ticket.getName(), ticket.getRegion(), t.getNewValue(), ticket.getTitleID()));
                    }
                }
        );

        columnTitleID.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Ticket, String>>() {
                    public void handle(TableColumn.CellEditEvent<Ticket, String> t) {
                        Ticket ticket = ((Ticket) t.getTableView().getItems().get(t.getTablePosition().getRow()));
                        ((Ticket) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setTitleID(t.getOldValue());
                    }
                }
        );

        textSearch.setDisable(false);
        btnDownload.setDisable(false);
        tgbtnShowPreinstalledGame.setDisable(false);
    }

    @FXML
    protected void openTicket() throws Exception{
        String path = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        path = path.substring(1, path.lastIndexOf("/")) + "/";
        //FILECHOOSER
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open ticket.db");
        fileChooser.setInitialDirectory(new File(path));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ticket.db", "*.db"));
        File selectedFile = fileChooser.showOpenDialog(mainPane.getScene().getWindow());

        if(selectedFile != null){
            dbpath = selectedFile.getPath();
            openTicketDB();
            DebugLogger.log("Ticket.db opened", Level.INFO);
        }
    }

    @FXML
    protected void selectOutput() throws Exception{
        String path = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        path = path.substring(1, path.lastIndexOf("/")) + "/";
        //DIRECTORYCHOOSER
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(path));
        File selectedDirectory = directoryChooser.showDialog(mainPane.getScene().getWindow());
        if(selectedDirectory != null){
            outputpath = selectedDirectory.getPath();
            menuRebuildRawContent.setDisable(false);
            DebugLogger.log("Outputfolder setted", Level.INFO);
        }
    }

    @FXML
    protected void close(){
        if(editedEntries.size() > 0){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Close Confirmation");
            alert.setHeaderText("Do you want to save your edited entries?");
            alert.initModality(Modality.APPLICATION_MODAL);

            ButtonType Yes = new ButtonType("Yes");
            ButtonType No = new ButtonType("No");
            alert.getButtonTypes().setAll(Yes, No);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == Yes){
                try{
                    CustomXMLHandler.writeIntoXML(editedEntries);
                }catch (Exception e){
                    StringWriter errors = new StringWriter();
                    e.printStackTrace(new PrintWriter(errors));
                    DebugLogger.log(errors.toString(), Level.SEVERE);
                }
                Platform.exit();
                System.exit(0);
            }else{
                Platform.exit();
                System.exit(0);
            }
        }else{
            Platform.exit();
            System.exit(0);
        }
    }

    @FXML
    protected void clickedSettings(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/GroovyCIASettings.fxml"));
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
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/RebuildCIA.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.getIcons().add(new Image("/resources/gciaicon.png"));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.DECORATED);
            stage.setTitle("Rebuild raw content");
            stage.setScene(new Scene(root1));
            stage.setResizable(false);
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    // consume event
                    event.consume();
                }
            });
            RebuildCIAController rb = fxmlLoader.getController();
            rb.setInput(outputpath);
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
                "\t\t\t\tMadridi<br>\n" +
                "\t\t\t\tihaveamac<br>\n" +
                "\t\t\t\tCha0s Em3rald<br><br>\n" +
                "\t\t\t\t<b>Community XML</b><br>\n" +
                "\t\t\t\tCha0s Em3rald<br>\n" +
                "\t\t\t\tMadridi<br>\n" +
                "\t\t\t\tJimmsu<br><br>\n" +
                "\t\t\t\t<b>Icon</b><br>\n" +
                "\t\t\t\talirezay<br><br>\n" +
                "\t\t\t\tVersion 1.04\n" +
                "\t\t\t</font>\n" +
                "\t\t</center>\n" +
                "\t</body>\n" +
                "</html>");
        webView.setPrefSize(300,400);
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
        Alert warning = new Alert(Alert.AlertType.INFORMATION);
        Stage stage = (Stage)warning.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("/resources/gciaicon.png"));
        warning.setTitle("Information");
        warning.setHeaderText("Patch DLC");
        warning.setContentText("By selecting this option, all DLC content on CDN will be unlocked, regardless of whether it was bought on eShop or not.\n\nDeselecting this option will only download your legit content.");
        warning.showAndWait();
    }

    @FXML
    protected void selectedDemo(){
        Alert warning = new Alert(Alert.AlertType.INFORMATION);
        Stage stage = (Stage)warning.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("/resources/gciaicon.png"));
        warning.setTitle("Information");
        warning.setHeaderText("Patch Demo");
        warning.setContentText("By selecting this option, the demo play count limit will be removed.\n\nDeselecting this option will download this demo without patching anything.");
        warning.showAndWait();
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
        Stage stage = (Stage)warning.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("/resources/gciaicon.png"));
        warning.setTitle("Information");
        warning.setHeaderText("Show legit CIAs");
        warning.setContentText("This will show only eShop titles without ConsoleID\nThat doesn't mean that every entry is a legit CIA!");
        if(tgbtnShowPreinstalledGame.isSelected()){
            warning.showAndWait();
        }
    }

    @FXML
    protected void clickedDownload(){
       if(btnDownload.getText().equals("Download")){
            if(downloadList.size() > 0){
                if(outputpath.length() > 0){
                    btnDownload.setText("Cancel");
                    menuRebuildRawContent.setDisable(true);
                    tableTickets.setEditable(false);
                    mnuAddToDownloadList.setDisable(true);
                    mnuRemoveFromDownloadList.setDisable(true);
                    mnuAddAllToDownloadList.setDisable(true);
                    mnuRemoveAllFromDownloadList.setDisable(true);
                    textSearch.setDisable(true);

                    dl = new Downloader(downloadList, outputpath);
                    dl.setBuildCIA(chbxBuildCIA.isSelected());
                    dl.setPatchDemo(chbxPatchDemo.isSelected());
                    dl.setPatchDLC(chbxPatchDLC.isSelected());
                    dl.setBlankID(!chbxPersonal.isSelected());
                    dl.setComponents(lblTitleCount, lblAttemptCount, lblFailedCount, lblTitleName, lblTitleID, lblTMD, lblFilesCount, lblDownloadStats, progressbarDownload, btnDownload);
                    dl.setXtraComponents(mnuAddToDownloadList, mnuRemoveFromDownloadList, menuRebuildRawContent, mnuAddAllToDownloadList, mnuRemoveAllFromDownloadList, textSearch, tableTickets);
                    dl.start();

                }else{
                    Alert warning = new Alert(Alert.AlertType.ERROR);
                    Stage stage = (Stage)warning.getDialogPane().getScene().getWindow();
                    stage.getIcons().add(new Image("/resources/gciaicon.png"));
                    warning.setTitle("Error");
                    warning.setHeaderText("Outputfolder!");
                    warning.setContentText("Please choose an outputfolder first!");
                    warning.showAndWait();
                }
            }else{
                Alert warning = new Alert(Alert.AlertType.ERROR);
                Stage stage = (Stage)warning.getDialogPane().getScene().getWindow();
                stage.getIcons().add(new Image("/resources/gciaicon.png"));
                warning.setTitle("Error");
                warning.setHeaderText("No Titles selected!");
                warning.setContentText("Please select at least one title!");
                warning.showAndWait();
            }
       }else{
           dl.setInterrupted(true);
           btnDownload.setText("Download");
           menuRebuildRawContent.setDisable(false);
           tableTickets.setEditable(true);
           mnuAddToDownloadList.setDisable(false);
           mnuRemoveFromDownloadList.setDisable(false);
           mnuAddAllToDownloadList.setDisable(false);
           mnuRemoveAllFromDownloadList.setDisable(false);
           textSearch.setDisable(false);
           for(int i = 0; i < TableFilter.filteredTickets.size(); i++){
               Ticket tiktik = TableFilter.filteredTickets.get(i);
               if(tiktik.getDownload()){
                   tiktik.setDownload(false);
               }
           }
           DebugLogger.log("Download cancled!", Level.INFO);
       }
    }

    @FXML
    protected void clickedGitHub(){
        try {
            Desktop.getDesktop().browse(new URI("http://ptrk25.github.io/GroovyFX/"));
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (URISyntaxException e1) {
            StringWriter errors = new StringWriter();
            e1.printStackTrace(new PrintWriter(errors));
            DebugLogger.log(errors.toString(), Level.SEVERE);
        }
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


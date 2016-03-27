package groovyfx;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class GroovyCIASettingsController implements Initializable{

    @FXML
    CheckBox chbxDefaultTicket,
            chbxDefaultOutput,
            chbxAllowSystemTitles,
            chbxAllowNonUniqueConsoleID,
            chbxDisableDBSupport,
            chbxEnableDebugmode;
    @FXML
    Button btnTicketSelect,
            btnOutputSelect,
            btnResetDatabase,
            btnOK,
            btnCancel;

    @FXML
    TextField textTicket, textOutput;

    public void initialize(URL location, ResourceBundle resources){
        //INIT
        initProperties();
    }

    private void initProperties(){
        if(PropertiesHandler.getInputPath() != null && PropertiesHandler.getInputPath().length() > 1){
            textTicket.setText(PropertiesHandler.getInputPath());
            chbxDefaultTicket.setSelected(true);
            btnTicketSelect.setDisable(false);
            textTicket.setDisable(false);
        }else{
            chbxDefaultTicket.setSelected(false);
            btnTicketSelect.setDisable(true);
            textTicket.setDisable(true);
        }

        if(PropertiesHandler.getOutputPath() != null && PropertiesHandler.getOutputPath().length() > 1){
            textOutput.setText(PropertiesHandler.getOutputPath());
            chbxDefaultOutput.setSelected(true);
            btnOutputSelect.setDisable(false);
            textOutput.setDisable(false);
        }else{
            chbxDefaultOutput.setSelected(false);
            btnOutputSelect.setDisable(true);
            textOutput.setDisable(true);
        }

        if(PropertiesHandler.getProperties("downloadsystemtitles") != null){
            if(PropertiesHandler.getProperties("downloadsystemtitles").equals("yes")){
                chbxAllowSystemTitles.setSelected(true);
            }else{
                chbxAllowSystemTitles.setSelected(false);
            }
        }

        if(PropertiesHandler.getProperties("downloadnonuniquetitles") != null){
            if(PropertiesHandler.getProperties("downloadnonuniquetitles").equals("yes")){
                chbxAllowNonUniqueConsoleID.setSelected(true);
            }else{
                chbxAllowNonUniqueConsoleID.setSelected(false);
            }
        }

        if(PropertiesHandler.getProperties("disable3dsdbsupport") != null){
            if(PropertiesHandler.getProperties("disable3dsdbsupport").equals("yes")){
                chbxDisableDBSupport.setSelected(true);
            }else{
                chbxDisableDBSupport.setSelected(false);
            }
        }

        if(PropertiesHandler.getProperties("debugmode") != null){
            if(PropertiesHandler.getProperties("debugmode").equals("yes")){
                chbxEnableDebugmode.setSelected(true);
            }else{
                chbxEnableDebugmode.setSelected(false);
            }
        }
    }

    @FXML
    protected void ticketSelected(){
        if(chbxDefaultTicket.isSelected()){
            textTicket.setDisable(false);
            btnTicketSelect.setDisable(false);
        }else{
            textTicket.setDisable(true);
            btnTicketSelect.setDisable(true);
            PropertiesHandler.setProperties("", "ticketdb");
        }
    }

    @FXML
    protected void outputSelected(){
        if(chbxDefaultOutput.isSelected()){
            textOutput.setDisable(false);
            btnOutputSelect.setDisable(false);
        }else{
            textOutput.setDisable(true);
            btnOutputSelect.setDisable(true);
            PropertiesHandler.setProperties("", "outputfolder");
        }
    }

    @FXML
    protected void selectTicket(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open ticket.db");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ticket.db", "*.db"));
        File selectedFile = fileChooser.showOpenDialog(btnCancel.getScene().getWindow());

        if(selectedFile != null){
            String path = selectedFile.getPath();
            textTicket.setText(path);
            path = path.replaceAll(":", "!");
            PropertiesHandler.setProperties(path, "ticketdb");
        }
    }

    @FXML
    protected void selectOutput(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(btnCancel.getScene().getWindow());
        if(selectedDirectory != null){
            String path = selectedDirectory.getPath();
            textOutput.setText(path);
            path = path.replaceAll(":", "!");
            PropertiesHandler.setProperties(path, "outputfolder");
        }
    }

    @FXML
    protected void selectSysTitles(){
        if(chbxAllowSystemTitles.isSelected()){
            Alert warning = new Alert(Alert.AlertType.WARNING);
            warning.setTitle("Warning");
            warning.setHeaderText("Download Systemtitles");
            warning.setContentText("This feature is experimental and should only be used for debugging purposes.\n\n" +
                                    "Do not use this option as a NUS replacement.\n" +
                                    "USE IT AT YOUR OWN RISK!!");
            warning.showAndWait();
            PropertiesHandler.setProperties("yes", "downloadsystemtitles");
        }else{
            PropertiesHandler.setProperties("no", "downloadsystemtitles");
        }
    }

    @FXML
    protected void resetDatabase(){
        //TODO: Reset custom Database
    }

    @FXML
    protected void selectNonUniqueTickets(){
        if(chbxAllowNonUniqueConsoleID.isSelected()){
            Alert warning = new Alert(Alert.AlertType.WARNING);
            warning.setTitle("Warning");
            warning.setHeaderText("Download Non-Unique Titles");
            warning.setContentText("This will allow you to download content from your Ticket.db that is not attached to your eShop.\n\n" +
                                    "Only select this option if you have a PREINSTALLED/LEGIT CIA in your Ticket.db.\n\n" +
                                    "Otherwise, content may not install or work properly as intended.\n\n" +
                                    "USE IT AT YOUR RISK!!");
            warning.showAndWait();
            PropertiesHandler.setProperties("yes", "downloadnonuniquetitles");
        }else{
            PropertiesHandler.setProperties("no", "downloadnonuniquetitles");
        }
    }

    @FXML
    protected void selectDisableDBSupport(){
        if(chbxDisableDBSupport.isSelected()){
            Alert warning = new Alert(Alert.AlertType.INFORMATION);
            warning.setTitle("Information");
            warning.setHeaderText("Disable 3dsdb support");
            warning.setContentText("By disabling this option:\n - The program will boot a lot faster.\n\n- The program will get all title information from the community.xml and database.xml");
            warning.showAndWait();
            PropertiesHandler.setProperties("yes", "disable3dsdbsupport");
        }else{
            PropertiesHandler.setProperties("no", "disable3dsdbsupport");
        }
    }

    @FXML
    protected void selectEnableDebugmode(){
        if(chbxEnableDebugmode.isSelected()){
            Alert warning = new Alert(Alert.AlertType.INFORMATION);
            warning.setTitle("Information");
            warning.setHeaderText("Enable Debugmode");
            warning.setContentText("If you are having a problem with this program, enable this option then replicate the issue. A log file will be produced by the program. Report the issue by posting the log file in the GroovyCIA thread");
            warning.showAndWait();
            PropertiesHandler.setProperties("yes", "debugmode");
        }else{
            PropertiesHandler.setProperties("no", "debugmode");
        }
    }

    @FXML
    protected void pressOK(){
        PropertiesHandler.saveProperties();
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    @FXML
    protected void pressCancel(){
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

}

package groovyfx;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RebuildCIAController implements Initializable{

    @FXML
    public Label labelStatus;

    @FXML
    private ProgressBar progressBar1;

    String path;

    public void initialize(URL location, ResourceBundle resources){

    }
    public void setInput(String path){
        this.path = path;
        Downloader dl = new Downloader(null, path);
        dl.setComponents(labelStatus, null, null, null, null, null, null, null, progressBar1, null);
        dl.setDownload(false);
        dl.start();
    }

}

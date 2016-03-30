package groovyfx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;
import java.util.logging.Level;

public class Main extends Application {

    /**
     * Loads GroovyCIA Mainwindow
     *
     * @param primaryStage      First Stage
     * @throws                  Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        DebugLogger.init();

        DebugLogger.log("INIT: Unpack make_cdn_cia...", Level.INFO);
        MakeCDN.unpackFile();
        DebugLogger.log("INIT: make_cdn_cia unpacked!", Level.INFO);

        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/gui/GroovyCIA.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            scene.getStylesheets().add("gui/StyleSheet.css");
            primaryStage.setTitle("GroovyCIA (Testers FX Edition)");
            primaryStage.getIcons().add(new Image("/resources/gciaicon.png"));
            primaryStage.setScene(scene);
            primaryStage.setMinHeight(609);
            primaryStage.setMinWidth(1091);
            primaryStage.show();

            GroovyCIAController gciac = loader.getController();
            primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {

                    // consume event
                    event.consume();

                    // show close dialog

                    if(gciac.editedEntries.size() > 0){
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Close Confirmation");
                        alert.setHeaderText("Do you want to save your edited entries?");
                        alert.initOwner(primaryStage);

                        ButtonType Yes = new ButtonType("Yes");
                        ButtonType No = new ButtonType("No");
                        alert.getButtonTypes().setAll(Yes, No);

                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.get() == Yes){
                            //SAVE
                            Platform.exit();
                        }
                    }else{
                        Platform.exit();
                    }
                }
            });


            DebugLogger.log("Initialization complete!", Level.INFO);
        }catch(Exception e){
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            DebugLogger.log(errors.toString(), Level.SEVERE);
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}

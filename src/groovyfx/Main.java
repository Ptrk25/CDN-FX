package groovyfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.io.StringWriter;
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
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../gui/GroovyCIA.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            scene.getStylesheets().add("gui/StyleSheet.css");
            primaryStage.setTitle("GroovyCIA (Testers FX Edition)");
            primaryStage.setScene(scene);
            primaryStage.show();

            DebugLogger.log("Initialization successful!", Level.INFO);
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

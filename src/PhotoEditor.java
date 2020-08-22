// Main application class that loads and displays the Tip Calculator's GUI.
import javafx.application.Application;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;

import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.BorderPane;

public class PhotoEditor extends Application {

    @Override
    public void start(final Stage stage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("resources/PhotoEditor.fxml"));
        Scene scene = new Scene(root); // attach scene graph to scene
        stage.setTitle("Photo Editor"); // displayed in window's title bar
        stage.setScene(scene); // attach scene to stage
        stage.show(); // display the stage

    }

    public static void main(final String[] args) {
        // create a PhotoEditor object and call its start method
        launch(args);
    }
}
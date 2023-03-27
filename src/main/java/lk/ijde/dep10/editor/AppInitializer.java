package lk.ijde.dep10.editor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lk.ijde.dep10.editor.controller.EditorSceneController;

import java.io.IOException;
import java.net.URL;

public class AppInitializer extends Application {

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws IOException {
        URL fxmlFile = getClass().getResource("/view/EditorScene.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(fxmlFile);
        AnchorPane root = fxmlLoader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);

        primaryStage.setTitle("Untitled file");
        primaryStage.show();
        primaryStage.centerOnScreen();

        EditorSceneController controller = fxmlLoader.getController();
        controller.closeStageFromButton(primaryStage);
    }

}

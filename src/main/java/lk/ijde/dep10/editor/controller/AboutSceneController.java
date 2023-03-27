package lk.ijde.dep10.editor.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class AboutSceneController {

    @FXML
    private Button btnClose;

    @FXML
    void btnCloseOnAction(ActionEvent event) {
        Stage newStage = (Stage) btnClose.getScene().getWindow();
        newStage.close();
    }

}


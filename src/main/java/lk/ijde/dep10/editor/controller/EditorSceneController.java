package lk.ijde.dep10.editor.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.print.JobSettings;
import javafx.print.PageLayout;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import lk.ijde.dep10.editor.AppInitializer;

import java.io.*;
import java.net.URL;
import java.util.Optional;

public class EditorSceneController {
    @FXML
    private TextArea txtEditor;
    private File saveFile;

    public void initialize(){
        txtEditor.textProperty().addListener((value,previous,current) -> {
            Stage newStage = (Stage) txtEditor.getScene().getWindow();
            if (previous != current){
                if (newStage.getTitle().charAt(0) != '*'){
                    newStage.setTitle("*" + newStage.getTitle());
                }
            }
        });
    }
    public void closeStageFromButton(Stage stage){
        Stage newStage = (Stage) txtEditor.getScene().getWindow();
        newStage.setOnCloseRequest(event -> {
            String getTitle = newStage.getTitle();
            if (getTitle.charAt(0) == '*'){
                Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION,"Are you sure to close this project before saving...?", ButtonType.YES,ButtonType.NO);
                Optional<ButtonType> button = confirmationAlert.showAndWait();
                if (button.isPresent() && button.get() == ButtonType.YES){
                    newStage.close();
                }
                else {
                    try {
                        mnSaveAsOnAction();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            else {
                newStage.close();
            }
        });
    }
    @FXML
    void mnAboutOnAction(ActionEvent event) throws IOException {
        Stage aboutStage = new Stage();
        aboutStage.setTitle("About Simple Text Editor");

        URL fxmlFile = this.getClass().getResource("/view/AboutScene.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(fxmlFile);
        AnchorPane root = fxmlLoader.load();

        Scene scene = new Scene(root);
        aboutStage.setScene(scene);
        aboutStage.initModality(Modality.WINDOW_MODAL);
        aboutStage.initOwner(txtEditor.getScene().getWindow());
        aboutStage.show();
        aboutStage.centerOnScreen();
    }

    @FXML
    void mnCloseOnAction(ActionEvent event) throws IOException {
        Stage newStage = (Stage) txtEditor.getScene().getWindow();
        String getTitle = newStage.getTitle();
        if (getTitle.charAt(0) == '*'){
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION,"Are you sure to close this file before saving...?", ButtonType.YES,ButtonType.NO);
            Optional<ButtonType> button = confirmationAlert.showAndWait();
            if (button.isPresent() && button.get() == ButtonType.YES){
                newStage.close();
            }
            else {
                mnSaveAsOnAction();
            }
        }
        else {
            newStage.close();
        }
    }

    @FXML
    void mnNewOnAction(ActionEvent event) throws IOException {
        Stage newStage = (Stage) txtEditor.getScene().getWindow();
        if (newStage.getTitle().charAt(0) == '*'){
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION,"Are you sure to take a new file before saving previous file...?", ButtonType.YES,ButtonType.NO);
            Optional<ButtonType> button = confirmationAlert.showAndWait();
            if (button.isPresent() && button.get() == ButtonType.YES){
                txtEditor.clear();
                newStage.setTitle("Untitled file");
            }
            else {
                mnSaveAsOnAction();
                txtEditor.clear();
                newStage.setTitle("Untitled file");
            }
        }
        else {
            txtEditor.clear();
            newStage.setTitle("Untitled file");
        }
    }

    @FXML
    void mnOpenOnAction(ActionEvent event) throws IOException {
        Stage newStage = (Stage) txtEditor.getScene().getWindow();
        if (newStage.getTitle().charAt(0) == '*'){
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION,"Are you sure to open another file before saving this file...?", ButtonType.YES,ButtonType.NO);
            Optional<ButtonType> button = confirmationAlert.showAndWait();
            if (button.isPresent() && button.get() == ButtonType.YES){
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open a text file");
                File file = fileChooser.showOpenDialog(txtEditor.getScene().getWindow());
                saveFile = file;
                if (file == null) return;

                FileInputStream fis = new FileInputStream(file);
                byte[] bytes = fis.readAllBytes();
                fis.close();

                txtEditor.setText(new String(bytes));
                newStage.setTitle(file.getName());
            }
            else {
                mnSaveAsOnAction();
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open a text file");
                File file = fileChooser.showOpenDialog(txtEditor.getScene().getWindow());
                saveFile = file;
                if (file == null) return;

                FileInputStream fis = new FileInputStream(file);
                byte[] bytes = fis.readAllBytes();
                fis.close();

                txtEditor.setText(new String(bytes));
                newStage.setTitle(file.getName());
            }
        }
        else {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open a text file");
            File file = fileChooser.showOpenDialog(txtEditor.getScene().getWindow());
            saveFile = file;
            if (file == null) return;

            FileInputStream fis = new FileInputStream(file);
            byte[] bytes = fis.readAllBytes();
            fis.close();

            txtEditor.setText(new String(bytes));
            newStage.setTitle(file.getName());
        }
    }

    @FXML
    void mnPrintOnAction(ActionEvent event) {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job == null){
            new Alert(Alert.AlertType.ERROR,"No print job here...!").showAndWait();
            return;
        }
        boolean proceed = job.showPrintDialog(txtEditor.getScene().getWindow());
        JobSettings setting = job.getJobSettings();

        PageLayout pageLayout = setting.getPageLayout();
        double printableWidth = pageLayout.getPrintableWidth();
        double printableHeight = pageLayout.getPrintableHeight();

        TextArea tempTextArea = new TextArea(txtEditor.getText());
        tempTextArea.setPrefSize(printableWidth,printableHeight);
        tempTextArea.setWrapText(true);
        tempTextArea.setId("Temp Scroll Bar");

        if (proceed){
            boolean printed = job.printPage(tempTextArea);
            if (printed){
                job.endJob();
            }
            else {
                new Alert(Alert.AlertType.ERROR,"Unable to print, try again...!").showAndWait();
            }
        }
    }

    @FXML
    void mnSaveOnAction() throws IOException {
        Stage newStage = (Stage) txtEditor.getScene().getWindow();
        String getTitle = newStage.getTitle();
        if (!getTitle.equals("Untitled file") && !getTitle.equals("*Untitled file")){
            FileWriter fileWriter = new FileWriter(saveFile);
            fileWriter.write(txtEditor.getText());
            fileWriter.close();

            newStage.setTitle(getTitle.substring(1));
        }
        else {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save a text file");
            File file = fileChooser.showSaveDialog(txtEditor.getScene().getWindow());
            if (file == null) return;

            FileOutputStream fos = new FileOutputStream(file,false);
            String text = txtEditor.getText();
            byte[] bytes = text.getBytes();
            fos.write(bytes);
            fos.close();

            newStage.setTitle(file.getName());
        }
    }

    public void rootOnDragOver(DragEvent dragEvent) {
        dragEvent.acceptTransferModes(TransferMode.ANY);
    }

    public void rootOnDragDropped(DragEvent dragEvent) throws IOException {
        File droppedFile = dragEvent.getDragboard().getFiles().get(0);
        saveFile = droppedFile;
        FileInputStream fis = new FileInputStream(droppedFile);
        byte[] bytes = fis.readAllBytes();
        fis.close();

        txtEditor.setText(new String(bytes));

        Stage newStage = (Stage) txtEditor.getScene().getWindow();
        newStage.setTitle(droppedFile.getName());
    }

    public void txtEditorOnKeyPressed(KeyEvent keyEvent) {

    }

    public void mnSaveAsOnAction() throws IOException {
        Stage newStage = (Stage) txtEditor.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save a text file");
        File file = fileChooser.showSaveDialog(txtEditor.getScene().getWindow());
        if (file == null) return;

        FileOutputStream fos = new FileOutputStream(file,false);
        String text = txtEditor.getText();
        byte[] bytes = text.getBytes();
        fos.write(bytes);
        fos.close();
        newStage.setTitle(file.getName());
    }

    public void mnFindAndReplaceOnAction(ActionEvent actionEvent) throws IOException {
        Stage findAndReplaceStage = new Stage();
        findAndReplaceStage.setTitle("Find and Replace");

        URL fxmlFile = this.getClass().getResource("/view/FindAndReplace.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(fxmlFile);
        AnchorPane root = fxmlLoader.load();

        Scene scene = new Scene(root);
        findAndReplaceStage.setScene(scene);
        findAndReplaceStage.initModality(Modality.WINDOW_MODAL);
        findAndReplaceStage.initOwner(txtEditor.getScene().getWindow());
        findAndReplaceStage.show();
        findAndReplaceStage.centerOnScreen();

        FindAndReplaceController controller = fxmlLoader.getController();
        SimpleStringProperty observableText  = new SimpleStringProperty(txtEditor.getText());
        txtEditor.textProperty().bind(observableText);
        controller.initData(txtEditor);

        findAndReplaceStage.setOnCloseRequest((event) ->{
            txtEditor.textProperty().unbind();
        });
    }
}


package lk.ijde.dep10.editor.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lk.ijde.dep10.editor.util.SearchResults;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindAndReplaceController {

    @FXML
    private Button btnDown;

    @FXML
    private Button btnReplace;

    @FXML
    private Button btnReplaceAll;

    @FXML
    private Button btnUp;

    @FXML
    private CheckBox chkMatchCase;

    @FXML
    private Label lblResults;

    @FXML
    private TextField txtFind;

    @FXML
    private TextField txtReplace;
    private ArrayList<SearchResults> searchResults = new ArrayList<>();
    private ArrayList<Integer> startIndexes = new ArrayList<>();
    private TextArea txtEditor;
    private int pos = 0;
    private int startPos = 0;
    public void initData(TextArea txtEditor){
        this.txtEditor = txtEditor;
    }
    public void initialize(){
        txtFind.textProperty().addListener((value, previous, current) -> {
            calculateSearchResult();
        });
    }
    private void calculateSearchResult(){
        String query = txtFind.getText();
        searchResults.clear();
        startIndexes.clear();
        pos = 0;
        txtEditor.deselect();
        Pattern pattern;
        try {
            if (chkMatchCase.isSelected()){
                pattern = Pattern.compile(query);
            }
            else {
                pattern = Pattern.compile(query,Pattern.CASE_INSENSITIVE);
            }
        }
        catch (RuntimeException e){
            return;
        }
        Matcher matcher = pattern.matcher(txtEditor.getText());
        while (matcher.find()){
            int start = matcher.start();
            int end = matcher.end();
            SearchResults result = new SearchResults(start, end);
            startIndexes.add(start);
            searchResults.add(result);
        }
        lblResults.setText(String.format("%d Results",searchResults.size()));
        select();
    }
    private void select(){
        if (searchResults.isEmpty()) return;
        SearchResults searchResult = searchResults.get(pos);
        txtEditor.selectRange(searchResult.getStart(),searchResult.getEnd());
        lblResults.setText(String.format("%d/%d Results" ,(pos + 1),searchResults.size()));
    }

    @FXML
    void btnDownOnAction(ActionEvent event) {
        pos++;
        startPos++;
        if (pos == searchResults.size()){
            pos = -1;
            startPos = -1;
            return;
        }
        select();
    }

    @FXML
    void btnReplaceAllOnAction(ActionEvent event) {
        String findText = txtFind.getText();
        String replaceText = txtReplace.getText();

        if (txtFind.getText().isBlank()){
            new Alert(Alert.AlertType.ERROR,"Please input something to the find text").show();
            return;
        }

        if (txtReplace.getText().isBlank()){
            new Alert(Alert.AlertType.ERROR,"Please input something to the replace text").show();
            return;
        }

        txtEditor.textProperty().unbind();
        String newText = txtEditor.getText().replaceAll(findText,replaceText);
        txtEditor.setText(newText);

        Stage closeStage = (Stage) btnReplaceAll.getScene().getWindow();
        closeStage.close();
    }

    @FXML
    void btnReplaceOnAction(ActionEvent event) {
        if (txtFind.getText().isBlank()){
            new Alert(Alert.AlertType.ERROR,"Please input something to the find text").show();
            return;
        }

        if (txtReplace.getText().isBlank()){
            new Alert(Alert.AlertType.ERROR,"Please input something to the replace text").show();
            return;
        }

        txtEditor.textProperty().unbind();
        if (txtFind.getText().length() == 1){
            String firstPart = txtEditor.getText().substring(0,startIndexes.get(startPos));
            String secondPart = txtEditor.getText().substring(startIndexes.get(startPos)+1);
            String newSecondPart = secondPart.replaceFirst(txtEditor.getSelectedText(),txtReplace.getText());
            String finalString = firstPart + newSecondPart;
            txtEditor.setText(finalString);
        }
        if (txtFind.getText().length() != 1){
            String firstPart = txtEditor.getText().substring(0,startIndexes.get(startPos));
            String secondPart = txtEditor.getText().substring(startIndexes.get(startPos) + 1 + (txtFind.getText().length() - 1));
            String newSecondPart = secondPart.replaceFirst(txtEditor.getSelectedText(),txtReplace.getText());
            String finalString = firstPart + newSecondPart;
            txtEditor.setText(finalString);
        }
        Stage closeStage = (Stage) btnReplace.getScene().getWindow();
        closeStage.close();

    }

    @FXML
    void btnUpOnAction(ActionEvent event) {
        pos--;
        startPos--;
        if (pos < 0){
            pos = searchResults.size();
            startPos = startIndexes.size();
            return;
        }
        select();
    }

    @FXML
    void chkMatchCaseOnAction(ActionEvent event) {
        if (txtFind.getText().isBlank()){
            lblResults.setText("0 Results");
        }
        else {
            calculateSearchResult();
        }
    }

}


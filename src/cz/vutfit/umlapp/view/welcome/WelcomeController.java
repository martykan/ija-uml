package cz.vutfit.umlapp.view.welcome;

import cz.vutfit.umlapp.viewmodel.WelcomeViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class WelcomeController {
    @FXML
    public Label label;
    private WelcomeViewModel viewModel;

    public void init(WelcomeViewModel viewModel) {
        this.viewModel = viewModel;

        label.textProperty().bind(this.viewModel.buttonText);
    }

    public void handleOpenFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        this.viewModel.handleOpenFile(selectedFile);
    }

    public void handleNewFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showSaveDialog(new Stage());
        this.viewModel.handleNewFile(selectedFile);
    }
}

package cz.vutfit.umlapp.view.welcome;

import cz.vutfit.umlapp.model.DataModel;
import cz.vutfit.umlapp.model.ModelFactory;
import cz.vutfit.umlapp.view.IController;
import cz.vutfit.umlapp.view.ViewHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class WelcomeController implements IController {
    @FXML
    public Label label;

    private DataModel dataModel;
    private ViewHandler viewHandler;

    public void init(ModelFactory modelFactory, ViewHandler viewHandler) {
        this.dataModel = modelFactory.getDataModel();
        this.viewHandler = viewHandler;

        this.viewHandler.setTitle("IJA UML App");
    }

    public void handleOpenFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("IJA UML", "*.ijuml"));
        try {
            File selectedFile = fileChooser.showOpenDialog(new Stage());
            if (selectedFile == null) return;
            dataModel.openFile(selectedFile);
            this.viewHandler.openView("Main");
        } catch (Exception exception) {
            this.showErrorMessage(exception.getLocalizedMessage());
            exception.printStackTrace();
        }
    }

    public void handleNewFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("IJA UML", "*.ijuml"));
        try {
            File selectedFile = fileChooser.showSaveDialog(new Stage());
            if (selectedFile == null) return;
            dataModel.newFile(selectedFile);
            this.viewHandler.openView("Main");
        } catch (Exception exception) {
            this.showErrorMessage(exception.getLocalizedMessage());
            exception.printStackTrace();
        }
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Couldn't open file");
        alert.setContentText(message);
        alert.showAndWait();
    }
}

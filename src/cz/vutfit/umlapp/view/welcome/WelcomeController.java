/*
 * File: WelcomeController.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

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

/**
 * Class implementing Welcome screen after application launch
 */
public class WelcomeController implements IController {
    @FXML
    public Label label; /** heading of window **/

    private DataModel dataModel;
    private ViewHandler viewHandler;

    /**
     * Constructor for class
     * @param modelFactory
     * @param viewHandler
     */
    public void init(ModelFactory modelFactory, ViewHandler viewHandler) {
        this.dataModel = modelFactory.getDataModel();
        this.viewHandler = viewHandler;

        this.viewHandler.setTitle("IJA UML App");
    }

    /**
     * If user clicks on 'open existing file' button, this function is called
     * @param actionEvent
     */
    public void handleOpenFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("IJA UML", "*.ijuml"));
        try {
            File selectedFile = fileChooser.showOpenDialog(new Stage());
            if (selectedFile == null) return;
            dataModel.openFile(selectedFile);
            this.viewHandler.openView("ClassDiagram");
        } catch (Exception exception) {
            this.showErrorMessage(exception.getLocalizedMessage());
            exception.printStackTrace();
        }
    }

    /**
     * After clicking on 'Create new file' button, this function is called
     * @param actionEvent
     */
    public void handleNewFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("IJA UML", "*.ijuml"));
        try {
            File selectedFile = fileChooser.showSaveDialog(new Stage());
            if (selectedFile == null) return;
            dataModel.newFile(selectedFile);
            this.viewHandler.openView("ClassDiagram");
        } catch (Exception exception) {
            this.showErrorMessage(exception.getLocalizedMessage());
            exception.printStackTrace();
        }
    }

    /**
     * @param message
     */
    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Couldn't open file");
        alert.setContentText(message);
        alert.showAndWait();
    }
}

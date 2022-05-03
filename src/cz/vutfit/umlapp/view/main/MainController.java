/*
 * File: MainController.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.view.main;

import cz.vutfit.umlapp.model.DataModel;
import cz.vutfit.umlapp.model.ModelFactory;
import cz.vutfit.umlapp.model.commands.AddSequenceDiagramCommand;
import cz.vutfit.umlapp.model.uml.ClassDiagram;
import cz.vutfit.umlapp.view.IController;
import cz.vutfit.umlapp.view.ViewHandler;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

/**
 * Class for controller of MainView
 */
public class MainController implements IController {
    @FXML
    public Button closeButton;
    @FXML
    public Button saveButton;
    @FXML
    public Button undoButton;
    @FXML
    public TreeView<String> diagramTreeView;
    @FXML
    public ScrollPane scrollPane;
    @FXML
    public Pane anchorScrollPane;

    public DataModel dataModel;
    public ViewHandler viewHandler;

    /**
     * Listens to changes in diagramTreeView.
     * Used for displaying properties of any element from diagramTreeView.
     */
    ChangeListener<TreeItem<String>> handleDiagramSelection = (observableValue, oldItem, newItem) -> {
        if (newItem != null) {
            try {
                if (Objects.equals(newItem.getValue(), "Class diagram")) {
                    this.dataModel.setActiveDiagram(null);
                    this.viewHandler.openView("ClassDiagram");
                } else {
                    this.dataModel.setActiveDiagram(newItem.getValue());
                    this.viewHandler.openView("SequenceDiagram");
                }
            } catch (IOException e) {
                this.showErrorMessage(e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
    };

    @Override
    public void init(ModelFactory modelFactory, ViewHandler viewHandler) {
        this.dataModel = modelFactory.getDataModel();
        this.viewHandler = viewHandler;
        this.diagramTreeView.getSelectionModel().selectedItemProperty().addListener(handleDiagramSelection);

        this.updateView();

        Platform.runLater(MainController.this::initKeyboardShortcuts);
    }

    /**
     * Updates entire View (UI).
     */
    public void updateView() {
        try {
            viewHandler.setTitle("IJA UML App - " + this.dataModel.getFileName() + (this.dataModel.getFileSaveStatus() ? "" : "*"));

            // Diagrams menu
            TreeViewItemModel diagrams = new TreeViewItemModel(this.dataModel, diagramTreeView, EDataType.DIAGRAM);
            diagrams.showTreeItem();
            diagrams.rootViewUpdate();
        } catch (Exception e) {
            this.showErrorMessage(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    private void initKeyboardShortcuts() {
        try {
            closeButton.getScene().getAccelerators().put(
                    new KeyCharacterCombination("w", KeyCombination.SHORTCUT_DOWN),
                    () -> closeButton.fire()
            );
            saveButton.getScene().getAccelerators().put(
                    new KeyCharacterCombination("s", KeyCombination.SHORTCUT_DOWN),
                    () -> saveButton.fire()
            );
            undoButton.getScene().getAccelerators().put(
                    new KeyCharacterCombination("z", KeyCombination.SHORTCUT_DOWN),
                    () -> undoButton.fire()
            );
        } catch (Exception e) {
            this.showErrorMessage(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    /**
     * After clicking on save icon, this function is called. It saves changes user made into file opened.
     * @param actionEvent
     */
    public void handleSave(ActionEvent actionEvent) {
        try {
            this.dataModel.saveFile();
            this.updateView();
            /** Save info window **/
            /**
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "", ButtonType.OK);
            alert.setTitle("File Save");
            alert.setHeaderText("File saved successfully");
            alert.showAndWait();
             **/
        } catch (IOException e) {
            this.showErrorMessage(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    /**
     * Function called after clicking on home icon. View changes back to 'Welcome screen'.
     * @param actionEvent
     */
    public void handleClose(ActionEvent actionEvent) {
        try {
            /** Save window **/
            if (this.dataModel.getFileSaveStatus() == false) {
                ButtonType save = new ButtonType("Save and leave", ButtonBar.ButtonData.APPLY);
                Alert alert = new Alert(Alert.AlertType.WARNING, "Leave without saving changes?", ButtonType.YES, save, ButtonType.CANCEL);
                alert.setTitle("Leave Warning");
                alert.setHeaderText("You have unsaved changes in this file.");
                alert.showAndWait();
                if (alert.getResult() == ButtonType.YES) { // Leave without saving
                    this.viewHandler.openView("Welcome");
                } else if (alert.getResult() == save) { // Save and leave
                    this.dataModel.saveFile();
                    this.viewHandler.openView("Welcome");
                } else if (alert.getResult() == ButtonType.NO) { // Do not leave
                    return;
                }
            } else {
                this.viewHandler.openView("Welcome");
            }
        } catch (IOException e) {
            this.showErrorMessage(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    /**
     * After clicking on undo button, this function is called.
     * @param actionEvent
     */
    public void handleUndo(ActionEvent actionEvent) {
        try {
            if (this.dataModel.isCommandHistoryEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "You have not performed any command or you have already\nundo-ed all operations. Nothing happened.", ButtonType.OK);
                alert.setTitle("Undo operation");
                alert.setHeaderText("Command history is empty");
                alert.showAndWait();
            }
            this.dataModel.undo();
            this.updateView();
        } catch (Exception e) {
            this.showErrorMessage("Unable to undo operation", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    /**
     * After clicking on + button (next to Diagrams in menu), this function is called.
     * It is supposed to add new sequence diagram. Currently TODO.
     * @param actionEvent
     */
    public void handleAddDiagram(ActionEvent actionEvent) {
        try {
            TextInputDialog dialog = new TextInputDialog("");
            dialog.setTitle("New Sequence Diagram");
            dialog.setHeaderText(null);
            dialog.setContentText("New Diagram Name:");

            // disable OK button if text-input is empty
            BooleanBinding validName = Bindings.createBooleanBinding(() -> {
                return dialog.getEditor().getText().equals("");
            }, dialog.getEditor().textProperty());
            dialog.getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(validName);

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(diagramName -> {
                try {
                    this.dataModel.executeCommand(new AddSequenceDiagramCommand(diagramName));
                    this.updateView();
                } catch (Exception e) {
                    this.showErrorMessage("Unable to add new diagram", e.getLocalizedMessage());
                    e.printStackTrace();
                }
            });
        } catch (Exception exception) {
            this.showErrorMessage(exception.getLocalizedMessage());
            exception.printStackTrace();
        }
    }

    /**
     * After clicking on camera button in menu, this function is called.
     * Outputs entire diagram into PNG image.
     * @param actionEvent
     */
    public void handleSnapshot(ActionEvent actionEvent) {
        // Choose a file for output
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG", "*.png"));
        fileChooser.setTitle("Save Snapshot");
        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
            // Calculate used region
            int minX = 10000, minY = 10000;
            int maxX = 0, maxY = 0;
            for (Node node : this.anchorScrollPane.getChildren()) {
                if (node.getBoundsInParent().getMinX() < minX)
                    minX = (int) node.getBoundsInParent().getMinX();
                if (node.getBoundsInParent().getMinY() < minY)
                    minY = (int) node.getBoundsInParent().getMinY();
                if (node.getBoundsInParent().getMaxX() > maxX)
                    maxX = (int) node.getBoundsInParent().getMaxX();
                if (node.getBoundsInParent().getMaxY() > maxY)
                    maxY = (int) node.getBoundsInParent().getMaxY();
            }
            // Set snapshot parameters
            SnapshotParameters sp = new SnapshotParameters();
            Rectangle2D rect = new Rectangle2D(minX - 50, minY - 50, maxX - minX + 100, maxY - minY + 100);
            sp.setViewport(rect);
            sp.setFill(Color.WHITE);
            WritableImage wi = new WritableImage((int) rect.getWidth(), (int) rect.getHeight());
            this.anchorScrollPane.getStyleClass().remove("checkerboard");
            this.anchorScrollPane.snapshot(sp, wi);
            this.anchorScrollPane.getStyleClass().add("checkerboard");
            // Save snapshot as PNG
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(wi, null), "png", file);
            } catch (IOException e) {
                this.showErrorMessage("Error when saving snapshot", e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
    }

    public void showErrorMessage(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Internal error (exception)");
        alert.setContentText(message);
        alert.showAndWait();
    }
}

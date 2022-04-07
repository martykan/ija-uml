package cz.vutfit.umlapp.view.main;

import cz.vutfit.umlapp.model.DataModel;
import cz.vutfit.umlapp.model.ModelFactory;
import cz.vutfit.umlapp.model.commands.AddClassCommand;
import cz.vutfit.umlapp.view.IController;
import cz.vutfit.umlapp.view.ViewHandler;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.input.KeyCombination;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;

public class MainController implements IController {
    @FXML
    public Button closeButton;
    @FXML
    public Button saveButton;
    @FXML
    public Button undoButton;
    @FXML
    public Label label;
    @FXML
    public TextArea textArea;

    private DataModel dataModel;
    private ViewHandler viewHandler;

    @Override
    public void init(ModelFactory modelFactory, ViewHandler viewHandler) {
        this.dataModel = modelFactory.getDataModel();
        this.viewHandler = viewHandler;

        this.updateView();

        Platform.runLater(MainController.this::initKeyboardShortcuts);
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
            e.printStackTrace();
        }
    }

    public void handleSave(ActionEvent actionEvent) {
        try {
            this.dataModel.saveFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleClose(ActionEvent actionEvent) {
        try {
            this.viewHandler.openView("Welcome");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateView() {
        viewHandler.setTitle("IJA UML App - " + this.dataModel.getFileName());
        this.label.setText("Opened file " + this.dataModel.getFileName());

        this.textArea.setEditable(false);
        this.textArea.setText(this.dataModel.getData().getClasses().stream().map(it -> it.name).collect(Collectors.joining("\n")));
    }

    public void handleUndo(ActionEvent actionEvent) {
        this.dataModel.undo();
        this.updateView();
    }

    public void handleAddClass(ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("New Class");
        dialog.setHeaderText(null);
        dialog.setContentText("New Class Name:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(className -> {
            this.dataModel.executeCommand(new AddClassCommand(className));
            this.updateView();
        });
    }
}

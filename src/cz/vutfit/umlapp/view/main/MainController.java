package cz.vutfit.umlapp.view.main;

import cz.vutfit.umlapp.model.DataModel;
import cz.vutfit.umlapp.model.ModelFactory;
import cz.vutfit.umlapp.model.commands.AddClassCommand;
import cz.vutfit.umlapp.model.commands.AddClassMethodCommand;
import cz.vutfit.umlapp.model.uml.Attributes;
import cz.vutfit.umlapp.model.uml.ClassDiagram;
import cz.vutfit.umlapp.model.uml.EAttribVisibility;
import cz.vutfit.umlapp.view.IController;
import cz.vutfit.umlapp.view.ViewHandler;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;

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
    public TextArea textArea;
    @FXML
    public TreeView<String> treeView;
    @FXML
    public HBox boxClassOptions;

    private DataModel dataModel;
    private ViewHandler viewHandler;

    private String selectedClass;
    ChangeListener<TreeItem<String>> handleClassSelection = (observableValue, oldItem, newItem) -> {
        if (newItem != null) {
            this.selectedClass = newItem.getValue();
        } else {
            this.selectedClass = null;
        }
        boxClassOptions.setVisible(this.selectedClass != null);
    };

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

    @Override
    public void init(ModelFactory modelFactory, ViewHandler viewHandler) {
        this.dataModel = modelFactory.getDataModel();
        this.viewHandler = viewHandler;
        this.treeView.getSelectionModel().selectedItemProperty().addListener(handleClassSelection);

        this.updateView();

        Platform.runLater(MainController.this::initKeyboardShortcuts);
    }

    private void updateView() {
        viewHandler.setTitle("IJA UML App - " + this.dataModel.getFileName());

        this.textArea.setEditable(false);
        this.textArea.setText(this.dataModel.getData().getClasses().stream().map(it -> it.name).collect(Collectors.joining("\n")));

        TreeItem<String> dummyRoot = new TreeItem<>();
        for (ClassDiagram classDiagram : this.dataModel.getData().getClasses()) {
            TreeItem<String> item = new TreeItem<>(classDiagram.getName());
            for (Attributes attributes : classDiagram.getAttribs()) {
                item.getChildren().add(new TreeItem<String>(attributes.getNameWithPrefix()));
            }
            dummyRoot.getChildren().add(item);
        }
        this.treeView.setShowRoot(false);
        this.treeView.setRoot(dummyRoot);

        boxClassOptions.setVisible(this.selectedClass != null);
    }

    public void handleUndo(ActionEvent actionEvent) {
        try {
            this.dataModel.undo();
            this.updateView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleAddClass(ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("New Class");
        dialog.setHeaderText(null);
        dialog.setContentText("New Class Name:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(className -> {
            try {
                this.dataModel.executeCommand(new AddClassCommand(className));
                this.updateView();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void handleAddClassMethod(ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("New Class Method");
        dialog.setHeaderText(null);
        dialog.setContentText("New Class Method Name:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(className -> {
            try {
                // TODO store the selected class properly in the TreeView
                int selectedClassId = this.dataModel.getData().getClasses()
                        .stream()
                        .filter(it -> it.getName().equals(selectedClass))
                        .map(ClassDiagram::getID)
                        .findFirst()
                        .orElseThrow();
                this.dataModel.executeCommand(new AddClassMethodCommand(selectedClassId, className, EAttribVisibility.PUBLIC));
                this.updateView();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}

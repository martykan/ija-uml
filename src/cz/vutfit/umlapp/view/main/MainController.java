package cz.vutfit.umlapp.view.main;

import cz.vutfit.umlapp.model.DataModel;
import cz.vutfit.umlapp.model.ModelFactory;
import cz.vutfit.umlapp.model.commands.AddClassCommand;
import cz.vutfit.umlapp.model.commands.AddClassMethodCommand;
import cz.vutfit.umlapp.model.commands.AddClassAttributeCommand;
import cz.vutfit.umlapp.model.commands.RemoveClassCommand;
import cz.vutfit.umlapp.model.commands.RemoveClassAttributeCommand;
import cz.vutfit.umlapp.model.commands.RemoveClassMethodCommand;
import cz.vutfit.umlapp.model.commands.DragClassCommand;
import cz.vutfit.umlapp.model.uml.Attributes;
import cz.vutfit.umlapp.model.uml.ClassDiagram;
import cz.vutfit.umlapp.model.uml.EAttribVisibility;
import cz.vutfit.umlapp.model.uml.Methods;
import cz.vutfit.umlapp.view.IController;
import cz.vutfit.umlapp.view.ViewHandler;
import cz.vutfit.umlapp.view.main.TreeViewItemModel;
import cz.vutfit.umlapp.view.main.EDataType;
import cz.vutfit.umlapp.view.components.DraggableUMLClassView;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;

import java.lang.reflect.Array;
import java.util.ArrayList;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

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
    public TreeView<String> classTreeView;
    @FXML
    public TreeView<String> diagramTreeView;
    @FXML
    public HBox boxClassOptions;
    @FXML
    public ScrollPane scrollPane;
    @FXML
    public Pane anchorScrollPane;

    private DataModel dataModel;
    private ViewHandler viewHandler;
    private TreeViewItemModel diagrams;
    private TreeViewItemModel classes;

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

    private void initDragDrop() {
        AtomicReference<Double> totalZoom = new AtomicReference<>(1.0);
        anchorScrollPane.setOnZoom(event -> {
            Scale scale = new Scale();
            scale.setPivotX(event.getX());
            scale.setPivotY(event.getY());
            double newZoom = totalZoom.get() * event.getZoomFactor();
            if (newZoom <= 2 && newZoom >= 0.1) {
                totalZoom.set(newZoom);
                scale.setX(event.getZoomFactor());
                scale.setY(event.getZoomFactor());
                anchorScrollPane.getTransforms().add(scale);
            }
            event.consume();
        });
        anchorScrollPane.getChildren().clear();
        for (ClassDiagram classDiagram : this.dataModel.getData().getClasses()) {
            DraggableUMLClassView node = new DraggableUMLClassView(classDiagram, totalZoom);
            node.setOnMouseReleased(event -> {
                if (node.getTranslateX() == classDiagram.positionX && node.getTranslateY() == classDiagram.positionY)
                    return;
                try {
                    this.dataModel.executeCommand(new DragClassCommand(classDiagram.getID(), node.getTranslateX(), node.getTranslateY()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            anchorScrollPane.getChildren().add(node);
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
        this.classTreeView.getSelectionModel().selectedItemProperty().addListener(handleClassSelection);

        this.updateView();

        Platform.runLater(MainController.this::initKeyboardShortcuts);
    }

    private void updateView() {
        viewHandler.setTitle("IJA UML App - " + this.dataModel.getFileName());

        /** Diagrams menu **/
        this.diagrams = new TreeViewItemModel(this.dataModel, diagramTreeView, EDataType.CLASS_DIAGRAM);
        this.diagrams.showTreeItem();
        this.diagrams.rootViewUpdate();

        /** Classes menu **/
        this.classes = new TreeViewItemModel(this.dataModel, classTreeView, EDataType.CLASS);
        this.classes.showTreeItem();
        this.classes.rootViewUpdate();

        this.initDragDrop();

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

    public void handleRemove(ActionEvent actionEvent) {
        try {
            String id;
            if (classTreeView.getSelectionModel().getSelectedItem() != null) { // no item selected / 0 items in tree-view
                id = classTreeView.getSelectionModel().getSelectedItem().getValue();
            } else {
                return;
            }
            ClassDiagram myclass = this.dataModel.getData().getClassByName(id);
            if (myclass == null) { // currently not selected class
                id = classTreeView.getSelectionModel().getSelectedItem().getParent().getValue();
                myclass = this.dataModel.getData().getClassByName(id);
                ArrayList<Attributes> a = myclass.getAttribs();
                ArrayList<Methods> m = myclass.getMethods();
                String currentOption = classTreeView.getSelectionModel().getSelectedItem().getValue();
                String[] x = currentOption.split("[+]", 2)[1].split("[(]", 2);
                if (x.length > 1 && x[1].equals(")")) {
                    for (Methods y : m) {
                        if (y.getName().equals(x[0])) {
                            this.dataModel.executeCommand(new RemoveClassMethodCommand(myclass, x[0], y.getVisibility()));
                            break;
                        }
                    }
                    this.updateView();
                } else {
                    for (Attributes z : a) {
                        if (z.getName().equals(x[0])) {
                            this.dataModel.executeCommand(new RemoveClassAttributeCommand(myclass, x[0], z.getVisibility()));
                            break;
                        }
                    }
                    this.updateView();
                }

            } else {
                this.dataModel.executeCommand(new RemoveClassCommand(myclass.getID()));
                this.updateView();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // TODO: tlacitko + u Diagrams, pridava Sekvenci diagramy (=> Need TODO: model - seq. diagram)
    public void handleAddDiagram(ActionEvent actionEvent) {
        return;
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
                String id;
                if (classTreeView.getSelectionModel().getSelectedItem() != null) { // no item selected / 0 items in tree-view
                    id = classTreeView.getSelectionModel().getSelectedItem().getValue();
                } else {
                    return;
                }
                ClassDiagram myclass = this.dataModel.getData().getClassByName(id);
                if (myclass == null) // if not selected class but some attribute/method/relationship in that class
                    myclass = this.dataModel.getData().getClassByName(classTreeView.getSelectionModel().getSelectedItem().getParent().getValue());
                this.dataModel.executeCommand(new AddClassMethodCommand(myclass.getID(), className, EAttribVisibility.PUBLIC));
                this.updateView();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void handleAddAttribute(ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("New Class Attribute");
        dialog.setHeaderText(null);
        dialog.setContentText("New Class Attribute Name:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(className -> {
            try {
                // TODO store the selected class properly in the TreeView
                String id;
                if (classTreeView.getSelectionModel().getSelectedItem() != null) { // no item selected / 0 items in tree-view
                    id = classTreeView.getSelectionModel().getSelectedItem().getValue();
                } else {
                    return;
                }
                ClassDiagram myclass = this.dataModel.getData().getClassByName(id);
                if (myclass == null) // if not selected class but some attribute/method/relationship in that class
                    myclass = this.dataModel.getData().getClassByName(classTreeView.getSelectionModel().getSelectedItem().getParent().getValue());
                this.dataModel.executeCommand(new AddClassAttributeCommand(myclass.getID(), className, EAttribVisibility.PUBLIC));
                this.updateView();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void handleAddRelation(ActionEvent actionEvent) {
        return;
    }
}

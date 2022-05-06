/*
 * File: ClassDiagramController.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.view.classdiagram;

import cz.vutfit.umlapp.model.ModelFactory;
import cz.vutfit.umlapp.model.commands.*;
import cz.vutfit.umlapp.model.uml.ClassDiagram;
import cz.vutfit.umlapp.model.uml.EAttribVisibility;
import cz.vutfit.umlapp.model.uml.ERelationType;
import cz.vutfit.umlapp.model.uml.Relationships;
import cz.vutfit.umlapp.view.ViewHandler;
import cz.vutfit.umlapp.view.components.DraggableUMLClassView;
import cz.vutfit.umlapp.view.components.DraggableUMLRelationView;
import cz.vutfit.umlapp.view.components.EPropertyType;
import cz.vutfit.umlapp.view.components.PropertiesView;
import cz.vutfit.umlapp.view.main.EDataType;
import cz.vutfit.umlapp.view.main.MainController;
import cz.vutfit.umlapp.view.main.TreeViewDataHolder;
import cz.vutfit.umlapp.view.main.TreeViewItemModel;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.transform.Scale;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Class for controller of MainView
 */
public class ClassDiagramController extends MainController {
    @FXML
    public TreeView<TreeViewDataHolder> classTreeView;
    @FXML
    public HBox boxClassOptions;
    /**
     * Area for buttons that's showed after user clicks on any item in classTreeView
     */
    @FXML
    public PropertiesView propertiesView;

    private boolean isSelectedClass;

    /**
     * Listens to changes in classTreeView.
     * Used for displaying properties of any element from classTreeView.
     *
     * @see #handleProperties(TreeItem)
     */
    final ChangeListener<TreeItem<TreeViewDataHolder>> handleClassSelection = (observableValue, oldItem, newItem) -> {
        if (newItem != null) {
            this.isSelectedClass = true;
            handleProperties(newItem);
        } else {
            this.isSelectedClass = false;
            propertiesView.resetProperties();
            try {
                propertiesView.addPropertyLine("Nothing selected", "");
            } catch (Exception e) {
                this.showErrorMessage(e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
        boxClassOptions.setVisible(this.isSelectedClass);
    };

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
        HashMap<Integer, DraggableUMLClassView> classNodes = new HashMap<>();
        for (ClassDiagram classDiagram : this.dataModel.getData().getClasses()) {
            DraggableUMLClassView node = new DraggableUMLClassView(classDiagram, totalZoom);
            node.setOnMouseReleased(event -> {
                // Save new position
                if (node.getTranslateX() == classDiagram.positionX && node.getTranslateY() == classDiagram.positionY)
                    return;
                try {
                    this.dataModel.executeCommand(new DragClassCommand(classDiagram.getID(), node.getTranslateX(), node.getTranslateY()));
                } catch (Exception e) {
                    this.showErrorMessage(e.getLocalizedMessage());
                    e.printStackTrace();
                }
            });
            node.setOnMouseClicked(event -> {
                // Select in treeView
                int index = -1;
                int i = 0;
                for (TreeItem<TreeViewDataHolder> treeItem : this.classTreeView.getRoot().getChildren()) {
                    if (treeItem.getValue().getClassDiagram().getName().equals(classDiagram.getName())) {
                        index = i;
                        treeItem.setExpanded(true);
                    }
                    if (treeItem.isExpanded()) {
                        i += treeItem.getChildren().size();
                    }
                    i++;
                }
                this.classTreeView.getSelectionModel().select(index);
            });
            anchorScrollPane.getChildren().add(node);
            classNodes.put(classDiagram.getID(), node);
        }

        for (Relationships relationship : this.dataModel.getData().getRelationships()) {
            DraggableUMLClassView node = classNodes.get(relationship.fromId);
            DraggableUMLClassView node2 = classNodes.get(relationship.toId);
            if (node == null || node2 == null) continue;
            DraggableUMLRelationView line = new DraggableUMLRelationView(node, node2, totalZoom, relationship);
            anchorScrollPane.getChildren().add(line);
        }
    }

    @Override
    public void init(ModelFactory modelFactory, ViewHandler viewHandler) {
        super.init(modelFactory, viewHandler);
        this.classTreeView.setCellFactory(TreeViewDataHolder.getCellFactory(this.dataModel));
        this.classTreeView.getSelectionModel().selectedItemProperty().addListener(handleClassSelection);
        try {
            propertiesView.addPropertyLine("Nothing selected", "");
            propertiesView.setOnUpdated(this::updateView);
        } catch (Exception e) {
            this.showErrorMessage(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    /**
     * Updates entire View (UI).
     */
    public void updateView() {
        super.updateView();
        try {
            // Classes menu
            TreeViewItemModel classes = new TreeViewItemModel(this.dataModel, classTreeView, EDataType.CLASS);
            classes.buildTree();
            classes.rootViewUpdate();

            // Handle inconsistencies
            this.dataModel.getErrorClass().checkClassDiagram();

            this.initDragDrop();

            boxClassOptions.setVisible(this.isSelectedClass);
        } catch (Exception e) {
            this.showErrorMessage(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    /**
     * Called after clicking on + button (next to Classes header in menu)
     */
    public void handleAddClass() {
        try {
            TextInputDialog dialog = new TextInputDialog("");
            dialog.setTitle("New Class");
            dialog.setHeaderText(null);
            dialog.setContentText("New Class Name:");

            // disable OK button if text-input is empty
            BooleanBinding validName = Bindings.createBooleanBinding(() ->
                    dialog.getEditor().getText().equals(""), dialog.getEditor().textProperty());
            dialog.getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(validName);

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(className -> {
                try {
                    this.dataModel.executeCommand(new AddClassCommand(className));
                    this.updateView();
                } catch (Exception e) {
                    this.showErrorMessage("Unable to add new class", e.getLocalizedMessage());
                    e.printStackTrace();
                }
            });
        } catch (Exception exception) {
            this.showErrorMessage(exception.getLocalizedMessage());
            exception.printStackTrace();
        }
    }

    /**
     * Called after clicking on - button (next to Classes in menu).
     * The button deletes anything that's selected in classTreeView - class, method, attribute or relationship.
     */
    public void handleRemove() {
        try {
            TreeItem<TreeViewDataHolder> selectedTreeItem;
            if (classTreeView.getSelectionModel().getSelectedItem() != null) { // no item selected / 0 items in tree-view
                selectedTreeItem = classTreeView.getSelectionModel().getSelectedItem();
            } else {
                return;
            }
            TreeViewDataHolder selectedItem = selectedTreeItem.getValue();
            if (selectedItem.getDataType() == EDataType.CLASS) {
                this.dataModel.executeCommand(new RemoveClassCommand(selectedItem.getClassDiagram().getID()));
                this.updateView();
            } else if (selectedItem.getDataType() == EDataType.ATTRIBUTE) {
                ClassDiagram selectedClass = selectedTreeItem.getParent().getValue().getClassDiagram();
                this.dataModel.executeCommand(new RemoveClassAttributeCommand(selectedClass, selectedItem.getAttribute()));
                this.updateView();
            } else if (selectedItem.getDataType() == EDataType.METHOD) {
                ClassDiagram selectedClass = selectedTreeItem.getParent().getValue().getClassDiagram();
                this.dataModel.executeCommand(new RemoveClassMethodCommand(selectedClass, selectedItem.getMethod()));
                this.updateView();
            } else if (selectedItem.getDataType() == EDataType.RELATIONSHIP) {
                this.dataModel.executeCommand(new RemoveClassRelationshipCommand(selectedItem.getRelationship().getID()));
                this.updateView();
            }
        } catch (Exception e) {
            this.showErrorMessage(e.getLocalizedMessage());
            e.printStackTrace();

        }
    }

    /**
     * In boxClassOptions after clicking on New Class button, this function is called.
     * Add new method to class.
     * If selected item in classTreeView is class, method will be added to this class.
     * If selected item is method, attribute or relationship, new method will be added to the same class as the selected item.
     */
    public void handleAddClassMethod() {
        try {
            Dialog<Pair<String, String>> dialog = new Dialog<>();
            dialog.setTitle("New Class Method");
            dialog.setHeaderText("Add new method to class");

            ButtonType createButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField visBox = new TextField();

            TextField currVis = new TextField();

            grid.add(new Label("Name: "), 0, 0);
            grid.add(currVis, 1, 0);
            grid.add(new Label("Type: "), 0, 1);
            grid.add(visBox, 1, 1);
            dialog.getDialogPane().setContent(grid);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == createButtonType) {
                    String x = visBox.getText();
                    return new Pair<>(currVis.getText(), x);
                }
                return null;
            });

            BooleanBinding validName = Bindings.createBooleanBinding(() -> {
                if (currVis.getText().equals("")) {
                    return true;
                } else if (!currVis.getText().matches("^[A-z0-9]+\\(.*\\)[ ]?$")) {
                    return true;
                } else {
                    return false;
                }
            }, currVis.textProperty());
            dialog.getDialogPane().lookupButton(createButtonType).disableProperty().bind(validName);

            Optional<Pair<String, String>> result = dialog.showAndWait();
            result.ifPresent(className -> {
                try {
                    String type = (className.getValue().equals("") ? "void" : className.getValue());
                    ClassDiagram selectedClass = TreeViewDataHolder.getTreeViewSelectedClass(this.classTreeView);
                    if (selectedClass == null) return;
                    this.dataModel.executeCommand(new AddClassMethodCommand(selectedClass.getID(), className.getKey(), EAttribVisibility.PUBLIC, type));
                    this.updateView();
                } catch (Exception e) {
                    this.showErrorMessage("Unable to add new method", e.getLocalizedMessage());
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            this.showErrorMessage(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    /**
     * In boxClassOptions after clicking on New Attribute, this function is called.
     * Works same handleAddClassMethod, instead of method it adds attribute.
     *
     * @see #handleAddClassMethod()
     */
    public void handleAddAttribute() {
        try {
            Dialog<Pair<String, String>> dialog = new Dialog<>();
            dialog.setTitle("New Class Attribute");
            dialog.setHeaderText("Add new attribute to class");

            ButtonType createButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField visBox = new TextField();

            TextField currVis = new TextField();

            grid.add(new Label("Name: "), 0, 0);
            grid.add(currVis, 1, 0);
            grid.add(new Label("Type: "), 0, 1);
            grid.add(visBox, 1, 1);
            dialog.getDialogPane().setContent(grid);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == createButtonType) {
                    String x = visBox.getText();
                    return new Pair<>(currVis.getText(), x);
                }
                return null;
            });

            BooleanBinding validName = Bindings.createBooleanBinding(() -> {
                if (currVis.getText().equals("")) {
                    return true;
                } else {
                    return false;
                }
            }, currVis.textProperty());
            dialog.getDialogPane().lookupButton(createButtonType).disableProperty().bind(validName);

            Optional<Pair<String, String>> result = dialog.showAndWait();
            result.ifPresent(className -> {
                try {
                    String type = (className.getValue().equals("") ? "void" : className.getValue());
                    ClassDiagram selectedClass = TreeViewDataHolder.getTreeViewSelectedClass(this.classTreeView);
                    if (selectedClass == null) return;
                    this.dataModel.executeCommand(new AddClassAttributeCommand(selectedClass.getID(), className.getKey(), EAttribVisibility.PUBLIC, type));
                    this.updateView();
                } catch (Exception e) {
                    this.showErrorMessage("Unable to add new attribute", e.getLocalizedMessage());
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            this.showErrorMessage(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    /**
     * In boxClassOptions after clicking on New Relationship button, this function is called.
     * It is supposed to add new relationship from current class to another, works same way
     * as handleAddClassMethod or as handleAddAttribute.
     *
     * @see #handleAddClassMethod()
     */
    public void handleAddRelation() {
        try {
            ClassDiagram selectedClass = TreeViewDataHolder.getTreeViewSelectedClass(this.classTreeView);

            // Create the custom dialog.
            Dialog<ArrayList<String>> dialog = new Dialog<>();
            dialog.setTitle("New Relationship");
            dialog.setHeaderText("Create a new relationship between two classes");

            ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            ChoiceBox<String> typeBox = new ChoiceBox<>();
            ChoiceBox<String> fromClassBox = new ChoiceBox<>();
            ChoiceBox<String> toClassBox = new ChoiceBox<>();

            for (ERelationType x : ERelationType.values()) {
                typeBox.getItems().add(x.relationToString());
            }
            typeBox.getSelectionModel().selectFirst();

            for (ClassDiagram c : this.dataModel.getData().getClasses()) {
                fromClassBox.getItems().add(c.getName());
                toClassBox.getItems().add(c.getName());
            }
            fromClassBox.getSelectionModel().selectFirst();
            if (selectedClass != null) {
                fromClassBox.getSelectionModel().select(selectedClass.getName());
            }
            toClassBox.getSelectionModel().selectFirst();

            grid.add(new Label("Type:"), 0, 0);
            grid.add(typeBox, 1, 0);
            grid.add(new Label("From:"), 0, 1);
            grid.add(fromClassBox, 1, 1);
            grid.add(new Label("To:"), 0, 2);
            grid.add(toClassBox, 1, 2);
            dialog.getDialogPane().setContent(grid);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == createButtonType) {
                    ArrayList<String> x = new ArrayList<>();
                    x.add(fromClassBox.getSelectionModel().getSelectedItem());
                    x.add(toClassBox.getSelectionModel().getSelectedItem());
                    x.add(typeBox.getSelectionModel().getSelectedItem());
                    return x;
                }
                return null;
            });

            Optional<ArrayList<String>> result = dialog.showAndWait();

            result.ifPresent(returned -> {
                try {
                    Integer idFrom = this.dataModel.getData().getClassByName(returned.get(0)).getID();
                    Integer idTo = this.dataModel.getData().getClassByName(returned.get(1)).getID();
                    ERelationType relType = ERelationType.fromString(returned.get(2));
                    this.dataModel.executeCommand(new AddClassRelationshipCommand(idFrom, idTo, relType));
                    this.updateView();
                } catch (Exception e) {
                    this.showErrorMessage("Unable to add new relationship", e.getLocalizedMessage());
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            this.showErrorMessage(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    /**
     * This function is called after item selection in classTreeView is changed (by user).
     * Shows item-element properties (in Properties section in menu) depending on its type - class, method, attribute or relationship.
     * If nothing is selected, simply shows 'nothing selected'.
     *
     * @param selectedTreeItem currently selected item in classTreeView
     * @see #handleClassSelection
     */
    public void handleProperties(TreeItem<TreeViewDataHolder> selectedTreeItem) {
        try {
            propertiesView.setDataModel(this.dataModel);
            propertiesView.setClassTreeView(this.classTreeView);
            TreeViewDataHolder selectedItem = selectedTreeItem.getValue();
            if (selectedItem.getDataType() == EDataType.CLASS) {
                ClassDiagram selectedClass = selectedItem.getClassDiagram();
                propertiesView.resetProperties();
                propertiesView.setGroupType(EPropertyType.CLASS);
                propertiesView.setID(selectedClass.getID());
                propertiesView.addPropertyLine("Class", selectedClass.getName());
                propertiesView.addPropertyLine("Attributes", String.valueOf(selectedClass.getAttribs().size()));
                propertiesView.addPropertyLine("Methods", String.valueOf(selectedClass.getMethods().size()));
            } else if (selectedItem.getDataType() == EDataType.ATTRIBUTE) {
                ClassDiagram selectedClass = selectedTreeItem.getParent().getValue().getClassDiagram();
                propertiesView.resetProperties();
                propertiesView.setGroupType(EPropertyType.ATTRIBUTE);
                propertiesView.setID(selectedItem.getAttribute().getName());
                propertiesView.addPropertyLine("Attribute", selectedItem.getAttribute().getName());
                propertiesView.addPropertyLine("Type", selectedItem.getAttribute().getType());
                propertiesView.addPropertyLine("Class", String.valueOf(selectedClass.getName()));
                propertiesView.addPropertyLine("Visibility", selectedItem.getAttribute().getVisibility().getVisibilityString());
            } else if (selectedItem.getDataType() == EDataType.METHOD) {
                ClassDiagram selectedClass = selectedTreeItem.getParent().getValue().getClassDiagram();
                propertiesView.resetProperties();
                propertiesView.setGroupType(EPropertyType.METHOD);
                propertiesView.setID(selectedItem.getMethod().getName());
                propertiesView.addPropertyLine("Method", selectedItem.getMethod().getName());
                propertiesView.addPropertyLine("Return type", selectedItem.getMethod().getType());
                propertiesView.addPropertyLine("Class", String.valueOf(selectedClass.getName()));
                propertiesView.addPropertyLine("Visibility", selectedItem.getMethod().getVisibility().getVisibilityString());
            } else if (selectedItem.getDataType() == EDataType.RELATIONSHIP) {
                String relType = selectedItem.getRelationship().getType().relationToString();
                int fromClassID = selectedItem.getRelationship().getFromClassID();
                int toClassID = selectedItem.getRelationship().getToClassID();
                String fromString = this.dataModel.getData().getClassByID(fromClassID).getName();
                String toString = this.dataModel.getData().getClassByID(toClassID).getName();
                String fromDesc = selectedItem.getRelationship().getFromDesc();
                String toDesc = selectedItem.getRelationship().getToDesc();
                String relName = selectedItem.getRelationship().getName();
                int ID = selectedItem.getRelationship().getID();

                if (fromDesc == null)
                    fromDesc = "<empty>";
                if (toDesc == null)
                    toDesc = "<empty>";
                if (relName == null)
                    relName = "<unnamed>";

                propertiesView.resetProperties();
                propertiesView.setGroupType(EPropertyType.RELATIONSHIP);
                propertiesView.setID(ID);
                propertiesView.addPropertyLine("Relationship", relName);
                propertiesView.addPropertyLine("From", fromString);
                propertiesView.addPropertyLine("To", toString);
                propertiesView.addPropertyLine("Type", relType);
                propertiesView.addPropertyLine("FromDesc", fromDesc);
                propertiesView.addPropertyLine("ToDesc", toDesc);
            } else {
                propertiesView.resetProperties();
            }
        } catch (Exception e) {
            this.showErrorMessage(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public void handleRemoveDiagram() {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Remove Class Diagram");
            alert.setContentText("This action will remove all classes and related sequence diagrams. Proceed?");
            alert.setHeaderText("Remove everything and start over");
            ButtonType okButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
            ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
            alert.getButtonTypes().setAll(okButton, noButton);
            alert.showAndWait().ifPresent(type -> {
                if (type.getButtonData() == ButtonBar.ButtonData.YES) {
                    try {
                        this.dataModel.executeCommand(new FileResetCommand());
                        this.updateView();
                    } catch (Exception e) {
                        this.showErrorMessage(e.getLocalizedMessage());
                        e.printStackTrace();
                    }
                }
            });
        } catch(Exception e){
            this.showErrorMessage(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }
}

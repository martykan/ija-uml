/*
 * File: ClassDiagramController.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

/*
 * File: MainController.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.view.classdiagram;

import cz.vutfit.umlapp.model.ModelFactory;
import cz.vutfit.umlapp.model.commands.*;
import cz.vutfit.umlapp.model.uml.*;
import cz.vutfit.umlapp.view.ViewHandler;
import cz.vutfit.umlapp.view.components.DraggableUMLClassView;
import cz.vutfit.umlapp.view.components.DraggableUMLRelationView;
import cz.vutfit.umlapp.view.components.EPropertyType;
import cz.vutfit.umlapp.view.components.PropertiesView;
import cz.vutfit.umlapp.view.main.EDataType;
import cz.vutfit.umlapp.view.main.MainController;
import cz.vutfit.umlapp.view.main.TreeViewItemModel;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.transform.Scale;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Class for controller of MainView
 */
public class ClassDiagramController extends MainController {
    @FXML
    public TreeView<String> classTreeView;
    @FXML
    public HBox boxClassOptions;
    /**
     * Area for buttons that's showed after user clicks on any item in classTreeView
     */
    @FXML
    public PropertiesView propertiesView;

    private String selectedClass;

    /**
     * Listens to changes in classTreeView.
     * Used for displaying properties of any element from classTreeView.
     *
     * @see #handleProperties(TreeItem)
     */
    ChangeListener<TreeItem<String>> handleClassSelection = (observableValue, oldItem, newItem) -> {
        if (newItem != null) {
            this.selectedClass = newItem.getValue();
            handleProperties(newItem);
        } else {
            this.selectedClass = null;
            propertiesView.resetProperties();
            try {
                propertiesView.addPropertyLine("Nothing selected", "");
            } catch (Exception e) {
                this.showErrorMessage(e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
        boxClassOptions.setVisible(this.selectedClass != null);
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
                if (node.getTranslateX() == classDiagram.positionX && node.getTranslateY() == classDiagram.positionY)
                    return;
                try {
                    this.dataModel.executeCommand(new DragClassCommand(classDiagram.getID(), node.getTranslateX(), node.getTranslateY()));
                } catch (Exception e) {
                    this.showErrorMessage(e.getLocalizedMessage());
                    e.printStackTrace();
                }
            });
            anchorScrollPane.getChildren().add(node);
            classNodes.put(classDiagram.getID(), node);
        }

        for (Relationships relationship : this.dataModel.getData().getRelationships()) {
            DraggableUMLClassView node = classNodes.get(relationship.fromId);
            DraggableUMLClassView node2 = classNodes.get(relationship.toId);
            DraggableUMLRelationView line = new DraggableUMLRelationView(node, node2, totalZoom, relationship);
            anchorScrollPane.getChildren().add(line);
        }
    }

    @Override
    public void init(ModelFactory modelFactory, ViewHandler viewHandler) {
        super.init(modelFactory, viewHandler);
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
            classes.showTreeItem();
            classes.rootViewUpdate();

            this.initDragDrop();

            boxClassOptions.setVisible(this.selectedClass != null);
        } catch (Exception e) {
            this.showErrorMessage(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    /**
     * Called after clicking on + button (next to Classes header in menu)
     *
     * @param actionEvent
     */
    public void handleAddClass(ActionEvent actionEvent) {
        try {
            TextInputDialog dialog = new TextInputDialog("");
            dialog.setTitle("New Class");
            dialog.setHeaderText(null);
            dialog.setContentText("New Class Name:");

            // disable OK button if text-input is empty
            BooleanBinding validName = Bindings.createBooleanBinding(() -> {
                if (dialog.getEditor().getText().equals("")) {
                    return true;
                } else {
                    return false;
                }
            }, dialog.getEditor().textProperty());
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
     * The button deletes anything that's selected in classTreeView - class, method, attribute or relationship (todo).
     *
     * @param actionEvent
     */
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
                ArrayList<Relationships> r = this.dataModel.getData().getRelationships();
                String currentOption = classTreeView.getSelectionModel().getSelectedItem().getValue();
                String[] x = currentOption.split("[+\\-#~]", 2);
                boolean isAM = false;
                if (x.length > 1) {
                    x = x[1].split("[(]", 2);
                    isAM = true;
                }
                if (x.length > 1 && x[1].equals(")")) {
                    for (Methods y : m) {
                        if (y.getName().equals(x[0])) {
                            this.dataModel.executeCommand(new RemoveClassMethodCommand(myclass, x[0], y.getVisibility()));
                            break;
                        }
                    }
                    this.updateView();
                } else if (isAM) {
                    for (Attributes z : a) {
                        if (z.getName().equals(x[0])) {
                            this.dataModel.executeCommand(new RemoveClassAttributeCommand(myclass, x[0], z.getVisibility()));
                            break;
                        }
                    }
                    this.updateView();
                } else {
                    String currSplit = currentOption.split("[>< ]", 3)[2];
                    if (currentOption.contains("><")) {
                        currSplit = currSplit.split("[ ]", 2)[1];
                        for (Relationships relation : r) {
                            ClassDiagram nameclass = this.dataModel.getData().getClassByName(currSplit);
                            if (nameclass == null)
                                return;
                            if (relation.getToClassID() == nameclass.getID() && relation.getFromClassID() == relation.getToClassID()) {
                                this.dataModel.executeCommand(new RemoveClassRelationshipCommand(relation.getID()));
                                break;
                            }
                        }
                    } else if (currentOption.contains(">")) {
                        for (Relationships relation : r) {
                            if (relation.getToClassID() == this.dataModel.getData().getClassByName(currSplit).getID() && relation.getFromClassID() == this.dataModel.getData().getClassByName(id).getID()) {
                                this.dataModel.executeCommand(new RemoveClassRelationshipCommand(relation.getID()));
                                break;
                            }
                        }
                    } else if (currentOption.contains("<")) {
                        for (Relationships relation : r) {
                            if (relation.getFromClassID() == this.dataModel.getData().getClassByName(currSplit).getID() && relation.getToClassID() == this.dataModel.getData().getClassByName(id).getID()) {
                                this.dataModel.executeCommand(new RemoveClassRelationshipCommand(relation.getID()));
                                break;
                            }
                        }
                    }
                    this.updateView();
                }
            } else {
                this.dataModel.executeCommand(new RemoveClassCommand(myclass.getID()));
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
     *
     * @param actionEvent
     */
    public void handleAddClassMethod(ActionEvent actionEvent) {
        try {
            TextInputDialog dialog = new TextInputDialog("");
            dialog.setTitle("New Class Method");
            dialog.setHeaderText(null);
            dialog.setContentText("New Class Method Name:");

            // disable OK button if text-input is empty
            BooleanBinding validName = Bindings.createBooleanBinding(() -> {
                if (dialog.getEditor().getText().equals("")) {
                    return true;
                } else {
                    return false;
                }
            }, dialog.getEditor().textProperty());
            dialog.getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(validName);

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(className -> {
                try {
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
     * @param actionEvent
     * @see #handleAddClassMethod(ActionEvent)
     */
    public void handleAddAttribute(ActionEvent actionEvent) {
        try {
            TextInputDialog dialog = new TextInputDialog("");
            dialog.setTitle("New Class Attribute");
            dialog.setHeaderText(null);
            dialog.setContentText("New Class Attribute Name:");

            // disable OK button if text-input is empty
            BooleanBinding validName = Bindings.createBooleanBinding(() -> {
                if (dialog.getEditor().getText().equals("")) {
                    return true;
                } else {
                    return false;
                }
            }, dialog.getEditor().textProperty());
            dialog.getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(validName);

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(className -> {
                try {
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
     * @param actionEvent
     * @see #handleAddClassMethod(ActionEvent)
     */
    public void handleAddRelation(ActionEvent actionEvent) {
        try {
            String id;
            if (classTreeView.getSelectionModel().getSelectedItem() != null) { // no item selected / 0 items in tree-view
                id = classTreeView.getSelectionModel().getSelectedItem().getValue();
            } else {
                return;
            }
            ClassDiagram myclass = this.dataModel.getData().getClassByName(id);
            if (myclass == null) // if not selected class but some attribute/method/relationship in that class
                myclass = this.dataModel.getData().getClassByName(classTreeView.getSelectionModel().getSelectedItem().getParent().getValue());
            String fromName = myclass.getName();

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
            if (fromName != null) {
                fromClassBox.getSelectionModel().select(fromName);
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
                    ArrayList<String> x = new ArrayList<String>();
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
                    ERelationType relType = this.dataModel.getData().stringToRelation(returned.get(2));
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
     * Shows item-element properties (in Properties section in menu) depending on its type - class, method, attribute or relationship (todo).
     * If nothing is selected, simply shows 'nothing selected'.
     *
     * @param selected currently selected item in classTreeView
     * @see #handleClassSelection
     */
    public void handleProperties(TreeItem<String> selected) {
        try {
            propertiesView.setDataModel(this.dataModel);
            propertiesView.setClassTreeView(this.classTreeView);
            String id = classTreeView.getSelectionModel().getSelectedItem().getValue();
            ClassDiagram myclass = this.dataModel.getData().getClassByName(id);
            if (myclass == null) { // currently not selected class
                id = classTreeView.getSelectionModel().getSelectedItem().getParent().getValue();
                myclass = this.dataModel.getData().getClassByName(id);
                String currentOption = classTreeView.getSelectionModel().getSelectedItem().getValue();
                String[] x = currentOption.split("[+\\-#~]", 2);
                boolean isAM = false;
                if (x.length > 1) {
                    x = x[1].split("[(]", 2);
                    isAM = true;
                }
                if (x.length > 1 && x[1].equals(")")) {
                    propertiesView.resetProperties();
                    propertiesView.setGroupType(EPropertyType.METHOD);
                    propertiesView.setID(x[0]);
                    propertiesView.addPropertyLine("Method", x[0]);
                    propertiesView.addPropertyLine("Class", String.valueOf(myclass.getName()));
                    propertiesView.addPropertyLine("Visibility", myclass.getMethod(x[0]).getVisibility().getVisiblityString());
                } else if (isAM) {
                    propertiesView.resetProperties();
                    propertiesView.setGroupType(EPropertyType.ATTRIBUTE);
                    propertiesView.setID(x[0]);
                    propertiesView.addPropertyLine("Attribute", x[0]);
                    propertiesView.addPropertyLine("Class", String.valueOf(myclass.getName()));
                    propertiesView.addPropertyLine("Visibility", myclass.getAttribute(x[0]).getVisibility().getVisiblityString());
                } else {
                    String relType = "<empty>";
                    String fromString = "<empty>";
                    String toString = "<empty>";
                    String fromDesc = null;
                    String toDesc = null;
                    String relName = "<empty>";
                    Integer ID = 0;
                    String splitString = currentOption.split("[<>]", 2)[1];
                    if (currentOption.contains("><")) {
                        fromString = String.valueOf(myclass.getName());
                        toString = fromString;
                        for (Relationships y : this.dataModel.getData().getRelationships()) {
                            if (toString.equals(this.dataModel.getData().getClassByID(y.getFromClassID()).getName())) {
                                relType = y.getType().relationToString();
                                fromDesc = y.getFromDesc();
                                toDesc = y.getToDesc();
                                ID = y.getID();
                                relName = y.getName();
                                break;
                            }
                        }
                    } else if (currentOption.contains(">")) {
                        fromString = String.valueOf(myclass.getName());
                        toString = splitString;
                        for (Relationships y : this.dataModel.getData().getRelationships()) {
                            if (fromString.equals(this.dataModel.getData().getClassByID(y.getFromClassID()).getName())) {
                                relType = y.getType().relationToString();
                                fromDesc = y.getFromDesc();
                                toDesc = y.getToDesc();
                                ID = y.getID();
                                relName = y.getName();
                                break;
                            }
                        }
                    } else if (currentOption.contains("<")) {
                        toString = String.valueOf(myclass.getName());
                        fromString = splitString;
                        for (Relationships y : this.dataModel.getData().getRelationships()) {
                            if (toString.equals(this.dataModel.getData().getClassByID(y.getFromClassID()).getName())) {
                                relType = y.getType().relationToString();
                                fromDesc = y.getFromDesc();
                                toDesc = y.getToDesc();
                                ID = y.getID();
                                relName = y.getName();
                                break;
                            }
                        }
                    }

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
                }
            } else {
                propertiesView.resetProperties();
                propertiesView.setGroupType(EPropertyType.CLASS);
                propertiesView.setID(myclass.getID());
                propertiesView.addPropertyLine("Class", myclass.getName());
                propertiesView.addPropertyLine("Attributes", String.valueOf(myclass.getAttribs().size()));
                propertiesView.addPropertyLine("Methods", String.valueOf(myclass.getMethods().size()));
                propertiesView.addPropertyLine("Linked seq. diagrams", String.valueOf(myclass.getSeqdigs().size()));
            }
        } catch (Exception e) {
            this.showErrorMessage(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public void handleRemoveDiagram(ActionEvent actionEvent) {
        try {
            String id;
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
                        this.dataModel.executeCommand(new FileResetCommand(this.dataModel.getData()));
                        this.updateView();
                    } catch (Exception e) {
                        this.showErrorMessage(e.getLocalizedMessage());
                        e.printStackTrace();
                    }
                } else if (type.getButtonData() == ButtonBar.ButtonData.NO) {
                    return;
                }
            });
        } catch(Exception e){
            this.showErrorMessage(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }
}
/*
 * File: MainController.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.view.main;

import cz.vutfit.umlapp.model.DataModel;
import cz.vutfit.umlapp.model.ModelFactory;
import cz.vutfit.umlapp.model.commands.*;
import cz.vutfit.umlapp.model.uml.*;
import cz.vutfit.umlapp.view.IController;
import cz.vutfit.umlapp.view.ViewHandler;
import cz.vutfit.umlapp.view.components.DraggableUMLClassView;
import cz.vutfit.umlapp.view.components.DraggableUMLRelationView;
import cz.vutfit.umlapp.view.components.PropertiesView;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

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
    public TreeView<String> classTreeView;
    @FXML
    public TreeView<String> diagramTreeView;
    @FXML
    public HBox boxClassOptions; /** Area for buttons that's showed after user clicks on any item in classTreeView */
    @FXML
    public ScrollPane scrollPane;
    @FXML
    public Pane anchorScrollPane;
    @FXML
    public PropertiesView propertiesView;

    private DataModel dataModel;
    private ViewHandler viewHandler;

    private String selectedClass;

    /**
     * Listens to changes in classTreeView.
     * Used for displaying properties of any element from classTreeView.
     * @see #handleProperties(TreeItem)
     */
    ChangeListener<TreeItem<String>> handleClassSelection = (observableValue, oldItem, newItem) -> {
        if (newItem != null) {
            this.selectedClass = newItem.getValue();
            handleProperties(newItem);
        } else {
            this.selectedClass = null;
            propertiesView.resetProperties();
            propertiesView.addPropertyLine("Nothing selected", "");
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
            this.showErrorMessage(e.getLocalizedMessage());
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
                    this.showErrorMessage(e.getLocalizedMessage());
                    e.printStackTrace();
                }
            });
            anchorScrollPane.getChildren().add(node);
        }

        // Relation testing - lines between all classes
        List<Node> children = new ArrayList<>(anchorScrollPane.getChildren().filtered(it -> it instanceof DraggableUMLClassView));
        for (int i = 0; i < children.size(); i++) {
            DraggableUMLClassView node = (DraggableUMLClassView) children.get(i);
            for (int j = i + 1; j < children.size(); j++) {
                DraggableUMLClassView node2 = (DraggableUMLClassView) children.get(j);
                DraggableUMLRelationView line = new DraggableUMLRelationView(node, node2, totalZoom);
                anchorScrollPane.getChildren().add(line);
                System.out.println("DraggableUMLRelationView " + i + " " + j);
            }
        }
    }

    /**
     * After clicking on save icon, this function is called. It saves changes user made into file opened.
     * @param actionEvent
     */
    public void handleSave(ActionEvent actionEvent) {
        try {
            this.dataModel.saveFile();
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
            this.viewHandler.openView("Welcome");
        } catch (IOException e) {
            this.showErrorMessage(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void init(ModelFactory modelFactory, ViewHandler viewHandler) {
        this.dataModel = modelFactory.getDataModel();
        this.viewHandler = viewHandler;
        this.classTreeView.getSelectionModel().selectedItemProperty().addListener(handleClassSelection);
        propertiesView.addPropertyLine("Nothing selected", "");
        this.updateView();

        Platform.runLater(MainController.this::initKeyboardShortcuts);
    }

    /**
     * Updates entire View (UI).
     */
    private void updateView() {
        try {
            viewHandler.setTitle("IJA UML App - " + this.dataModel.getFileName());

            // Diagrams menu
            TreeViewItemModel diagrams = new TreeViewItemModel(this.dataModel, diagramTreeView, EDataType.CLASS_DIAGRAM);
            diagrams.showTreeItem();
            diagrams.rootViewUpdate();

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
     * After clicking on undo button, this function is called.
     * @param actionEvent
     */
    public void handleUndo(ActionEvent actionEvent) {
        try {
            this.dataModel.undo();
            this.updateView();
        } catch (Exception e) {
            this.showErrorMessage(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    /**
     * Called after clicking on + button (next to Classes header in menu)
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
                    this.showErrorMessage(e.getLocalizedMessage());
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
                } else if (isAM){
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
     * After clicking on + button (next to Diagrams in menu), this function is called.
     * It is supposed to add new sequence diagram. Currently TODO.
     * @param actionEvent
     */
    public void handleAddDiagram(ActionEvent actionEvent) {
        return;
    }

    /**
     * In boxClassOptions after clicking on New Class button, this function is called.
     * Add new method to class.
     * If selected item in classTreeView is class, method will be added to this class.
     * If selected item is method, attribute or relationship, new method will be added to the same class as the selected item.
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
                    this.showErrorMessage(e.getLocalizedMessage());
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
     * @see #handleAddClassMethod(ActionEvent)
     * @param actionEvent
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
                    this.showErrorMessage(e.getLocalizedMessage());
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
                    System.out.println("New relationship: " + "From=" + returned.get(0) + ", To=" + returned.get(1) + ", Type=" + returned.get(2));
                    Integer idFrom = this.dataModel.getData().getClassByName(returned.get(0)).getID();
                    Integer idTo = this.dataModel.getData().getClassByName(returned.get(1)).getID();
                    ERelationType relType = this.dataModel.getData().stringToRelation(returned.get(2));
                    this.dataModel.executeCommand(new AddClassRelationshipCommand(idFrom, idTo, relType));
                    this.updateView();
                } catch (Exception e) {
                    this.showErrorMessage(e.getLocalizedMessage());
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
     * @param selected currently selected item in classTreeView
     * @see #handleClassSelection
     */
    public void handleProperties(TreeItem<String> selected) {
        try {
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
                    propertiesView.addPropertyLine("Method", x[0]);
                    propertiesView.addPropertyLine("Class", String.valueOf(myclass.getName()));
                    propertiesView.addPropertyLine("Visibility", myclass.getMethod(x[0]).getVisibility().getVisiblityString());
                } else if (isAM) {
                    propertiesView.resetProperties();
                    propertiesView.addPropertyLine("Attribute", x[0]);
                    propertiesView.addPropertyLine("Class", String.valueOf(myclass.getName()));
                    propertiesView.addPropertyLine("Visibility", myclass.getAttribute(x[0]).getVisibility().getVisiblityString());
                } else {
                    String relType = "<none>";
                    String fromString = "<none>";
                    String toString = "<none>";
                    String splitString = currentOption.split("[<>]", 2)[1];
                    if (currentOption.contains("><")) {
                        fromString = String.valueOf(myclass.getName());
                        toString = fromString;
                        for (Relationships y : this.dataModel.getData().getRelationships()) {
                            if (toString.equals(this.dataModel.getData().getClassByID(y.getFromClassID()).getName())) {
                                relType = y.getType().relationToString();
                                break;
                            }
                        }
                    } else if (currentOption.contains(">")) {
                        fromString = String.valueOf(myclass.getName());
                        toString = splitString;
                        for (Relationships y : this.dataModel.getData().getRelationships()) {
                            if (fromString.equals(this.dataModel.getData().getClassByID(y.getFromClassID()).getName())) {
                                relType = y.getType().relationToString();
                                break;
                            }
                        }
                    } else if (currentOption.contains("<")) {
                        toString = String.valueOf(myclass.getName());
                        fromString = splitString;
                        for (Relationships y : this.dataModel.getData().getRelationships()) {
                            if (toString.equals(this.dataModel.getData().getClassByID(y.getFromClassID()).getName())) {
                                relType = y.getType().relationToString();
                                break;
                            }
                        }
                    }

                    propertiesView.resetProperties();
                    propertiesView.addPropertyLine("Relationship", " ");
                    propertiesView.addPropertyLine("From", fromString);
                    propertiesView.addPropertyLine("To", toString);
                    propertiesView.addPropertyLine("Type", relType);
                }
            } else {
                propertiesView.resetProperties();
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

    /**
     * After clicking on camera button in menu, this function is called.
     * Outputs entire diagram into PNG image.
     * @param actionEvent
     */
    public void handleSnapshot(ActionEvent actionEvent) {
        // Choose a file for output
        FileChooser fileChooser = new FileChooser();
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
                this.showErrorMessage(e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Exception");
        alert.setContentText(message);
        alert.showAndWait();
    }
}

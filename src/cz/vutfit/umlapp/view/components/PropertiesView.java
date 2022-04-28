/*
 * File: PropertiesView.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.view.components;

import cz.vutfit.umlapp.model.DataModel;
import cz.vutfit.umlapp.model.commands.EditClassAttributeVisibilityCommand;
import cz.vutfit.umlapp.model.commands.EditClassNameCommand;
import cz.vutfit.umlapp.model.commands.EditClassAttributeNameCommand;
import cz.vutfit.umlapp.model.commands.EditClassMethodNameCommand;
import cz.vutfit.umlapp.model.commands.EditClassMethodVisibilityCommand;
import cz.vutfit.umlapp.model.commands.EditClassRelationshipToDescCommand;
import cz.vutfit.umlapp.model.commands.EditClassRelationshipFromDescCommand;
import cz.vutfit.umlapp.model.commands.EditClassRelationshipFromToCommand;
import cz.vutfit.umlapp.model.commands.EditClassRelationshipTypeCommand;
import cz.vutfit.umlapp.model.uml.ClassDiagram;
import cz.vutfit.umlapp.model.uml.EAttribVisibility;
import cz.vutfit.umlapp.model.uml.ERelationType;
import cz.vutfit.umlapp.model.uml.Relationships;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.scene.input.MouseButton;
import cz.vutfit.umlapp.view.components.EPropertyType;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Class for properties view (Properties section in menu)
 */
public class PropertiesView extends VBox {
<<<<<<< HEAD
    public EPropertyType groupType = EPropertyType.EMPTY;
    public DataModel dataModel;
    public TreeView<String> classTreeView;
    public String stringID;
    public int intID;

    private UpdatedCallback updatedCallback;

    /**
     * Set callback on updated
     *
     * @param updatedCallback callback
     */
    public void setOnUpdated(UpdatedCallback updatedCallback) {
        this.updatedCallback = updatedCallback;
    }
=======
    private UpdatedCallback updatedCallback;

    /**
     * Set callback on updated
     *
     * @param updatedCallback callback
     */
    public void setOnUpdated(UpdatedCallback updatedCallback) {
        this.updatedCallback = updatedCallback;
    }
>>>>>>> 80443efb81e7ded0ba7e9c114e4310c40570f41a

    /**
     * Adds one line to the properties section.
     *
     * @param property name of property, data description
     * @param text     data-value (in String)
     */
    public void addPropertyLine(String property, String text) throws Exception {
        BorderPane line = new BorderPane();
        Label property_lab = new Label(property);
        Label text_lab = new Label(text);

        line.getStyleClass().add("box-property-line");
        bindPropertyActions(line, property_lab, text_lab);

        property_lab.getStyleClass().add("property-name");
        property_lab.setAlignment(Pos.TOP_LEFT);
        property_lab.setTextAlignment(TextAlignment.LEFT);
        text_lab.setAlignment(Pos.TOP_RIGHT);
        text_lab.setTextAlignment(TextAlignment.RIGHT);

        line.setLeft(property_lab);
        line.setRight(text_lab);
        this.getChildren().add(line);
    }

    /**
     * Clears properties section
     */
    public void resetProperties() {
        this.getChildren().clear();
        this.groupType = EPropertyType.EMPTY;
    }

<<<<<<< HEAD
    public void setGroupType(EPropertyType type) {
        this.groupType = type;
    }

    public void setDataModel(DataModel model) {
        this.dataModel = model;
    }

    public void setClassTreeView(TreeView<String> x) {
        this.classTreeView = x;
    }

    public void setID(int ID) {
        this.intID = ID;
    }

    public void setID(String ID) {
        this.stringID = ID;
    }

    private void bindPropertyActions(BorderPane line, Label prop, Label text) throws Exception {
        switch (this.groupType) {
            case CLASS:
                bindClassActions(prop, text, line);
                break;
            case ATTRIBUTE:
                bindAttributeActions(prop, text, line);
                break;
            case METHOD:
                bindMethodActions(prop, text, line);
                break;
            case RELATIONSHIP:
                bindRelationshipActions(prop, text, line);
                break;
            case SEQUENCE_DIAGRAM:
                bindSequenceActions(prop, text, line);
            case EMPTY:
                System.out.println("Warning: Executed bindPropertyActions on groupType EMPTY");
                break;
        }
    }

    private void bindClassActions(Label prop, Label text, BorderPane line) {
        System.out.println(text.getText());
        if (prop.getText().equals("Class")) {
            line.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                if (e.getClickCount() == 2) {
                    try {
                        TextInputDialog dialog = new TextInputDialog("");
                        dialog.setTitle("New Class Name");
                        dialog.setHeaderText(null);
                        dialog.setContentText("New Name:");

                        // disable OK button if text-input is empty or name is same as old
                        BooleanBinding validName = Bindings.createBooleanBinding(() -> {
                            if (dialog.getEditor().getText().equals("") || dialog.getEditor().getText().equals(text.getText())) {
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
                                this.dataModel.executeCommand(new EditClassNameCommand(myclass.getID(), className));
                                //this.updateView();
                            } catch (Exception ex) {
                                //this.showErrorMessage("Unable to set new class name", e.getLocalizedMessage());
                                ex.printStackTrace();
                            }
                        });
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        } else if (prop.getText().equals("Attributes")) {   // TODO: not sure what to do
            line.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                        if (mouseEvent.getClickCount() == 2) {
                            System.out.println("Double clicked on Attributes");
                        }
                    }
                }
            });
        } else if (prop.getText().equals("Methods")) { // TODO: not sure what to do
            line.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                        if (mouseEvent.getClickCount() == 2) {
                            System.out.println("Double clicked on Methods");
                        }
                    }
                }
            });
        } else if (prop.getText().equals("Linked seq. diagrams")) { // TODO: not sure what to do
            line.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                        if (mouseEvent.getClickCount() == 2) {
                            System.out.println("Double click on linked sequence diagrams");
                        }
                    }
                }
            });
        } else {
            System.out.println("Warning: bindClassActions did not recognize following propertyText: " + prop.getText());
        }
    }

    private void bindAttributeActions(Label prop, Label text, BorderPane line) {
        if (prop.getText().equals("Attribute")) {
            line.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                if (e.getClickCount() == 2) {
                    try {
                        TextInputDialog dialog = new TextInputDialog("");
                        dialog.setTitle("New Attribute Name");
                        dialog.setHeaderText(null);
                        dialog.setContentText("New Name:");

                        // disable OK button if text-input is empty or name is same as old
                        BooleanBinding validName = Bindings.createBooleanBinding(() -> {
                            if (dialog.getEditor().getText().equals("") || dialog.getEditor().getText().equals(text.getText())) {
                                return true;
                            } else {
                                return false;
                            }
                        }, dialog.getEditor().textProperty());
                        dialog.getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(validName);

                        Optional<String> result = dialog.showAndWait();
                        result.ifPresent(attribName -> {
                            try {
                                String id;
                                if (classTreeView.getSelectionModel().getSelectedItem() != null) { // no item selected / 0 items in tree-view
                                    id = classTreeView.getSelectionModel().getSelectedItem().getParent().getValue();
                                } else {
                                    return;
                                }
                                ClassDiagram myclass = this.dataModel.getData().getClassByName(id);
                                String oldAttribName = text.getText();
                                this.dataModel.executeCommand(new EditClassAttributeNameCommand(myclass.getID(), oldAttribName, attribName));
                                //this.updateView();
                            } catch (Exception ex) {
                                //this.showErrorMessage("Unable to set new attribute name", e.getLocalizedMessage());
                                ex.printStackTrace();
                            }
                        });
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        } else if (prop.getText().equals("Class")) {
            line.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                if (e.getClickCount() == 2) {
                    try {
                        TextInputDialog dialog = new TextInputDialog("");
                        dialog.setTitle("New Class Name");
                        dialog.setHeaderText(null);
                        dialog.setContentText("New Name:");

                        // disable OK button if text-input is empty or name is same as old
                        BooleanBinding validName = Bindings.createBooleanBinding(() -> {
                            if (dialog.getEditor().getText().equals("") || dialog.getEditor().getText().equals(text.getText())) {
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
                                    id = classTreeView.getSelectionModel().getSelectedItem().getParent().getValue();
                                } else {
                                    return;
                                }
                                ClassDiagram myclass = this.dataModel.getData().getClassByName(id);
                                this.dataModel.executeCommand(new EditClassNameCommand(myclass.getID(), className));
                                //this.updateView();
                            } catch (Exception ex) {
                                //this.showErrorMessage("Unable to set new attribute class name", e.getLocalizedMessage());
                                ex.printStackTrace();
                            }
                        });
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        } else if (prop.getText().equals("Visibility")) {
            line.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                if (e.getClickCount() == 2) {
                    try {
                        // Create the custom dialog.
                        Dialog<EAttribVisibility> dialog = new Dialog<>();
                        dialog.setTitle("Attribute visibility");
                        dialog.setHeaderText("Change current visibility of this attribute");

                        ButtonType createButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

                        GridPane grid = new GridPane();
                        grid.setHgap(10);
                        grid.setVgap(10);
                        grid.setPadding(new Insets(20, 150, 10, 10));

                        Label currVis = new Label(text.getText());

                        ChoiceBox<String> visBox = new ChoiceBox<>();
                        for (EAttribVisibility x : EAttribVisibility.values()) {
                            visBox.getItems().add(x.getVisiblityString());
                        }
                        visBox.getSelectionModel().selectFirst();

                        grid.add(new Label("Current visibility: "), 0, 0);
                        grid.add(currVis, 1, 0);
                        grid.add(new Label("New visibility: "), 0, 1);
                        grid.add(visBox, 1, 1);
                        dialog.getDialogPane().setContent(grid);

                        dialog.setResultConverter(dialogButton -> {
                            if (dialogButton == createButtonType) {
                                String x = visBox.getSelectionModel().getSelectedItem();
                                switch (x) {
                                    case "Public":
                                        return EAttribVisibility.PUBLIC;
                                    case "Private":
                                        return EAttribVisibility.PRIVATE;
                                    case "Protected":
                                        return EAttribVisibility.PROTECTED;
                                    case "Package":
                                        return EAttribVisibility.PACKAGE;
                                }
                            }
                            return null;
                        });

                        Optional<EAttribVisibility> result = dialog.showAndWait();
                        result.ifPresent(newVisibility -> {
                            try {
                                String id;
                                if (classTreeView.getSelectionModel().getSelectedItem() != null) { // no item selected / 0 items in tree-view
                                    id = classTreeView.getSelectionModel().getSelectedItem().getParent().getValue();
                                } else {
                                    return;
                                }
                                ClassDiagram myclass = this.dataModel.getData().getClassByName(id);
                                String attribName = this.stringID;
                                System.out.println(attribName);
                                this.dataModel.executeCommand(new EditClassAttributeVisibilityCommand(myclass.getID(), attribName, newVisibility));
                                //this.updateView();
                            } catch (Exception ex) {
                                //this.showErrorMessage("Unable to set new attribute visibility", e.getLocalizedMessage());
                                ex.printStackTrace();
                            }
                        });
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        } else {
            System.out.println("Warning: bindAttributeActions did not recognize following propertyText: " + prop.getText());
        }
    }

    private void bindMethodActions(Label prop, Label text, BorderPane line) {
        if (prop.getText().equals("Method")) {
            line.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                if (e.getClickCount() == 2) {
                    try {
                        TextInputDialog dialog = new TextInputDialog("");
                        dialog.setTitle("New Method Name");
                        dialog.setHeaderText(null);
                        dialog.setContentText("New Name:");

                        // disable OK button if text-input is empty or name is same as old
                        BooleanBinding validName = Bindings.createBooleanBinding(() -> {
                            if (dialog.getEditor().getText().equals("") || dialog.getEditor().getText().equals(text.getText())) {
                                return true;
                            } else {
                                return false;
                            }
                        }, dialog.getEditor().textProperty());
                        dialog.getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(validName);

                        Optional<String> result = dialog.showAndWait();
                        result.ifPresent(attribName -> {
                            try {
                                String id;
                                if (classTreeView.getSelectionModel().getSelectedItem() != null) { // no item selected / 0 items in tree-view
                                    id = classTreeView.getSelectionModel().getSelectedItem().getParent().getValue();
                                } else {
                                    return;
                                }
                                ClassDiagram myclass = this.dataModel.getData().getClassByName(id);
                                String oldAttribName = this.stringID;
                                this.dataModel.executeCommand(new EditClassMethodNameCommand(myclass.getID(), oldAttribName, attribName));
                                //this.updateView();
                            } catch (Exception ex) {
                                //this.showErrorMessage("Unable to set new method name", e.getLocalizedMessage());
                                ex.printStackTrace();
                            }
                        });
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        } else if (prop.getText().equals("Class")) {
            line.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                if (e.getClickCount() == 2) {
                    try {
                        TextInputDialog dialog = new TextInputDialog("");
                        dialog.setTitle("New Class Name");
                        dialog.setHeaderText(null);
                        dialog.setContentText("New Name:");

                        // disable OK button if text-input is empty or name is same as old
                        BooleanBinding validName = Bindings.createBooleanBinding(() -> {
                            if (dialog.getEditor().getText().equals("") || dialog.getEditor().getText().equals(text.getText())) {
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
                                    id = classTreeView.getSelectionModel().getSelectedItem().getParent().getValue();
                                } else {
                                    return;
                                }
                                ClassDiagram myclass = this.dataModel.getData().getClassByName(id);
                                this.dataModel.executeCommand(new EditClassNameCommand(myclass.getID(), className));
                                //this.updateView();
                            } catch (Exception ex) {
                                //this.showErrorMessage("Unable to set new method class name", e.getLocalizedMessage());
                                ex.printStackTrace();
                            }
                        });
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        } else if (prop.getText().equals("Visibility")) {
            line.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                if (e.getClickCount() == 2) {
                    try {
                        // Create the custom dialog.
                        Dialog<EAttribVisibility> dialog = new Dialog<>();
                        dialog.setTitle("Method visibility");
                        dialog.setHeaderText("Change current visibility of this method");

                        ButtonType createButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

                        GridPane grid = new GridPane();
                        grid.setHgap(10);
                        grid.setVgap(10);
                        grid.setPadding(new Insets(20, 150, 10, 10));

                        Label currVis = new Label(text.getText());

                        ChoiceBox<String> visBox = new ChoiceBox<>();
                        for (EAttribVisibility x : EAttribVisibility.values()) {
                            visBox.getItems().add(x.getVisiblityString());
                        }
                        visBox.getSelectionModel().selectFirst();

                        grid.add(new Label("Current visibility: "), 0, 0);
                        grid.add(currVis, 1, 0);
                        grid.add(new Label("New visibility: "), 0, 1);
                        grid.add(visBox, 1, 1);
                        dialog.getDialogPane().setContent(grid);

                        dialog.setResultConverter(dialogButton -> {
                            if (dialogButton == createButtonType) {
                                String x = visBox.getSelectionModel().getSelectedItem();
                                switch (x) {
                                    case "Public":
                                        return EAttribVisibility.PUBLIC;
                                    case "Private":
                                        return EAttribVisibility.PRIVATE;
                                    case "Protected":
                                        return EAttribVisibility.PROTECTED;
                                    case "Package":
                                        return EAttribVisibility.PACKAGE;
                                }
                            }
                            return null;
                        });

                        Optional<EAttribVisibility> result = dialog.showAndWait();
                        result.ifPresent(newVisibility -> {
                            try {
                                String id;
                                if (classTreeView.getSelectionModel().getSelectedItem() != null) { // no item selected / 0 items in tree-view
                                    id = classTreeView.getSelectionModel().getSelectedItem().getParent().getValue();
                                } else {
                                    return;
                                }
                                ClassDiagram myclass = this.dataModel.getData().getClassByName(id);
                                String attribName = this.stringID;
                                this.dataModel.executeCommand(new EditClassMethodVisibilityCommand(myclass.getID(), attribName, newVisibility));
                                //this.updateView();
                            } catch (Exception ex) {
                                //this.showErrorMessage("Unable to set new method visibility", e.getLocalizedMessage());
                                ex.printStackTrace();
                            }
                        });
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        } else {
            System.out.println("Warning: bindMethodActions did not recognize following propertyText: " + prop.getText());
        }
    }

    private void bindRelationshipActions(Label prop, Label text, BorderPane line) {
        if (prop.getText().equals("Relationship")) {
            return; // no action
        } else if (prop.getText().equals("From") || (prop.getText().equals("To"))) {
            line.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                if (e.getClickCount() == 2) {
                    try {
                        // Create the custom dialog.
                        Dialog<ArrayList<String>> dialog = new Dialog<>();
                        dialog.setTitle("Relationship path");
                        dialog.setHeaderText("Change path of this relationship.\nYou can change class, where this relationship starts or ends.");

                        ButtonType createButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

                        GridPane grid = new GridPane();
                        grid.setHgap(10);
                        grid.setVgap(10);
                        grid.setPadding(new Insets(20, 150, 10, 10));

                        Relationships thisRelation = this.dataModel.getData().getRelationByID(this.intID);
                        ClassDiagram fromClass = this.dataModel.getData().getClassByID(thisRelation.getFromClassID());
                        ClassDiagram toClass = this.dataModel.getData().getClassByID(thisRelation.getToClassID());
                        Label current = new Label(fromClass.getName() + " -> " + toClass.getName());

                        ChoiceBox<String> fromClassBox = new ChoiceBox<>();
                        ChoiceBox<String> toClassBox = new ChoiceBox<>();

                        for (ClassDiagram c : this.dataModel.getData().getClasses()) {
                            fromClassBox.getItems().add(c.getName());
                            toClassBox.getItems().add(c.getName());
                        }
                        fromClassBox.getSelectionModel().select(fromClass.getName());
                        toClassBox.getSelectionModel().select(toClass.getName());

                        grid.add(new Label("Current path: "), 0, 0);
                        grid.add(current, 1, 0);
                        grid.add(new Label("From: "), 0, 1);
                        grid.add(fromClassBox, 1, 1);
                        grid.add(new Label("To: "), 0, 2);
                        grid.add(toClassBox, 1, 2);
                        dialog.getDialogPane().setContent(grid);

                        dialog.setResultConverter(dialogButton -> {
                            if (dialogButton == createButtonType) {
                                ArrayList<String> x = new ArrayList<>();
                                x.add(fromClassBox.getSelectionModel().getSelectedItem());
                                x.add(toClassBox.getSelectionModel().getSelectedItem());
                                return x;
                            }
                            return null;
                        });

                        Optional<ArrayList<String>> result = dialog.showAndWait();
                        result.ifPresent(data -> {
                            try {
                                int fromID = this.dataModel.getData().getClassByName(data.get(0)).getID();
                                int toID = this.dataModel.getData().getClassByName(data.get(1)).getID();
                                this.dataModel.executeCommand(new EditClassRelationshipFromToCommand(this.intID, fromID, toID));
                                //this.updateView();
                            } catch (Exception ex) {
                                //this.showErrorMessage("Unable to set new method visibility", e.getLocalizedMessage());
                                ex.printStackTrace();
                            }
                        });
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        } else if (prop.getText().equals("Type")) {
            line.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                if (e.getClickCount() == 2) {
                    try {
                        // Create the custom dialog.
                        Dialog<ERelationType> dialog = new Dialog<>();
                        dialog.setTitle("Relationship type");
                        dialog.setHeaderText("Change current type of this relationship");

                        ButtonType createButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

                        GridPane grid = new GridPane();
                        grid.setHgap(10);
                        grid.setVgap(10);
                        grid.setPadding(new Insets(20, 150, 10, 10));

                        Label currVis = new Label(text.getText());

                        ChoiceBox<String> visBox = new ChoiceBox<>();
                        for (ERelationType x : ERelationType.values()) {
                            visBox.getItems().add(x.relationToString());
                        }
                        visBox.getSelectionModel().selectFirst();

                        grid.add(new Label("Current type: "), 0, 0);
                        grid.add(currVis, 1, 0);
                        grid.add(new Label("New type: "), 0, 1);
                        grid.add(visBox, 1, 1);
                        dialog.getDialogPane().setContent(grid);

                        dialog.setResultConverter(dialogButton -> {
                            if (dialogButton == createButtonType) {
                                String x = visBox.getSelectionModel().getSelectedItem();
                                return this.dataModel.getData().stringToRelation(x);
                            }
                            return null;
                        });

                        Optional<ERelationType> result = dialog.showAndWait();
                        result.ifPresent(newType -> {
                            try {
                                this.dataModel.executeCommand(new EditClassRelationshipTypeCommand(this.intID, newType));
                                //this.updateView();
                            } catch (Exception ex) {
                                //this.showErrorMessage("Unable to set new method visibility", e.getLocalizedMessage());
                                ex.printStackTrace();
                            }
                        });
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        } else if (prop.getText().equals("FromDesc")) {
            line.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                if (e.getClickCount() == 2) {
                    try {
                        TextInputDialog dialog = new TextInputDialog("");
                        dialog.setTitle("Relationship description");
                        dialog.setHeaderText("Change description of relationship in its beginning.\nOld description: " + text.getText());
                        dialog.setContentText("New description:");

                        // disable OK button if text-input is same as old
                        BooleanBinding validName = Bindings.createBooleanBinding(() -> {
                            if (dialog.getEditor().getText().equals(text.getText())) {
                                return true;
                            } else {
                                return false;
                            }
                        }, dialog.getEditor().textProperty());
                        dialog.getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(validName);

                        Optional<String> result = dialog.showAndWait();
                        result.ifPresent(newString -> {
                            try {
                                this.dataModel.executeCommand(new EditClassRelationshipFromDescCommand(this.intID, newString));
                                //this.updateView();
                            } catch (Exception ex) {
                                //this.showErrorMessage("Unable to set new attribute class name", e.getLocalizedMessage());
                                ex.printStackTrace();
                            }
                        });
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        } else if (prop.getText().equals("ToDesc")) {
            line.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                if (e.getClickCount() == 2) {
                    try {
                        TextInputDialog dialog = new TextInputDialog("");
                        dialog.setTitle("Relationship description");
                        dialog.setHeaderText("Change description of relationship in its end.\nOld description: " + text.getText());
                        dialog.setContentText("New description:");

                        // disable OK button if text-input is same as old
                        BooleanBinding validName = Bindings.createBooleanBinding(() -> {
                            if (dialog.getEditor().getText().equals(text.getText())) {
                                return true;
                            } else {
                                return false;
                            }
                        }, dialog.getEditor().textProperty());
                        dialog.getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(validName);

                        Optional<String> result = dialog.showAndWait();
                        result.ifPresent(newString -> {
                            try {
                                this.dataModel.executeCommand(new EditClassRelationshipToDescCommand(this.intID, newString));
                                //this.updateView();
                            } catch (Exception ex) {
                                //this.showErrorMessage("Unable to set new attribute class name", e.getLocalizedMessage());
                                ex.printStackTrace();
                            }
                        });
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        } else {
            System.out.println("Warning: bindRelationshipActions did not recognize following propertyText: " + prop.getText());
        }
    }

    private void bindSequenceActions(Label prop, Label text, BorderPane line) {
        System.out.println("bindSequenceActions: TODO");
    }

    /**
     * Callback interface for when the data is updated
     */
    public interface UpdatedCallback {
        void onUpdated();
    }

=======
    /**
     * Callback interface for when the data is updated
     */
    public interface UpdatedCallback {
        void onUpdated();
    }
>>>>>>> 80443efb81e7ded0ba7e9c114e4310c40570f41a
}

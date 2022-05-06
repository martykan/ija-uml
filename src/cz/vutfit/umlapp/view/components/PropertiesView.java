/*
 * File: PropertiesView.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.view.components;

import cz.vutfit.umlapp.model.DataModel;
import cz.vutfit.umlapp.model.commands.*;
import cz.vutfit.umlapp.model.uml.*;
import cz.vutfit.umlapp.view.main.TreeViewDataHolder;
import cz.vutfit.umlapp.view.sequencediagram.SequenceDiagramController;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Class for properties view (Properties section in menu)
 */
public class PropertiesView extends VBox {
    private final ButtonType createButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);

    public EPropertyType groupType = EPropertyType.EMPTY;
    public DataModel dataModel;
    public TreeView<TreeViewDataHolder> classTreeView;
    public TreeView<TreeViewDataHolder> messagesTreeView;
    public String stringID = null;
    public int intID;
    public int parentIntID;

    /**
     * Set callback on updated
     *
     * @param updatedCallback callback
     */
    public void setOnUpdated(UpdatedCallback updatedCallback) {
        this.updatedCallback = updatedCallback;
    }
    private UpdatedCallback updatedCallback;

    /**
     * Adds one line to the properties section.
     *
     * @param property name of property, data description
     * @param text     data-value (in String)
     */
    public void addPropertyLine(String property, String text) {
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

    public void setGroupType(EPropertyType type) {
        this.groupType = type;
    }

    public void setDataModel(DataModel model) {
        this.dataModel = model;
    }

    public void setClassTreeView(TreeView<TreeViewDataHolder> x) {
        this.classTreeView = x;
    }

    public void setMessagesTreeView(TreeView<TreeViewDataHolder> x) {
        this.messagesTreeView = x;
    }

    public void setID(int ID) {
        this.intID = ID;
    }

    public void setID(String ID) {
        this.stringID = ID;
    }

    public void setParentID(int ID) { this.parentIntID = ID; }

    private void bindPropertyActions(BorderPane line, Label prop, Label text) {
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
            case SEQ_MESSAGE:
                bindSequenceMessageActions(prop, text, line);
                break;
            case SEQ_OBJECT:
                bindSequenceObjectActions(prop, text, line);
                break;
            case EMPTY:
                if (!prop.getText().equals("Nothing selected"))
                    System.out.println("Warning: Executed bindPropertyActions on groupType EMPTY ["+prop+";"+text+"]");
                break;
        }
    }

    private void bindClassActions(Label prop, Label text, BorderPane line) {
        if (prop.getText().equals("Class")) {
            line.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                if (e.getClickCount() != 2) return;
                TextInputDialog dialog = renameClassDialog(text);
                Optional<String> result = dialog.showAndWait();
                result.ifPresent(className -> {
                    try {
                        ClassDiagram selectedClass = TreeViewDataHolder.getTreeViewSelectedClass(this.classTreeView);
                        assert selectedClass != null;
                        this.dataModel.executeCommand(new EditClassNameCommand(selectedClass.getID(), className));
                        this.updatedCallback.onUpdated();
                    } catch (Exception ex) {
                        this.showErrorMessage("Unable to set new class name", ex.getLocalizedMessage());
                        ex.printStackTrace();
                    }
                });
            });
        } else {
            System.out.println("Warning: bindClassActions did not recognize following propertyText: " + prop.getText());
        }
    }

    private void bindAttributeActions(Label prop, Label text, BorderPane line) {
        if (prop.getText().equals("Attribute")) {
            line.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                if (e.getClickCount() != 2) return;
                try {
                    TextInputDialog dialog = new TextInputDialog("");
                    dialog.setTitle("New Attribute Name");
                    dialog.setHeaderText(null);
                    dialog.setContentText("New Name:");

                    // disable OK button if text-input is empty or name is same as old
                    BooleanBinding validName = Bindings.createBooleanBinding(() -> {
                        if (dialog.getEditor().getText().equals("")) {
                            dialog.setHeaderText("You have not entered any attribute name!");
                            return true;
                        } else if (dialog.getEditor().getText().equals(text.getText())) {
                            dialog.setHeaderText("New name is same as old one!");
                            return true;
                        } else {
                            return false;
                        }
                    }, dialog.getEditor().textProperty());
                    dialog.getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(validName);

                    Optional<String> result = dialog.showAndWait();
                    result.ifPresent(attribName -> {
                        try {
                            ClassDiagram selectedClass = TreeViewDataHolder.getTreeViewSelectedClass(this.classTreeView);
                            String oldAttribName = text.getText();
                            assert selectedClass != null;
                            this.dataModel.executeCommand(new EditClassAttributeNameCommand(selectedClass.getID(), oldAttribName, attribName));
                            this.updatedCallback.onUpdated();
                        } catch (Exception ex) {
                            this.showErrorMessage("Unable to set new attribute name", ex.getLocalizedMessage());
                            ex.printStackTrace();
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        } else if (prop.getText().equals("Class")) {
            line.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                if (e.getClickCount() != 2) return;
                try {
                    TextInputDialog dialog = renameClassDialog(text);
                    Optional<String> result = dialog.showAndWait();
                    result.ifPresent(className -> {
                        try {
                            ClassDiagram selectedClass = TreeViewDataHolder.getTreeViewSelectedClass(this.classTreeView);
                            assert selectedClass != null;
                            this.dataModel.executeCommand(new EditClassNameCommand(selectedClass.getID(), className));
                            this.updatedCallback.onUpdated();
                        } catch (Exception ex) {
                            this.showErrorMessage("Unable to set new attribute class name", ex.getLocalizedMessage());
                            ex.printStackTrace();
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        } else if (prop.getText().equals("Visibility")) {
            line.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                if (e.getClickCount() != 2) return;
                try {
                    // Create the custom dialog.
                    Dialog<EAttribVisibility> dialog = new Dialog<>();
                    dialog.setTitle("Attribute visibility");
                    dialog.setHeaderText("Change current visibility of this attribute");

                    visibilityDialog(text, dialog);

                    Optional<EAttribVisibility> result = dialog.showAndWait();
                    result.ifPresent(newVisibility -> {
                        try {
                            ClassDiagram selectedClass = TreeViewDataHolder.getTreeViewSelectedClass(this.classTreeView);
                            String attribName = this.stringID;
                            assert selectedClass != null;
                            this.dataModel.executeCommand(new EditClassAttributeVisibilityCommand(selectedClass.getID(), attribName, newVisibility));
                            this.updatedCallback.onUpdated();
                        } catch (Exception ex) {
                            this.showErrorMessage("Unable to set new attribute visibility", ex.getLocalizedMessage());
                            ex.printStackTrace();
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        } else if (prop.getText().equals("Type")) {
            line.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                if (e.getClickCount() != 2) return;
                try {
                    // Create the custom dialog.
                    Dialog<String> dialog = new Dialog<>();
                    dialog.setTitle("Attribute type");
                    dialog.setHeaderText("Change current type of this attribute");

                    typeDialog(text, dialog);

                    Optional<String> result = dialog.showAndWait();
                    result.ifPresent(newVisibility -> {
                        try {
                            ClassDiagram selectedClass = TreeViewDataHolder.getTreeViewSelectedClass(this.classTreeView);
                            String attribName = this.stringID;
                            assert selectedClass != null;
                            this.dataModel.executeCommand(new EditClassAttributeTypeCommand(selectedClass.getID(), attribName, (newVisibility.equals("") ? "void" : newVisibility)));
                            this.updatedCallback.onUpdated();
                        } catch (Exception ex) {
                            this.showErrorMessage("Unable to set new attribute type", ex.getLocalizedMessage());
                            ex.printStackTrace();
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        } else {
            System.out.println("Warning: bindAttributeActions did not recognize following propertyText: " + prop.getText());
        }
    }

    private void bindMethodActions(Label prop, Label text, BorderPane line) {
        if (prop.getText().equals("Method")) {
            line.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                if (e.getClickCount() != 2) return;
                try {
                    TextInputDialog dialog = new TextInputDialog("");
                    dialog.setTitle("New Method Name");
                    dialog.setHeaderText(null);
                    dialog.setContentText("New Name:");

                    // disable OK button if text-input is empty or name is same as old
                    BooleanBinding validName = Bindings.createBooleanBinding(() -> {
                        if (dialog.getEditor().getText().equals("")) {
                            dialog.setHeaderText("You have not entered any method name!");
                            return true;
                        } else if (!dialog.getEditor().getText().matches("^[A-z0-9]+\\(.*\\)[ ]?$")) {
                            dialog.setHeaderText("Doesn't match method name format (include parenthesis)");
                            return true;
                        } else if (dialog.getEditor().getText().equals(text.getText())) {
                            dialog.setHeaderText("New name is same as the old one!");
                            return true;
                        } else {
                            dialog.setHeaderText("Name is valid");
                            return false;
                        }
                    }, dialog.getEditor().textProperty());
                    dialog.getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(validName);

                    Optional<String> result = dialog.showAndWait();
                    result.ifPresent(attribName -> {
                        try {
                            ClassDiagram selectedClass = TreeViewDataHolder.getTreeViewSelectedClass(this.classTreeView);
                            String oldAttribName = this.stringID;
                            System.out.println(oldAttribName);
                            assert selectedClass != null;
                            this.dataModel.executeCommand(new EditClassMethodNameCommand(selectedClass.getID(), oldAttribName, attribName));
                            this.updatedCallback.onUpdated();
                        } catch (Exception ex) {
                            this.showErrorMessage("Unable to set new method name", ex.getLocalizedMessage());
                            ex.printStackTrace();
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        } else if (prop.getText().equals("Class")) {
            line.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                if (e.getClickCount() != 2) return;
                try {
                    TextInputDialog dialog = renameClassDialog(text);
                    Optional<String> result = dialog.showAndWait();
                    result.ifPresent(className -> {
                        try {
                            ClassDiagram selectedClass = TreeViewDataHolder.getTreeViewSelectedClass(this.classTreeView);
                            assert selectedClass != null;
                            this.dataModel.executeCommand(new EditClassNameCommand(selectedClass.getID(), className));
                            this.updatedCallback.onUpdated();
                        } catch (Exception ex) {
                            this.showErrorMessage("Unable to set new method class name", ex.getLocalizedMessage());
                            ex.printStackTrace();
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        } else if (prop.getText().equals("Visibility")) {
            line.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                if (e.getClickCount() != 2) return;
                try {
                    // Create the custom dialog.
                    Dialog<EAttribVisibility> dialog = new Dialog<>();
                    dialog.setTitle("Method visibility");
                    dialog.setHeaderText("Change current visibility of this method");

                    visibilityDialog(text, dialog);

                    Optional<EAttribVisibility> result = dialog.showAndWait();
                    result.ifPresent(newVisibility -> {
                        try {
                            ClassDiagram selectedClass = TreeViewDataHolder.getTreeViewSelectedClass(this.classTreeView);
                            String attribName = this.stringID;
                            assert selectedClass != null;
                            this.dataModel.executeCommand(new EditClassMethodVisibilityCommand(selectedClass.getID(), attribName, newVisibility));
                            this.updatedCallback.onUpdated();
                        } catch (Exception ex) {
                            this.showErrorMessage("Unable to set new method visibility", ex.getLocalizedMessage());
                            ex.printStackTrace();
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        } else if (prop.getText().equals("Return type")) {
            line.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                if (e.getClickCount() != 2) return;
                try {
                    // Create the custom dialog.
                    Dialog<String> dialog = new Dialog<>();
                    dialog.setTitle("Method type");
                    dialog.setHeaderText("Change current type of this method");

                    typeDialog(text, dialog);

                    Optional<String> result = dialog.showAndWait();
                    result.ifPresent(newVisibility -> {
                        try {
                            ClassDiagram selectedClass = TreeViewDataHolder.getTreeViewSelectedClass(this.classTreeView);
                            String attribName = this.stringID;
                            assert selectedClass != null;
                            this.dataModel.executeCommand(new EditClassMethodTypeCommand(selectedClass.getID(), attribName, (newVisibility.equals("") ? "void" : newVisibility)));
                            this.updatedCallback.onUpdated();
                        } catch (Exception ex) {
                            this.showErrorMessage("Unable to set new attribute type", ex.getLocalizedMessage());
                            ex.printStackTrace();
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        } else {
            System.out.println("Warning: bindMethodActions did not recognize following propertyText: " + prop.getText());
        }
    }

    private void bindRelationshipActions(Label prop, Label text, BorderPane line) {
        if (prop.getText().equals("Relationship")) {
            line.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                if (e.getClickCount() != 2) return;
                try {
                    TextInputDialog dialog = new TextInputDialog("");
                    dialog.setTitle("Relationship name");
                    dialog.setHeaderText("Change name of this relationship.\nOld name: " + text.getText());
                    dialog.setContentText("New name:");

                    // disable OK button if text-input is same as old
                    BooleanBinding validName = Bindings.createBooleanBinding(() -> (
                            dialog.getEditor().getText().equals(text.getText())
                    ), dialog.getEditor().textProperty());
                    dialog.getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(validName);

                    Optional<String> result = dialog.showAndWait();
                    result.ifPresent(newString -> {
                        try {
                            this.dataModel.executeCommand(new EditClassRelationshipNameCommand(this.intID, newString));
                            this.updatedCallback.onUpdated();
                        } catch (Exception ex) {
                            this.showErrorMessage("Unable to set new name", ex.getLocalizedMessage());
                            ex.printStackTrace();
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        } else if (prop.getText().equals("From") || (prop.getText().equals("To"))) {
            line.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                if (e.getClickCount() != 2) return;
                try {
                    // Create the custom dialog.
                    Dialog<ArrayList<String>> dialog = new Dialog<>();
                    dialog.setTitle("Relationship path");
                    dialog.setHeaderText("Change path of this relationship.\nYou can change class, where this relationship starts or ends.");

                    GridPane grid = dialogSetupWithGrid(dialog);
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
                            this.updatedCallback.onUpdated();
                        } catch (Exception ex) {
                            this.showErrorMessage("Unable to set new beginning or end of relationship", ex.getLocalizedMessage());
                            ex.printStackTrace();
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        } else if (prop.getText().equals("Type")) {
            line.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                if (e.getClickCount() != 2) return;
                try {
                    // Create the custom dialog.
                    Dialog<ERelationType> dialog = new Dialog<>();
                    dialog.setTitle("Relationship type");
                    dialog.setHeaderText("Change current type of this relationship");

                    GridPane grid = dialogSetupWithGrid(dialog);
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
                            return ERelationType.fromString(x);
                        }
                        return null;
                    });

                    Optional<ERelationType> result = dialog.showAndWait();
                    result.ifPresent(newType -> {
                        try {
                            this.dataModel.executeCommand(new EditClassRelationshipTypeCommand(this.intID, newType));
                            this.updatedCallback.onUpdated();
                        } catch (Exception ex) {
                            this.showErrorMessage("Unable to set new type of relationship", ex.getLocalizedMessage());
                            ex.printStackTrace();
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        } else if (prop.getText().equals("FromDesc")) {
            line.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                if (e.getClickCount() != 2) return;
                try {
                    TextInputDialog dialog = new TextInputDialog("");
                    dialog.setTitle("Relationship description");
                    dialog.setHeaderText("Change description of relationship in its beginning.\nOld description: " + text.getText());
                    dialog.setContentText("New description:");

                    // disable OK button if text-input is same as old
                    BooleanBinding validName = Bindings.createBooleanBinding(() -> (
                            dialog.getEditor().getText().equals(text.getText())
                    ), dialog.getEditor().textProperty());
                    dialog.getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(validName);

                    Optional<String> result = dialog.showAndWait();
                    result.ifPresent(newString -> {
                        try {
                            this.dataModel.executeCommand(new EditClassRelationshipFromDescCommand(this.intID, newString));
                            this.updatedCallback.onUpdated();
                        } catch (Exception ex) {
                            this.showErrorMessage("Unable to set new description", ex.getLocalizedMessage());
                            ex.printStackTrace();
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        } else if (prop.getText().equals("ToDesc")) {
            line.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                if (e.getClickCount() != 2) return;
                try {
                    TextInputDialog dialog = new TextInputDialog("");
                    dialog.setTitle("Relationship description");
                    dialog.setHeaderText("Change description of relationship in its end.\nOld description: " + text.getText());
                    dialog.setContentText("New description:");

                    // disable OK button if text-input is same as old
                    BooleanBinding validName = Bindings.createBooleanBinding(() -> (
                            dialog.getEditor().getText().equals(text.getText())
                    ), dialog.getEditor().textProperty());
                    dialog.getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(validName);

                    Optional<String> result = dialog.showAndWait();
                    result.ifPresent(newString -> {
                        try {
                            this.dataModel.executeCommand(new EditClassRelationshipToDescCommand(this.intID, newString));
                            this.updatedCallback.onUpdated();
                        } catch (Exception ex) {
                            this.showErrorMessage("Unable to set new description", ex.getLocalizedMessage());
                            ex.printStackTrace();
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        } else {
            System.out.println("Warning: bindRelationshipActions did not recognize following propertyText: " + prop.getText());
        }
    }

    private void bindSequenceMessageActions(Label prop, Label text, BorderPane line) {
        if (prop.getText().equals("Message")) {
            line.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                if (e.getClickCount() != 2) return;
                try {
                    Dialog<String> dialog = new Dialog<>();
                    dialog.setTitle("Change message");
                    dialog.setHeaderText("Change content of this message");

                    GridPane grid = dialogSetupWithGrid(dialog);
                    grid.setPadding(new Insets(20, 150, 10, 10));

                    ChoiceBox<String> contentBox = new ChoiceBox<>();
                    String clName = this.dataModel.getData().getSequenceByID(this.parentIntID).getMessageByID(this.intID).getReceiver().getKey();
                    SequenceDiagramController.populateMethodsContentBox(this.dataModel, contentBox, clName);

                    grid.add(new Label("New Content: "), 0, 0);
                    grid.add(contentBox, 1, 0);
                    TextField f = new TextField();
                    grid.add(f, 1, 0);
                    f.setVisible(false);
                    dialog.getDialogPane().setContent(grid);

                    BooleanBinding validName = Bindings.createBooleanBinding(() -> (
                            contentBox.getSelectionModel().getSelectedItem().equals("<no methods>")
                    ), contentBox.getSelectionModel().selectedItemProperty());
                    dialog.getDialogPane().lookupButton(createButtonType).disableProperty().bind(validName);

                    dialog.setResultConverter(dialogButton -> {
                        if (dialogButton == createButtonType) {
                            String x = contentBox.getSelectionModel().getSelectedItem();
                            if (f.isVisible())
                                x = f.getText();
                            return x;
                        }
                        return null;
                    });

                    BooleanBinding fVisible = Bindings.createBooleanBinding(() -> (
                            this.dataModel.getData().getSequenceByID(this.parentIntID).getMessageByID(this.intID).getType() == EMessageType.RETURN
                    ));
                    f.visibleProperty().bind(fVisible);

                    Optional<String> result = dialog.showAndWait();
                    result.ifPresent(newString -> {
                        try {
                            this.dataModel.executeCommand(new EditSequenceDiagramMessageContentCommand(this.parentIntID, this.intID, newString));
                            this.updatedCallback.onUpdated();
                        } catch (Exception ex) {
                            this.showErrorMessage("Unable to set new message content", ex.getLocalizedMessage());
                            ex.printStackTrace();
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        } else if (prop.getText().equals("From") || (prop.getText().equals("To"))) {
            line.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                if (e.getClickCount() != 2) return;
                try {
                    EMessageType thisMsgType = this.dataModel.getData().getSequenceByID(this.parentIntID).getMessageByID(this.intID).getType();
                    if (thisMsgType == EMessageType.NEW_OBJECT || thisMsgType == EMessageType.RELEASE_OBJECT) {
                        return;
                    }
                    // Create the custom dialog.
                    Dialog<ArrayList<Pair<String, String>>> dialog = new Dialog<>();
                    dialog.setTitle("Message direction");
                    dialog.setHeaderText("m of message.\nYou can change class-object, who sends and who receives this message.");

                    GridPane grid = dialogSetupWithGrid(dialog);
                    grid.setPadding(new Insets(20, 150, 10, 10));


                    ChoiceBox<String> fromClassBox = new ChoiceBox<>();
                    ChoiceBox<String> toClassBox = new ChoiceBox<>();
                    ArrayList<Pair<String, String>> fromClassID = new ArrayList<>();
                    ArrayList<Pair<String, String>> toClassID = new ArrayList<>();
                    SequenceMessages myMessage = this.dataModel.getData().getSequenceByID(this.parentIntID).getMessageByID(this.intID);
                    String current = "From object '" + myMessage.getSender().getKey() + ":" + myMessage.getSender().getValue() + "' to object '" + myMessage.getReceiver().getKey() + ":" + myMessage.getReceiver().getValue() + "'";

                    for (SequenceObjects c : this.dataModel.getData().getSequenceByID(this.parentIntID).getObjects()) {
                        fromClassBox.getItems().add(c.getObjectName() + " [" + c.getClassName() + "]");
                        fromClassID.add(new Pair<>(c.getClassName(), c.getObjectName()));
                        toClassBox.getItems().add(c.getObjectName() + " [" + c.getClassName() + "]");
                        toClassID.add(new Pair<>(c.getClassName(), c.getObjectName()));
                    }
                    fromClassBox.getSelectionModel().selectFirst();
                    toClassBox.getSelectionModel().selectFirst();
                    ChoiceBox<String> msgBox = new ChoiceBox<>();
                    String selection = toClassBox.getSelectionModel().getSelectedItem();
                    String classID = selection.split("\\[", 2)[1].split("]", 2)[0];
                    SequenceDiagramController.populateMethodsContentBox(this.dataModel, msgBox, classID);

                    grid.add(new Label("Current participants: "), 0, 0);
                    grid.add(new Label(current), 1, 0);
                    grid.add(new Label("New Content: "), 0, 1);
                    grid.add(msgBox, 1, 1);
                    grid.add(new Label("From: "), 0, 2);
                    grid.add(fromClassBox, 1, 2);
                    grid.add(new Label("To: "), 0, 3);
                    grid.add(toClassBox, 1, 3);
                    dialog.getDialogPane().setContent(grid);

                    toClassBox.setOnAction(event -> {
                        msgBox.getItems().clear();
                        String selectionX = toClassBox.getSelectionModel().getSelectedItem();
                        String classIDX = selectionX.split("\\[", 2)[1].split("]", 2)[0];
                        SequenceDiagramController.populateMethodsContentBox(this.dataModel, msgBox, classIDX);

                        BooleanBinding validName = Bindings.createBooleanBinding(() -> msgBox.getItems().get(0).equals("<no methods>"));
                        dialog.getDialogPane().lookupButton(createButtonType).disableProperty().bind(validName);
                    });

                    dialog.setResultConverter(dialogButton -> {
                        if (dialogButton == createButtonType) {
                            ArrayList<Pair<String, String>> x = new ArrayList<>();
                            x.add(fromClassID.get(fromClassBox.getSelectionModel().getSelectedIndex()));
                            x.add(toClassID.get(toClassBox.getSelectionModel().getSelectedIndex()));
                            x.add(new Pair<>(msgBox.getSelectionModel().getSelectedItem(), ""));
                            return x;
                        }
                        return null;
                    });

                    Optional<ArrayList<Pair<String, String>>> result = dialog.showAndWait();
                    result.ifPresent(data -> {
                        try {
                            Pair<String, String> newSender = data.get(0);
                            Pair<String, String> newReceiver = data.get(1);
                            this.dataModel.executeCommand(new EditSequenceDiagramMessageParticipantsCommand(this.parentIntID, this.intID, newSender, newReceiver, data.get(2).getKey()));
                            this.updatedCallback.onUpdated();
                        } catch (Exception ex) {
                            this.showErrorMessage("Unable to set new participants for selected message", ex.getLocalizedMessage());
                            ex.printStackTrace();
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        } else if (prop.getText().equals("Type")) {
            line.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                if (e.getClickCount() != 2) return;
                try {
                    EMessageType thisMsgType = this.dataModel.getData().getSequenceByID(this.parentIntID).getMessageByID(this.intID).getType();
                    if (thisMsgType == EMessageType.NEW_OBJECT || thisMsgType == EMessageType.RELEASE_OBJECT) {
                        return;
                    }
                    // Create the custom dialog.
                    Dialog<EMessageType> dialog = new Dialog<>();
                    dialog.setTitle("Message type");
                    dialog.setHeaderText("Change current type of this message");

                    GridPane grid = dialogSetupWithGrid(dialog);
                    grid.setPadding(new Insets(20, 150, 10, 10));

                    Label currType = new Label(text.getText());

                    ChoiceBox<String> typeBox = new ChoiceBox<>();
                    for (EMessageType x : EMessageType.values()) {
                        if (x != EMessageType.NEW_OBJECT && x != EMessageType.RELEASE_OBJECT)
                            typeBox.getItems().add(x.typeToString());
                    }
                    typeBox.getSelectionModel().selectFirst();

                    grid.add(new Label("Current type: "), 0, 0);
                    grid.add(currType, 1, 0);
                    grid.add(new Label("New type: "), 0, 1);
                    grid.add(typeBox, 1, 1);
                    dialog.getDialogPane().setContent(grid);

                    dialog.setResultConverter(dialogButton -> {
                        if (dialogButton == createButtonType) {
                            String x = typeBox.getSelectionModel().getSelectedItem();
                            return EMessageType.fromString(x);
                        }
                        return null;
                    });

                    Optional<EMessageType> result = dialog.showAndWait();
                    result.ifPresent(newType -> {
                        try {
                            this.dataModel.executeCommand(new EditSequenceDiagramMessageTypeCommand(this.parentIntID, this.intID, newType));
                            this.updatedCallback.onUpdated();
                        } catch (Exception ex) {
                            this.showErrorMessage("Unable to set new type of message", ex.getLocalizedMessage());
                            ex.printStackTrace();
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        } else {
            System.out.println("Warning: bindSequenceMessageActions did not recognize following propertyText: " + prop.getText());
        }
    }

    private void bindSequenceObjectActions(Label prop, Label text, BorderPane line) {
        if (prop.getText().equals("Class instance")) {
            if (this.stringID != null) { // Class instance property in Object

            } else { // Class instance property in Class instances

            }
        } else if (prop.getText().equals("Object")) {

        } else if (prop.getText().equals("Status")) {

        } else {
            System.out.println("Warning: bindSequenceObjectActions did not recognize following propertyText: " + prop.getText());
        }
    }

    /**
     * Callback interface for when the data is updated
     */
    public interface UpdatedCallback {
        void onUpdated();
    }

    private GridPane dialogSetupWithGrid(Dialog dialog) {
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        return grid;
    }

    private void typeDialog(Label text, Dialog<String> dialog) {
        GridPane grid = dialogSetupWithGrid(dialog);
        grid.setPadding(new Insets(20, 150, 10, 10));

        Label currVis = new Label(text.getText());
        TextField visBox = new TextField();

        grid.add(new Label("Current type: "), 0, 0);
        grid.add(currVis, 1, 0);
        grid.add(new Label("New type: "), 0, 1);
        grid.add(visBox, 1, 1);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                return visBox.getText();
            }
            return null;
        });
    }

    private void visibilityDialog(Label text, Dialog<EAttribVisibility> dialog) {
        GridPane grid = dialogSetupWithGrid(dialog);
        grid.setPadding(new Insets(20, 150, 10, 10));

        Label currVis = new Label(text.getText());

        ChoiceBox<String> visBox = new ChoiceBox<>();
        for (EAttribVisibility x : EAttribVisibility.values()) {
            visBox.getItems().add(x.getVisibilityString());
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
    }

    private TextInputDialog renameClassDialog(Label text) {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("New Class Name");
        dialog.setHeaderText(null);
        dialog.setContentText("New Name:");

        // disable OK button if text-input is empty or name is same as old
        BooleanBinding validName = Bindings.createBooleanBinding(() -> (
                dialog.getEditor().getText().equals("") || dialog.getEditor().getText().equals(text.getText())
        ), dialog.getEditor().textProperty());
        dialog.getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(validName);
        return dialog;
    }

    private void showErrorMessage(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

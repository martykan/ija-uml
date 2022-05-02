/*
 * File: SequenceDiagramController.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

/*
 * File: SequenceDiagramController.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.view.sequencediagram;

import cz.vutfit.umlapp.model.ModelFactory;
import cz.vutfit.umlapp.model.commands.*;
import cz.vutfit.umlapp.model.uml.*;
import cz.vutfit.umlapp.view.ViewHandler;
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
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Optional;

public class SequenceDiagramController extends MainController {
    @FXML
    public TreeView<String> diagramTreeView;

    @FXML
    public TreeView<String> classTreeView;

    @FXML
    public TreeView<String> messageTreeView;

    @FXML
    public PropertiesView propertiesView;

    private String selectedDiagram;
    private String selectedClass;
    private String selectedMessage;

    ChangeListener<TreeItem<String>> handleDiagramSelection = (observableValue, oldItem, newItem) -> {
        if (newItem != null) {
            this.selectedDiagram = newItem.getValue();
            this.classTreeView.getSelectionModel().clearSelection();
            this.messageTreeView.getSelectionModel().clearSelection();
            handleProperties(newItem);
        } else {
            this.selectedDiagram = null;
            propertiesView.resetProperties();
            try {
                propertiesView.addPropertyLine("Nothing selected", "");
            } catch (Exception e) {
                this.showErrorMessage(e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
        //boxClassOptions.setVisible(this.selectedClass != null);
    };

    ChangeListener<TreeItem<String>> handleClassSelection = (observableValue, oldItem, newItem) -> {
        if (newItem != null) {
            this.selectedClass = newItem.getValue();
            this.messageTreeView.getSelectionModel().clearSelection();
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
        //boxClassOptions.setVisible(this.selectedClass != null);
    };

    ChangeListener<TreeItem<String>> handleMessageSelection = (observableValue, oldItem, newItem) -> {
        if (newItem != null) {
            this.selectedMessage = newItem.getValue().split("\\.", 2)[1];
            this.classTreeView.getSelectionModel().clearSelection();
            handleProperties(newItem);
        } else {
            this.selectedMessage = null;
            propertiesView.resetProperties();
            try {
                propertiesView.addPropertyLine("Nothing selected", "");
            } catch (Exception e) {
                this.showErrorMessage(e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
        //boxClassOptions.setVisible(this.selectedClass != null);
    };

    @Override
    public void init(ModelFactory modelFactory, ViewHandler viewHandler) {
        super.init(modelFactory, viewHandler);
        this.diagramTreeView.getSelectionModel().selectedItemProperty().addListener(handleDiagramSelection);
        this.classTreeView.getSelectionModel().selectedItemProperty().addListener(handleClassSelection);
        this.messageTreeView.getSelectionModel().selectedItemProperty().addListener(handleMessageSelection);
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
            // Diagram menu
            TreeViewItemModel diagrams = new TreeViewItemModel(this.dataModel, diagramTreeView, EDataType.DIAGRAM);
            diagrams.showTreeItem();
            diagrams.rootViewUpdate();

            // Classes (objects) menu
            TreeViewItemModel classes = new TreeViewItemModel(this.dataModel, classTreeView, EDataType.SEQ_OBJECTS);

            this.selectedDiagram = this.dataModel.getActiveDiagram();

            classes.setSelectedSequence(this.selectedDiagram);
            classes.showTreeItem();
            classes.rootViewUpdate();

            // Messages menu
            TreeViewItemModel messages = new TreeViewItemModel(this.dataModel, messageTreeView, EDataType.SEQ_MESSAGES);
            messages.setSelectedSequence(this.selectedDiagram);
            messages.showTreeItem();
            messages.rootViewUpdate();

            //this.initDragDrop();

            //boxClassOptions.setVisible(this.selectedClass != null);
        } catch (Exception e) {
            this.showErrorMessage(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public void handleRemoveDiagram(ActionEvent actionEvent) {
        try {
            String id;
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Remove Sequence Diagram");
            alert.setContentText("This action will remove currently selected sequence diagram. Proceed?");
            alert.setHeaderText("Remove " + this.selectedDiagram);
            ButtonType okButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
            ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
            alert.getButtonTypes().setAll(okButton, noButton);
            alert.showAndWait().ifPresent(type -> {
                if (type.getButtonData() == ButtonBar.ButtonData.YES) {
                    try {
                        SequenceDiagram seqDiagram = this.dataModel.getData().getSequenceByName(this.selectedDiagram);
                        this.dataModel.executeCommand(new RemoveSequenceDiagramCommand(seqDiagram.getID()));
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

    public void handleRemoveClass (ActionEvent actionEvent) {
        try {
            SequenceDiagram currentSequence = this.dataModel.getData().getSequenceByName(this.selectedDiagram);
            ArrayList<SequenceObjects> objects = currentSequence.getObjects();

            // if no objects present, do nothing
            if (objects.size() == 0)
                return;

            try {
                String objectName = this.classTreeView.getSelectionModel().getSelectedItem().getValue(); // TODO: split by :
                this.dataModel.executeCommand(new RemoveSequenceDiagramObjectCommand(currentSequence.getID(), objectName, objectName));
                this.updateView();
            } catch (Exception ex) {
                this.showErrorMessage("Unable to remove object from sequence diagram", ex.getLocalizedMessage());
                ex.printStackTrace();
            }
        } catch (Exception ex) {
            this.showErrorMessage("Unable to remove object", ex.getLocalizedMessage());
            ex.printStackTrace();
        }
    }

    public void handleAddClass (ActionEvent actionEvent) {
        try {
            // Create the custom dialog.
            Dialog<ArrayList<String>> dialog = new Dialog<>();
            dialog.setTitle("Add new object");
            dialog.setHeaderText("Add new object to this sequence diagram");

            ButtonType createButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));


            ChoiceBox<String> classBox = new ChoiceBox<>();
            boolean isObject = false;
            for (ClassDiagram x : this.dataModel.getData().getClasses()) {
                isObject = false;
                for (TreeItem<String> item : this.classTreeView.getRoot().getChildren()) {
                    if (item.getValue().equals(x.getName())) {
                        isObject = true;
                        break;
                    }
                }
                if (!isObject)
                    classBox.getItems().add(x.getName());
            }
            classBox.getSelectionModel().selectFirst();

            TextField nameBox = new TextField();

            if (classBox.getItems().size() != 0) { // can add at least 1 class
                grid.add(new Label("Objects in sequence diagrams must be from existing class instances from class diagram."), 0, 0);
                grid.add(new Label("Select class instance: "), 0, 1);
                grid.add(classBox, 1, 1);
                grid.add(new Label("Object name: "), 0, 2);
                grid.add(nameBox, 1, 2);
                dialog.getDialogPane().setContent(grid);

                dialog.setResultConverter(dialogButton -> {
                    if (dialogButton == createButtonType) {
                        ArrayList<String> x = new ArrayList<>();
                        x.add(classBox.getSelectionModel().getSelectedItem());
                        x.add(nameBox.getText());
                        return x;
                    }
                    return null;
                });

                Optional<ArrayList<String>> result = dialog.showAndWait();
                result.ifPresent(returned -> {
                    try {
                        int seqID = this.dataModel.getData().getSequenceByName(this.selectedDiagram).getID();
                        this.dataModel.executeCommand(new AddSequenceDiagramObjectCommand(seqID, returned.get(0), returned.get(1)));
                        this.updateView();
                    } catch (Exception ex) {
                        this.showErrorMessage("Unable to add new object to sequence diagram", ex.getLocalizedMessage());
                        ex.printStackTrace();
                    }
                });
            } else { // no class in class diagram
                grid.add(new Label("There are no classes in Class Diagram."), 0, 0);
                grid.add(new Label("Create new class?"), 0, 1);
                dialog.getDialogPane().setContent(grid);

                dialog.setResultConverter(dialogButton -> {
                    if (dialogButton == createButtonType) {
                        return new ArrayList<String>();
                    }
                    return null;
                });

                Optional<ArrayList<String>> result = dialog.showAndWait();
                result.ifPresent(resulted -> {
                    try {
                        this.addClassToClassDiagram();
                    } catch (Exception ex) {
                        this.showErrorMessage("Unable to add new class to class diagram", ex.getLocalizedMessage());
                        ex.printStackTrace();
                    }
                });
            }
        } catch (Exception ex) {
            this.showErrorMessage("Unable to add new object", ex.getLocalizedMessage());
            ex.printStackTrace();
        }
    }

    public void addClassToClassDiagram() {
        try {
            TextInputDialog dialog = new TextInputDialog("");
            dialog.setTitle("New Class");
            dialog.setHeaderText("Add new class to Class diagram and to this Sequence diagram");
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
                    try {
                        int seqID = this.dataModel.getData().getSequenceByName(this.selectedDiagram).getID();
                        this.dataModel.executeCommand(new AddSequenceDiagramObjectCommand(seqID, className, "New Object"));
                    } catch (Exception e) {
                        this.showErrorMessage("Unable to add new object", e.getLocalizedMessage());
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    this.showErrorMessage("Unable to add new class", e.getLocalizedMessage());
                    e.printStackTrace();
                }
                this.updateView();
            });
        } catch (Exception exception) {
            this.showErrorMessage(exception.getLocalizedMessage());
            exception.printStackTrace();
        }
    }

    public void handleRemoveMessage (ActionEvent actionEvent) {
        try {
            SequenceDiagram currentSequence = this.dataModel.getData().getSequenceByName(this.selectedDiagram);
            ArrayList<SequenceMessages> messages = currentSequence.getMessages();

            // if no messages present, do nothing
            if (messages.size() == 0)
                return;

            try {
                String selectedItem = this.messageTreeView.getSelectionModel().getSelectedItem().getValue();
                String splitNumber = selectedItem.split("\\.", 2)[0];
                int messageIndex = Integer.parseInt(splitNumber);
                this.dataModel.executeCommand(new RemoveSequenceDiagramMessageCommand(currentSequence.getID(), messageIndex));
                this.updateView();
            } catch (Exception ex) {
                this.showErrorMessage("Unable to remove message from sequence diagram", ex.getLocalizedMessage());
                ex.printStackTrace();
            }
        } catch (Exception ex) {
            this.showErrorMessage("Unable to remove message", ex.getLocalizedMessage());
            ex.printStackTrace();
        }
    }

    public void handleAddMessage (ActionEvent actionEvent) {
        try {
            if (this.dataModel.getData().getSequenceByName(this.selectedDiagram).getObjects().size() != 0) {
                // Create the custom dialog.
                Dialog<ArrayList<String>> dialog = new Dialog<>();
                dialog.setTitle("Add new message");
                dialog.setHeaderText("Add new message to this sequence diagram");

                ButtonType createButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(20, 150, 10, 10));


                ChoiceBox<String> msgType = new ChoiceBox<>();
                ChoiceBox<String> sender = new ChoiceBox<>();
                ChoiceBox<String> receiver = new ChoiceBox<>();
                for (EMessageType type : EMessageType.values()) {
                    msgType.getItems().add(type.typeToString());
                }
                msgType.getSelectionModel().selectFirst();

                for (SequenceObjects obj : this.dataModel.getData().getSequenceByName(this.selectedDiagram).getObjects()) {
                    sender.getItems().add(obj.getObjectName() + " in " + obj.getClassName());
                }
                sender.getSelectionModel().selectFirst();

                for (SequenceObjects obj : this.dataModel.getData().getSequenceByName(this.selectedDiagram).getObjects()) {
                    receiver.getItems().add(obj.getObjectName() + " in " + obj.getClassName());
                }
                receiver.getSelectionModel().selectFirst();

                TextField contentBox = new TextField();

                grid.add(new Label("Message content: "), 0, 0);
                grid.add(contentBox, 1, 0);
                grid.add(new Label("Message type: "), 0, 1);
                grid.add(msgType, 1, 1);
                grid.add(new Label("Sender: "), 0, 2);
                grid.add(sender, 1, 2);
                grid.add(new Label("Receiver: "), 0, 3);
                grid.add(receiver, 1, 3);

                dialog.getDialogPane().setContent(grid);

                dialog.setResultConverter(dialogButton -> {
                    if (dialogButton == createButtonType) {
                        ArrayList<String> x = new ArrayList<>();
                        x.add(contentBox.getText());
                        x.add(msgType.getSelectionModel().getSelectedItem());
                        x.add(sender.getSelectionModel().getSelectedItem());
                        x.add(receiver.getSelectionModel().getSelectedItem());
                        return x;
                    }
                    return null;
                });

                Optional<ArrayList<String>> result = dialog.showAndWait();
                result.ifPresent(returned -> {
                    try {
                        int seqID = this.dataModel.getData().getSequenceByName(this.selectedDiagram).getID();
                        Pair<String, String> senderStr = new Pair<>(returned.get(2).split(":", 2)[0], returned.get(2).split(":", 2)[1]);
                        Pair<String, String> receiverStr = new Pair<>(returned.get(3).split(":", 2)[0], returned.get(3).split(":", 2)[1]);
                        EMessageType retType = stringToEMessageType(returned.get(1));
                        this.dataModel.executeCommand(new AddSequenceDiagramMessageCommand(seqID, returned.get(0), senderStr, receiverStr, retType));
                        this.updateView();
                    } catch (Exception ex) {
                        this.showErrorMessage("Unable to add new message to sequence diagram", ex.getLocalizedMessage());
                        ex.printStackTrace();
                    }
                });
            } else {
                String id;
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("No object found");
                alert.setContentText("There are no objects to communicate with, therefore you can not add new message.\nAdd new object to sequence diagram?");
                alert.setHeaderText("Missing objects in " + this.selectedDiagram);
                ButtonType okButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
                ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
                alert.getButtonTypes().setAll(okButton, noButton);
                alert.showAndWait().ifPresent(type -> {
                    if (type.getButtonData() == ButtonBar.ButtonData.YES) {
                        try {
                            handleAddClass(null);
                        } catch (Exception e) {
                            this.showErrorMessage(e.getLocalizedMessage());
                            e.printStackTrace();
                        }
                    } else if (type.getButtonData() == ButtonBar.ButtonData.NO) {
                        return;
                    }
                });
            }
        } catch (Exception ex) {
            this.showErrorMessage("Unable to add new message", ex.getLocalizedMessage());
            ex.printStackTrace();
        }
    }

    public EMessageType stringToEMessageType(String string) {
        if (string.equals("Synchronized"))
            return EMessageType.SYNC;
        else if (string.equals("Asynchronous"))
            return EMessageType.ASYNC;
        else if (string.equals("Return message"))
            return EMessageType.RETURN;
        else if (string.equals("Object creation"))
            return EMessageType.NEW_OBJECT;
        else if (string.equals("Object release"))
            return EMessageType.RELEASE_OBJECT;
        else
            return null;
    }

    public void handleProperties(TreeItem<String> selected) {
        try {
            propertiesView.setDataModel(this.dataModel);
            propertiesView.setClassTreeView(this.classTreeView);
            propertiesView.setMessagesTreeView(this.messageTreeView);
            SequenceDiagram current = this.dataModel.getData().getSequenceByName(this.selectedDiagram);
            if (this.classTreeView.getSelectionModel().getSelectedItem() != null) {
                String className = this.selectedClass.split(":", 2)[0];
                String objectName = this.selectedClass.split(":", 2)[1];
                SequenceObjects object = current.getObject(className, objectName);
                propertiesView.resetProperties();
                propertiesView.setGroupType(EPropertyType.SEQ_OBJECT);
                propertiesView.setParentID(current.getID());
                propertiesView.setID(object.getClassName() + ":" + object.getObjectName());
                propertiesView.addPropertyLine("Object", this.selectedClass);
                propertiesView.addPropertyLine("Status", object.getActiveStatusString());
            } else if (this.messageTreeView.getSelectionModel().getSelectedItem() != null) {
                SequenceMessages message = current.getMessageByIndex(this.messageTreeView.getSelectionModel().getSelectedIndex());
                Pair<String, String> fromObject = message.getSender();
                Pair<String, String> toObject = message.getReceiver();

                propertiesView.resetProperties();
                propertiesView.setGroupType(EPropertyType.SEQ_MESSAGE);
                propertiesView.setParentID(current.getID());
                propertiesView.setID(message.getID());
                propertiesView.addPropertyLine("Message", this.selectedMessage);
                propertiesView.addPropertyLine("From", fromObject.getKey() + ":" + toObject.getValue());
                propertiesView.addPropertyLine("To", toObject.getKey() + ":" + toObject.getValue());
                propertiesView.addPropertyLine("Type", message.getType().typeToString());
            } else {
                propertiesView.resetProperties();
                propertiesView.addPropertyLine("Nothing selected", "");
            }
        } catch (Exception e) {
            this.showErrorMessage("Unable to show properties menu", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }
}

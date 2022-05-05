/*
 * File: SequenceDiagramController.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

/*
 * File: SequenceDiagramController.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.view.sequencediagram;

import cz.vutfit.umlapp.model.*;
import cz.vutfit.umlapp.model.commands.*;
import cz.vutfit.umlapp.model.uml.*;
import cz.vutfit.umlapp.view.ViewHandler;
import cz.vutfit.umlapp.view.components.ArrowHead;
import cz.vutfit.umlapp.view.components.EPropertyType;
import cz.vutfit.umlapp.view.components.PropertiesView;
import cz.vutfit.umlapp.view.components.UMLSequenceObjectView;
import cz.vutfit.umlapp.view.main.EDataType;
import cz.vutfit.umlapp.view.main.MainController;
import cz.vutfit.umlapp.view.main.TreeViewItemModel;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;
import javafx.util.Pair;

import java.util.*;

public class SequenceDiagramController extends MainController {
    @FXML
    public TreeView<String> classTreeView;

    @FXML
    public TreeView<String> messageTreeView;

    @FXML
    public PropertiesView propertiesView;

    @FXML
    public HBox boxMessageOptions;

    @FXML
    public HBox boxClassOptions;

    private String selectedDiagram;
    private String selectedClass;
    private String selectedMessage;

    private final double cardWidth = 200;
    private final double spaceWidth = 250;
    private final double spaceHeight = 40;

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
        boxClassOptions.setVisible(this.selectedClass != null);
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
        boxMessageOptions.setVisible(this.selectedMessage != null);
    };

    @Override
    public void init(ModelFactory modelFactory, ViewHandler viewHandler) {
        super.init(modelFactory, viewHandler);
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

            // Handle inconsistencies
            diagramCheck();

            this.initDiagram();

            boxMessageOptions.setVisible(this.selectedMessage != null);
            boxClassOptions.setVisible(this.selectedClass != null);
        } catch (Exception e) {
            this.showErrorMessage(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    private void initDiagram() {
        anchorScrollPane.getChildren().clear();
        HashMap<String, UMLSequenceObjectView> objectsViewMap = new HashMap<>();
        SequenceDiagram diagram = this.dataModel.getData().getSequenceByName(this.selectedDiagram);

        double currentX = 10;
        double currentY = 100;

        for (SequenceObjects sequenceObject : diagram.getObjects()) {
            // Is created or destroyed at some point?
            int activatedAt = -1;
            int destroyedAt = -1;
            ArrayList<SequenceMessages> messages = diagram.getMessages();
            for (int i = 0; i < messages.size(); i++) {
                SequenceMessages sequenceMessage = messages.get(i);
                if (Objects.equals(sequenceMessage.toObject.getValue() + ":" + sequenceMessage.toObject.getKey(), sequenceObject.getObjectClassName())) {
                    if (activatedAt == -1 && sequenceMessage.type == EMessageType.NEW_OBJECT) {
                        activatedAt = i;
                    }
                    if (sequenceMessage.type == EMessageType.RELEASE_OBJECT) {
                        destroyedAt = i;
                    }
                }
            }

            // Lifeline
            double startPos = 10;
            if (activatedAt >= 0) {
                startPos = 100 + activatedAt * spaceHeight;
            }
            double endPos = 200 + diagram.getMessages().size() * spaceHeight;
            if (destroyedAt >= 0) {
                endPos = 100 + destroyedAt * spaceHeight;
            }
            Line line = new Line();
            line.setStartX(10 + cardWidth / 2 + currentX);
            line.setStartY(startPos);
            line.setEndX(10 + cardWidth / 2 + currentX);
            line.setEndY(endPos);
            line.setStrokeWidth(2);
            line.setStroke(Paint.valueOf("#aaaaaa"));
            line.getStrokeDashArray().addAll(2.0, 4.0);
            anchorScrollPane.getChildren().add(line);

            // Object
            UMLSequenceObjectView node = new UMLSequenceObjectView(sequenceObject);
            node.setTranslateX(10 + currentX);
            node.setTranslateY(startPos);
            anchorScrollPane.getChildren().add(node);
            objectsViewMap.put(sequenceObject.getObjectClassName(), node);
            currentX += spaceWidth;
        }

        // Activation boxes
        ArrayList<SequenceMessages> messages = diagram.getMessages();
        List<Node> nodesToAdd = new ArrayList<>();
        for (int i = 0; i < messages.size(); i++) {
            SequenceMessages sequenceMessage = messages.get(i);
            UMLSequenceObjectView fromObject = objectsViewMap.get(sequenceMessage.fromObject.getValue() + ":" + sequenceMessage.fromObject.getKey());
            UMLSequenceObjectView toObject = objectsViewMap.get(sequenceMessage.toObject.getValue() + ":" + sequenceMessage.toObject.getKey());
            if (fromObject == null || toObject == null) continue;

            if (fromObject.getActivatedAt() >= 0) {
                if (i >= messages.size() - 1 || sequenceMessage.type == EMessageType.RELEASE_OBJECT || sequenceMessage.type == EMessageType.RETURN) {
                    createActivationRectangle(i, fromObject);
                    System.out.println("Deactivating from at message " + i);
                }
            } else if (
                    i == 0 ||
                            sequenceMessage.type == EMessageType.NEW_OBJECT ||
                            sequenceMessage.type == EMessageType.SYNC ||
                            sequenceMessage.type == EMessageType.ASYNC
            ) {
                fromObject.setActivatedAt(i);
                System.out.println("Activating from at message " + i);
            }
            if (toObject.getActivatedAt() >= 0) {
                if (i >= messages.size() - 1 || sequenceMessage.type == EMessageType.RELEASE_OBJECT) {
                    createActivationRectangle(i, toObject);
                    System.out.println("Deactivating to at message " + i);
                }
            } else if (
                    sequenceMessage.type == EMessageType.ASYNC
            ) {
                toObject.setActivatedAt(i);
                System.out.println("Activating to at message " + i);
            }

            // Lines
            Line line = new Line();
            line.setStartX(fromObject.getTranslateX() + cardWidth / 2);
            line.setStartY(currentY);
            if (sequenceMessage.fromObject.equals(sequenceMessage.toObject)) {
                line.setEndX(cardWidth * 1.25);
            } else if (sequenceMessage.type == EMessageType.NEW_OBJECT) {
                line.setEndX(toObject.getTranslateX());
            } else {
                line.setEndX(toObject.getTranslateX() + cardWidth / 2);
            }
            if (line.getEndX() >= line.getStartX()) {
                line.setStartX(line.getStartX() + 5);
                line.setEndX(line.getEndX() - 5);
            } else {
                line.setStartX(line.getStartX() - 5);
                line.setEndX(line.getEndX() + 5);
            }
            line.setEndY(currentY);
            line.setStrokeWidth(2);
            if (sequenceMessage.type == EMessageType.RETURN) {
                line.getStrokeDashArray().addAll(2.0, 4.0);
            }
            nodesToAdd.add(line);

            ArrowHead.EArrowType arrowType = ArrowHead.EArrowType.BASIC;
            if (sequenceMessage.type == EMessageType.SYNC) {
                arrowType = ArrowHead.EArrowType.TRIANGLE_FILLED;
            } else if (sequenceMessage.type == EMessageType.RELEASE_OBJECT) {
                arrowType = ArrowHead.EArrowType.CROSS;
            }
            ArrowHead arrow = new ArrowHead(arrowType);
            arrow.setTranslateY(currentY);
            arrow.setTranslateX(line.getEndX());
            if (fromObject.getTranslateX() >= toObject.getTranslateX()) {
                arrow.setAngle(315);
            } else {
                arrow.setAngle(135);
            }
            nodesToAdd.add(arrow);

            Label label = new Label();
            label.setText(sequenceMessage.content);
            label.setTextAlignment(TextAlignment.CENTER);
            label.setAlignment(Pos.CENTER);
            label.setTranslateX(Math.min(fromObject.getTranslateX(), toObject.getTranslateX()) + cardWidth / 2);
            label.setTranslateY(currentY - 20);
            label.setPrefWidth(Math.abs(line.getStartX() - line.getEndX()));
            anchorScrollPane.getChildren().add(label);

            currentY += spaceHeight;
        }
        anchorScrollPane.getChildren().addAll(nodesToAdd);
    }

    private void createActivationRectangle(int i, UMLSequenceObjectView toObject) {
        Rectangle rectangle = new Rectangle();
        rectangle.setWidth(8);
        rectangle.setStrokeWidth(2);
        rectangle.setStroke(Paint.valueOf("black"));
        rectangle.setFill(Paint.valueOf("white"));
        rectangle.setTranslateX(toObject.getTranslateX() + cardWidth / 2 - 4);
        rectangle.setTranslateY(toObject.getActivatedAt() * spaceHeight + 90);
        rectangle.setHeight((i - toObject.getActivatedAt()) * spaceHeight + 20);
        toObject.setActivatedAt(-1);
        this.anchorScrollPane.getChildren().add(rectangle);
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
                        this.dataModel.setActiveDiagram(null);
                        this.diagramTreeView.getSelectionModel().selectFirst();
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
                String selectedValue = this.classTreeView.getSelectionModel().getSelectedItem().getValue();
                String[] splitValue = selectedValue.split(":");
                String className = splitValue[0];
                String objectName = splitValue[1];
                this.dataModel.executeCommand(new RemoveSequenceDiagramObjectCommand(currentSequence.getID(), objectName, className));
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
            for (ClassDiagram x : this.dataModel.getData().getClasses()) {
                classBox.getItems().add(x.getName());
            }
            classBox.getSelectionModel().selectFirst();

            TextField nameBox = new TextField();
            // disable OK button if text-input is empty
            BooleanBinding validName = Bindings.createBooleanBinding(() -> {
                return nameBox.getText().equals("");
            }, nameBox.textProperty());
            dialog.getDialogPane().lookupButton(createButtonType).disableProperty().bind(validName);

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
                nameBox.setText("New Object");
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
                return dialog.getEditor().getText().equals("");
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
                ArrayList<Pair<String, String>> senderID = new ArrayList<>();
                ArrayList<Pair<String, String>> receiverID = new ArrayList<>();
                for (EMessageType type : EMessageType.values()) {
                    msgType.getItems().add(type.typeToString());
                }
                msgType.getSelectionModel().selectFirst();

                for (SequenceObjects obj : this.dataModel.getData().getSequenceByName(this.selectedDiagram).getObjects()) {
                    sender.getItems().add(obj.getObjectName() + " [" + obj.getClassName() + "]");
                    senderID.add(new Pair<>(obj.getClassName(), obj.getObjectName()));
                }
                sender.getSelectionModel().selectFirst();

                for (SequenceObjects obj : this.dataModel.getData().getSequenceByName(this.selectedDiagram).getObjects()) {
                    receiver.getItems().add(obj.getObjectName() + " [" + obj.getClassName() + "]");
                    receiverID.add(new Pair<>(obj.getClassName(), obj.getObjectName()));
                }
                receiver.getSelectionModel().selectFirst();

                ChoiceBox<String> contentBox = new ChoiceBox<>();
                String selection = sender.getSelectionModel().getSelectedItem();
                String classID = selection.split("\\[", 2)[1].split("]",2)[0];
                for (Methods m : this.dataModel.getData().getClassByName(classID).getMethods()) {
                    contentBox.getItems().add(m.getName());
                }
                if (contentBox.getItems().size() == 0) {
                    contentBox.getItems().add("<no methods>");
                }
                contentBox.getSelectionModel().selectFirst();
                Label receiverLabel = new Label("Receiver: ");

                grid.add(new Label("Message content: "), 0, 0);
                grid.add(contentBox, 1, 0);
                grid.add(new Label("Message type: "), 0, 1);
                grid.add(msgType, 1, 1);
                grid.add(new Label("Sender: "), 0, 2);
                grid.add(sender, 1, 2);
                grid.add(receiverLabel, 0, 3);
                grid.add(receiver, 1, 3);

                TextField f = new TextField();
                grid.add(f, 1, 0);
                f.setVisible(false);

                TextField objNew = new TextField();
                ChoiceBox<String> recNew = new ChoiceBox<>();
                for (ClassDiagram cl : this.dataModel.getData().getClasses()) {
                    recNew.getItems().add(cl.getName());
                }
                recNew.getSelectionModel().selectFirst();
                grid.add(recNew, 1, 3);
                grid.add(objNew, 1, 4);

                objNew.setVisible(false);
                recNew.setVisible(false);
                dialog.getDialogPane().setContent(grid);


                sender.setOnAction(event -> {
                    contentBox.getItems().clear();
                    String selectionX = sender.getSelectionModel().getSelectedItem();
                    String classIDX = selectionX.split("\\[", 2)[1].split("]",2)[0];
                    for (Methods m : this.dataModel.getData().getClassByName(classIDX).getMethods()) {
                        contentBox.getItems().add(m.getName());
                    }
                    if (contentBox.getItems().size() == 0) {
                        contentBox.getItems().add("<no methods>");
                    }
                    contentBox.getSelectionModel().selectFirst();

                    BooleanBinding validName = Bindings.createBooleanBinding(() -> {
                        return contentBox.getItems().get(0).equals("<no methods>");
                    });
                    dialog.getDialogPane().lookupButton(createButtonType).disableProperty().bind(validName);
                });

                msgType.setOnAction(event -> {
                    if (msgType.getSelectionModel().getSelectedItem().equals("Return message")) {
                        f.setVisible(true);
                    } else {
                        f.setVisible(false);
                    }
                    if (msgType.getSelectionModel().getSelectedItem().equals("Object creation")) {
                        objNew.setVisible(true);
                        recNew.setVisible(true);
                        receiver.setVisible(false);
                        receiverLabel.setText("New object: ");
                    } else {
                        objNew.setVisible(false);
                        recNew.setVisible(false);
                        receiver.setVisible(true);
                        receiverLabel.setText("Receiver: ");
                    }
                });
                BooleanBinding validName2 = Bindings.createBooleanBinding(() -> {
                    return ( objNew.isVisible() && objNew.getText().equals("") ) || ( f.isVisible() && f.getText().equals("") );
                });
                dialog.getDialogPane().lookupButton(createButtonType).disableProperty().bind(validName2);


                dialog.setResultConverter(dialogButton -> {
                    if (dialogButton == createButtonType) {
                        ArrayList<String> x = new ArrayList<>();
                        if (f.isVisible())
                            x.add(f.getText());
                        else
                            x.add(contentBox.getSelectionModel().getSelectedItem());
                        x.add(msgType.getSelectionModel().getSelectedItem());
                        x.add(senderID.get(sender.getSelectionModel().getSelectedIndex()).getKey());
                        x.add(senderID.get(sender.getSelectionModel().getSelectedIndex()).getValue());
                        if (recNew.isVisible()) {
                            x.add(recNew.getSelectionModel().getSelectedItem());
                            x.add(objNew.getText());
                        } else {
                            x.add(receiverID.get(receiver.getSelectionModel().getSelectedIndex()).getKey());
                            x.add(receiverID.get(receiver.getSelectionModel().getSelectedIndex()).getValue());
                        }
                        return x;
                    }
                    return null;
                });

                Optional<ArrayList<String>> result = dialog.showAndWait();
                result.ifPresent(returned -> {
                    try {
                        int seqID = this.dataModel.getData().getSequenceByName(this.selectedDiagram).getID();
                        Pair<String, String> senderStr = new Pair<>(returned.get(2), returned.get(3));
                        Pair<String, String> receiverStr = new Pair<>(returned.get(4), returned.get(5));
                        EMessageType retType = stringToEMessageType(returned.get(1));
                        if (returned.get(5).equals("")) {
                            this.showErrorMessage("Unable to add new message", "New object has no name.");
                            return;
                        }
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

    public void handleMessageForward(ActionEvent actionEvent) {
        Integer selectedIndex = this.messageTreeView.getSelectionModel().getSelectedIndex();
        SequenceDiagram thisDiagram = this.dataModel.getData().getSequenceByName(this.selectedDiagram);
        SequenceMessages current = thisDiagram.getMessageByIndex(selectedIndex);
        Integer msgCount = thisDiagram.getMessages().size();

        if (selectedIndex < msgCount-1) {
            try {
                this.dataModel.executeCommand(new EditSequenceDiagramMessageIndexCommand(thisDiagram.getID(), selectedIndex, ++selectedIndex));
                this.updateView();
            } catch (Exception e) {
                showErrorMessage("Unable to move message forward", e.getLocalizedMessage());
                e.printStackTrace();
            }
        } else {
            return;
        }
    }

    public void handleMessageBackward(ActionEvent actionEvent) {
        Integer selectedIndex = this.messageTreeView.getSelectionModel().getSelectedIndex();
        SequenceDiagram thisDiagram = this.dataModel.getData().getSequenceByName(this.selectedDiagram);
        SequenceMessages current = thisDiagram.getMessageByIndex(selectedIndex);
        Integer msgCount = thisDiagram.getMessages().size();

        if (selectedIndex > 0) {
            try {
                this.dataModel.executeCommand(new EditSequenceDiagramMessageIndexCommand(thisDiagram.getID(), selectedIndex, --selectedIndex));
                this.updateView();
            } catch (Exception e) {
                showErrorMessage("Unable to move message backward", e.getLocalizedMessage());
                e.printStackTrace();
            }
        } else {
            return;
        }
    }

    public void handleObjectForward(ActionEvent actionEvent) {
        Integer selectedIndex = this.classTreeView.getSelectionModel().getSelectedIndex();
        SequenceDiagram thisDiagram = this.dataModel.getData().getSequenceByName(this.selectedDiagram);
        SequenceObjects current = thisDiagram.getObjectByIndex(selectedIndex);
        Integer objCount = thisDiagram.getObjects().size();

        if (selectedIndex < objCount-1) {
            try {
                this.dataModel.executeCommand(new EditSequenceDiagramObjectIndexCommand(thisDiagram.getID(), selectedIndex, ++selectedIndex));
                this.updateView();
            } catch (Exception e) {
                showErrorMessage("Unable to move object forward", e.getLocalizedMessage());
                e.printStackTrace();
            }
        } else {
            //System.out.println("not enough objects");
            return;
        }
    }

    public void handleObjectBackward(ActionEvent actionEvent) {
        Integer selectedIndex = this.classTreeView.getSelectionModel().getSelectedIndex();
        SequenceDiagram thisDiagram = this.dataModel.getData().getSequenceByName(this.selectedDiagram);

        if (selectedIndex > 0) {
            try {
                this.dataModel.executeCommand(new EditSequenceDiagramObjectIndexCommand(thisDiagram.getID(), selectedIndex, --selectedIndex));
                this.updateView();
            } catch (Exception e) {
                showErrorMessage("Unable to move object backward", e.getLocalizedMessage());
                e.printStackTrace();
            }
        } else {
            //System.out.println("not enough objects");
            return;
        }
    }

    public void handleProperties(TreeItem<String> selected) {
        try {
            propertiesView.setDataModel(this.dataModel);
            propertiesView.setClassTreeView(this.classTreeView);
            propertiesView.setMessagesTreeView(this.messageTreeView);
            SequenceDiagram current = this.dataModel.getData().getSequenceByName(this.selectedDiagram);
            if (this.classTreeView.getSelectionModel().getSelectedItem() != null) {
                propertiesView.resetProperties();
                propertiesView.setGroupType(EPropertyType.SEQ_OBJECT);
                String selectedValue = this.classTreeView.getSelectionModel().getSelectedItem().getValue();
                String[] splitValue = selectedValue.split(":");
                String objectName = splitValue[0];
                String className = splitValue[1];
                SequenceObjects object = current.getObject(className, objectName);
                propertiesView.setParentID(current.getID());
                propertiesView.setID(object.getObjectName() + ":" + object.getClassName());
                propertiesView.addPropertyLine("Object", objectName);
                propertiesView.addPropertyLine("Class instance", className);
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
                propertiesView.addPropertyLine("From", fromObject.getValue() + ":" + fromObject.getKey());
                propertiesView.addPropertyLine("To", toObject.getValue() + ":" + toObject.getKey());
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

    // checks all inconsitencies of sequence diagram
    // if there is inconsitency, sets check result to false!
    public void diagramCheck() {
        SequenceDiagram currentDiagram = this.dataModel.getData().getSequenceByName(this.selectedDiagram);
        ArrayList<SequenceMessages> allMessages = currentDiagram.getMessages();
        ArrayList<SequenceObjects> allObjects = currentDiagram.getObjects();
        ErrorCheckClass errorClass = this.dataModel.getErrorClass();
        ErrorCheckSequenceItem error = null;
        ESequenceCheckError errorType = null;
        String errorContent = null;

        // clear all errors
        errorClass.solveSequenceErrors();

        // Messages: type=return only opposite direction of other (last) messages
        // + must be response to something (atleast 1 message was received)
        int index = 0;
        for (SequenceMessages msg : allMessages) {
            if (msg.getType() == EMessageType.RETURN) {
                boolean returnOk = false;
                if (index != 0) {
                    for (SequenceMessages msg2 : allMessages) {
                        if (msg2.getReceiver().equals(msg.getSender()) && msg2.getSender().equals(msg.getReceiver()) && msg2.getType() != EMessageType.RETURN) {
                            returnOk = true;
                            break;
                        }
                    }
                    if (!returnOk) {
                        errorType = ESequenceCheckError.MSG_RET_DIRECTION;
                        errorContent = errorClass.generateErrorContent(currentDiagram.getName(), EElementType.SEQ_MESSAGE, msg.getID());
                        error = new ErrorCheckSequenceItem(errorType, errorContent);
                        errorClass.addSequenceError(error);
                    }
                }
            }
            index++;
        }

        // Class instance not existing anymore (but exists in sequence diagram object) because:
        //  1) user removed class from class diagram
        //  2) file load failed
        String className;
        for (SequenceObjects obj : allObjects) {
            className = obj.getClassName();
            if (this.dataModel.getData().getClassByName(className) == null) {
                errorType = ESequenceCheckError.OBJ_CLASS_NOEXISTENT;
                errorContent = errorClass.generateErrorContent(currentDiagram.getName(), EElementType.SEQ_OBJECT, obj.getObjectClassName());
                error = new ErrorCheckSequenceItem(errorType, errorContent);
                errorClass.addSequenceError(error);
            }
        }

        // Messages: non return type can be only send from current object to another (?)
        // (not response)
        /**
        int index = 0;
        for (SequenceMessages msg : allMessages) {
            if (msg.getType() != EMessageType.RETURN) { // TODO
                errorType = ESequenceCheckError.MSG_NONRET_DIRECTION;
                errorContent = errorClass.generateErrorContent(currentDiagram.getName(), EElementType.SEQ_MESSAGE, msg.getID());
                error = new ErrorCheckSequenceItem(errorType, errorContent);
                errorClass.addSequenceError(error);
            }
            index++;
        } **/

        this.dataModel.getErrorClass().printSequenceErrors();
    }
}

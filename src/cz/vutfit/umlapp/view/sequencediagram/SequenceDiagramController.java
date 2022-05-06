/*
 * File: SequenceDiagramController.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

/*
 * File: SequenceDiagramController.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.view.sequencediagram;

import cz.vutfit.umlapp.model.DataModel;
import cz.vutfit.umlapp.model.ModelFactory;
import cz.vutfit.umlapp.model.commands.*;
import cz.vutfit.umlapp.model.uml.*;
import cz.vutfit.umlapp.view.ViewHandler;
import cz.vutfit.umlapp.view.components.ArrowHead;
import cz.vutfit.umlapp.view.components.EPropertyType;
import cz.vutfit.umlapp.view.components.PropertiesView;
import cz.vutfit.umlapp.view.components.UMLSequenceObjectView;
import cz.vutfit.umlapp.view.main.EDataType;
import cz.vutfit.umlapp.view.main.MainController;
import cz.vutfit.umlapp.view.main.TreeViewDataHolder;
import cz.vutfit.umlapp.view.main.TreeViewItemModel;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
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
    public TreeView<TreeViewDataHolder> classTreeView;

    @FXML
    public TreeView<TreeViewDataHolder> messageTreeView;

    @FXML
    public PropertiesView propertiesView;

    @FXML
    public HBox boxMessageOptions;

    @FXML
    public HBox boxClassOptions;

    private String selectedDiagram;
    private String selectedClass;
    private String selectedMessage;

    private final double cardWidth = 150;
    private final double spaceWidth = 210;
    private final double spaceHeight = 40;

    ChangeListener<TreeItem<TreeViewDataHolder>> handleObjectSelection = (observableValue, oldItem, newItem) -> {
        if (newItem != null) {
            this.selectedClass = newItem.getValue().getSequenceObject().getObjectClassName();
            this.messageTreeView.getSelectionModel().clearSelection();
            handleProperties();
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

    ChangeListener<TreeItem<TreeViewDataHolder>> handleMessageSelection = (observableValue, oldItem, newItem) -> {
        if (newItem != null) {
            this.selectedMessage = newItem.getValue().getSequenceMessage().getContent();
            this.classTreeView.getSelectionModel().clearSelection();
            handleProperties();
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
        this.classTreeView.setCellFactory(TreeViewDataHolder.getCellFactory(this.dataModel));
        this.messageTreeView.setCellFactory(TreeViewDataHolder.getCellFactory(this.dataModel));
        this.classTreeView.getSelectionModel().selectedItemProperty().addListener(handleObjectSelection);
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
            // Classes (objects) menu
            TreeViewItemModel classes = new TreeViewItemModel(this.dataModel, classTreeView, EDataType.SEQ_OBJECTS);

            this.selectedDiagram = this.dataModel.getActiveDiagram();

            classes.setSelectedSequence(this.selectedDiagram);
            classes.buildTree();
            classes.rootViewUpdate();

            // Messages menu
            TreeViewItemModel messages = new TreeViewItemModel(this.dataModel, messageTreeView, EDataType.SEQ_MESSAGES);
            messages.setSelectedSequence(this.selectedDiagram);
            messages.buildTree();
            messages.rootViewUpdate();

            // Handle inconsistencies
            this.dataModel.getErrorClass().checkSequenceDiagram(this.selectedDiagram);

            this.initDiagram();

            boxMessageOptions.setVisible(this.selectedMessage != null);
            boxClassOptions.setVisible(this.selectedClass != null);
        } catch (Exception e) {
            this.showErrorMessage(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void populateMethodsContentBox(DataModel dataModel, ChoiceBox<String> methodsContentBox, String className) {
        ClassDiagram classDiagram = dataModel.getData().getClassByName(className);
        for (Methods m : classDiagram.getMethods()) {
            methodsContentBox.getItems().add(m.getName());
        }
        // Get inherited methods
        for (Relationships r : dataModel.getData().getRelationships()) {
            if (r.getToClassID() == classDiagram.getID() && r.getType() == ERelationType.GENERALIZATION) {
                for (Methods m : dataModel.getData().getClassByID(r.getFromClassID()).getMethods()) {
                    methodsContentBox.getItems().add(m.getName());
                }
            }
        }
        if (methodsContentBox.getItems().size() == 0) {
            methodsContentBox.getItems().add("<no methods>");
        }
        methodsContentBox.getSelectionModel().selectFirst();
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
                if (Objects.equals(sequenceMessage.getReceiverString(), sequenceObject.getObjectClassName())) {
                    if (activatedAt == -1 && sequenceMessage.type == EMessageType.NEW_OBJECT) {
                        activatedAt = i;
                    }
                    if (sequenceMessage.type == EMessageType.RELEASE_OBJECT) {
                        destroyedAt = i;
                    }
                }
            }
            boolean hasObjectError = this.dataModel.getErrorClass().isSeqObjectCorrect(selectedDiagram, sequenceObject.getObjectClassName());

            // Lifeline
            double startPos = 10;
            if (activatedAt >= 0) {
                startPos = 100 + activatedAt * spaceHeight;
            }
            double endPos = 200 + diagram.getMessages().size() * spaceHeight;
            if (destroyedAt >= 0) {
                endPos = 100 + (destroyedAt + 1) * spaceHeight;
            }
            Line line = new Line();
            line.setStartX(10 + cardWidth / 2 + currentX);
            line.setStartY(startPos);
            line.setEndX(10 + cardWidth / 2 + currentX);
            line.setEndY(endPos);
            line.setStrokeWidth(2);
            line.setStroke(Paint.valueOf(hasObjectError ? "red" : "#aaaaaa"));
            line.getStrokeDashArray().addAll(2.0, 4.0);
            anchorScrollPane.getChildren().add(line);

            // Object
            UMLSequenceObjectView node = new UMLSequenceObjectView(sequenceObject);
            node.setTranslateX(10 + currentX);
            node.setTranslateY(startPos);
            node.setHasError(hasObjectError);
            anchorScrollPane.getChildren().add(node);
            objectsViewMap.put(sequenceObject.getObjectClassName(), node);
            currentX += spaceWidth;
        }

        ArrayList<SequenceMessages> messages = diagram.getMessages();
        List<Node> nodesToAdd = new ArrayList<>();
        for (int i = 0; i < messages.size(); i++) {
            SequenceMessages sequenceMessage = messages.get(i);
            UMLSequenceObjectView fromObject = objectsViewMap.get(sequenceMessage.getSenderString());
            UMLSequenceObjectView toObject = objectsViewMap.get(sequenceMessage.getReceiverString());
            if (fromObject == null || toObject == null) continue;
            boolean hasMessageError = this.dataModel.getErrorClass().isSeqMessageCorrect(selectedDiagram, sequenceMessage.getID());
            Paint primaryColor = Paint.valueOf(hasMessageError ? "red" : "black");

            // Activation boxes
            if (fromObject.getActivatedAt() >= 0) {
                if (i >= messages.size() - 1 || sequenceMessage.type == EMessageType.RELEASE_OBJECT || sequenceMessage.type == EMessageType.RETURN) {
                    boolean hasObjectError = this.dataModel.getErrorClass().isSeqObjectCorrect(selectedDiagram, sequenceMessage.getSenderString());
                    createActivationRectangle(i, fromObject, hasObjectError);
                }
            } else if (
                    i == 0 ||
                            sequenceMessage.type == EMessageType.NEW_OBJECT ||
                            sequenceMessage.type == EMessageType.SYNC ||
                            sequenceMessage.type == EMessageType.ASYNC
            ) {
                fromObject.setActivatedAt(i);
            }
            if (toObject.getActivatedAt() >= 0) {
                if (i >= messages.size() - 1 || sequenceMessage.type == EMessageType.RELEASE_OBJECT) {
                    boolean hasObjectError = this.dataModel.getErrorClass().isSeqObjectCorrect(selectedDiagram, sequenceMessage.getReceiverString());
                    createActivationRectangle(i, toObject, hasObjectError);
                }
            } else if (
                    sequenceMessage.type == EMessageType.ASYNC
            ) {
                toObject.setActivatedAt(i);
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
            line.setStroke(primaryColor);
            line.setStrokeWidth(2);
            if (sequenceMessage.type == EMessageType.RETURN) {
                line.getStrokeDashArray().addAll(2.0, 4.0);
            }
            nodesToAdd.add(line);

            ArrowHead.EArrowType arrowType = ArrowHead.EArrowType.BASIC;
            if (sequenceMessage.type == EMessageType.SYNC || sequenceMessage.type == EMessageType.ASYNC) {
                arrowType = ArrowHead.EArrowType.TRIANGLE_FILLED;
            } else if (sequenceMessage.type == EMessageType.RELEASE_OBJECT) {
                arrowType = ArrowHead.EArrowType.CROSS;
            }
            ArrowHead arrow = new ArrowHead(arrowType, primaryColor);
            arrow.setTranslateY(currentY);
            arrow.setTranslateX(line.getEndX());
            if (fromObject.getTranslateX() >= toObject.getTranslateX()) {
                arrow.setAngle(315);
            } else {
                arrow.setAngle(135);
            }
            nodesToAdd.add(arrow);

            Label label = new Label();
            label.setText(sequenceMessage.getContent());
            label.setTextAlignment(TextAlignment.CENTER);
            label.setTextFill(primaryColor);
            label.setAlignment(Pos.CENTER);
            label.setTranslateX(Math.min(fromObject.getTranslateX(), toObject.getTranslateX()) + cardWidth / 2);
            label.setTranslateY(currentY - 20);
            label.setPrefWidth(Math.abs(line.getStartX() - line.getEndX()));
            int finalI = i;
            label.setOnMouseClicked(event -> {
                messageTreeView.getSelectionModel().clearAndSelect(finalI);
            });
            anchorScrollPane.getChildren().add(label);

            currentY += spaceHeight;
        }
        anchorScrollPane.getChildren().addAll(nodesToAdd);
    }

    public void handleRemoveDiagram() {
        try {
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
                }
            });
        } catch(Exception e){
            this.showErrorMessage(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public void handleRemoveObject() {
        try {
            SequenceDiagram currentSequence = this.dataModel.getData().getSequenceByName(this.selectedDiagram);
            ArrayList<SequenceObjects> objects = currentSequence.getObjects();

            // if no objects present, do nothing
            if (objects.size() == 0)
                return;

            try {
                SequenceObjects selectedObject = this.classTreeView.getSelectionModel().getSelectedItem().getValue().getSequenceObject();
                this.dataModel.executeCommand(new RemoveSequenceDiagramObjectCommand(currentSequence.getID(), selectedObject));
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

    public void handleAddClass() {
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
            // Reference shows it's possible to have empty name
            /* BooleanBinding validName = Bindings.createBooleanBinding(() -> {
                return nameBox.getText().equals("");
            }, nameBox.textProperty());
            dialog.getDialogPane().lookupButton(createButtonType).disableProperty().bind(validName);*/

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
                        return new ArrayList<>();
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

    public void handleRemoveMessage() {
        try {
            SequenceDiagram currentSequence = this.dataModel.getData().getSequenceByName(this.selectedDiagram);
            ArrayList<SequenceMessages> messages = currentSequence.getMessages();

            // if no messages present, do nothing
            if (messages.size() == 0)
                return;

            try {
                int messageIndex = this.messageTreeView.getSelectionModel().getSelectedIndex();
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

    private void createActivationRectangle(int i, UMLSequenceObjectView toObject, boolean hasObjectError) {
        Rectangle rectangle = new Rectangle();
        rectangle.setWidth(8);
        rectangle.setStrokeWidth(2);
        rectangle.setStroke(Paint.valueOf(hasObjectError ? "red" : "black"));
        rectangle.setFill(Paint.valueOf("white"));
        rectangle.setTranslateX(toObject.getTranslateX() + cardWidth / 2 - 4);
        rectangle.setTranslateY(toObject.getActivatedAt() * spaceHeight + 90);
        rectangle.setHeight((i - toObject.getActivatedAt()) * spaceHeight + 20);
        toObject.setActivatedAt(-1);
        this.anchorScrollPane.getChildren().add(rectangle);
    }

    public void handleAddMessage() {
        try {
            if (this.dataModel.getData().getSequenceByName(this.selectedDiagram).getObjects().size() == 0) {
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
                            handleAddClass();
                        } catch (Exception e) {
                            this.showErrorMessage(e.getLocalizedMessage());
                            e.printStackTrace();
                        }
                    }
                });
                return;
            }
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
                String objectName = obj.getObjectName() + " [" + obj.getClassName() + "]";
                Pair<String, String> objectID = new Pair<>(obj.getClassName(), obj.getObjectName());
                sender.getItems().add(objectName);
                senderID.add(objectID);
                receiver.getItems().add(objectName);
                receiverID.add(objectID);
            }
            sender.getSelectionModel().selectFirst();
            receiver.getSelectionModel().selectFirst();

            ChoiceBox<String> methodsContentBox = new ChoiceBox<>();
            String selection = sender.getSelectionModel().getSelectedItem();
            String classID = selection.split("\\[", 2)[1].split("]", 2)[0];
            TextField arguments = new TextField();
            Label argumentsLabel = new Label("Arguments: ");

            populateMethodsContentBox(this.dataModel, methodsContentBox, classID);

            Label receiverLabel = new Label("Receiver: ");
            grid.add(new Label("Message content: "), 0, 0);
            grid.add(methodsContentBox, 1, 0);
            grid.add(argumentsLabel, 2, 0);
            grid.add(arguments, 3, 0);
            grid.add(new Label("Message type: "), 0, 1);
            grid.add(msgType, 1, 1);
            grid.add(new Label("Sender: "), 0, 2);
            grid.add(sender, 1, 2);
            grid.add(receiverLabel, 0, 3);
            grid.add(receiver, 1, 3);

            TextField returnMessageText = new TextField();
            returnMessageText.setVisible(false);
            grid.add(returnMessageText, 1, 0);

            dialog.getDialogPane().setContent(grid);

            receiver.setOnAction(event -> {
                methodsContentBox.getItems().clear();
                String selectionX = receiver.getSelectionModel().getSelectedItem();
                String classIDX = selectionX.split("\\[", 2)[1].split("]", 2)[0];
                populateMethodsContentBox(this.dataModel, methodsContentBox, classIDX);
            });
            msgType.setOnAction(event -> {
                boolean isReturnMessage = msgType.getSelectionModel().getSelectedItem().equals("Return message");
                returnMessageText.setVisible(isReturnMessage);
                argumentsLabel.setVisible(!isReturnMessage);
                arguments.setVisible(!isReturnMessage);
            });

            BooleanBinding validation = Bindings.createBooleanBinding(() -> {
                System.out.println("Binding called");
                if (returnMessageText.isVisible()) {
                    return returnMessageText.getText().equals("");
                }
                return methodsContentBox.getSelectionModel().getSelectedItem().equals("<no methods>");
            }, returnMessageText.visibleProperty(), returnMessageText.textProperty(), methodsContentBox.getSelectionModel().selectedItemProperty());
            dialog.getDialogPane().lookupButton(createButtonType).disableProperty().bind(validation);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == createButtonType) {
                    ArrayList<String> x = new ArrayList<>();
                    if (returnMessageText.isVisible())
                        x.add(returnMessageText.getText());
                    else
                        x.add(methodsContentBox.getSelectionModel().getSelectedItem());
                    x.add(msgType.getSelectionModel().getSelectedItem());
                    x.add(senderID.get(sender.getSelectionModel().getSelectedIndex()).getKey());
                    x.add(senderID.get(sender.getSelectionModel().getSelectedIndex()).getValue());
                    x.add(receiverID.get(receiver.getSelectionModel().getSelectedIndex()).getKey());
                    x.add(receiverID.get(receiver.getSelectionModel().getSelectedIndex()).getValue());
                    x.add(arguments.getText());
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
                    EMessageType retType = EMessageType.fromString(returned.get(1));
                    String args = returned.get(6);
                    String txt = returned.get(0);
                    if (!args.equals("") && retType != EMessageType.RETURN) {
                        txt = txt.split("\\(", 2)[0] + "(" + args + ")";
                    }
                    this.dataModel.executeCommand(new AddSequenceDiagramMessageCommand(seqID, txt, senderStr, receiverStr, retType));
                    this.updateView();
                } catch (Exception ex) {
                    this.showErrorMessage("Unable to add new message to sequence diagram", ex.getLocalizedMessage());
                    ex.printStackTrace();
                }
            });
        } catch (Exception ex) {
            this.showErrorMessage("Unable to add new message", ex.getLocalizedMessage());
            ex.printStackTrace();
        }
    }

    public void handleMessageForward() {
        int selectedIndex = this.messageTreeView.getSelectionModel().getSelectedIndex();
        SequenceDiagram thisDiagram = this.dataModel.getData().getSequenceByName(this.selectedDiagram);
        int msgCount = thisDiagram.getMessages().size();

        if (selectedIndex < msgCount - 1) {
            try {
                this.dataModel.executeCommand(new EditSequenceDiagramMessageIndexCommand(thisDiagram.getID(), selectedIndex, ++selectedIndex));
                this.updateView();
            } catch (Exception e) {
                showErrorMessage("Unable to move message forward", e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
    }

    public void handleMessageBackward() {
        int selectedIndex = this.messageTreeView.getSelectionModel().getSelectedIndex();
        SequenceDiagram thisDiagram = this.dataModel.getData().getSequenceByName(this.selectedDiagram);

        if (selectedIndex > 0) {
            try {
                this.dataModel.executeCommand(new EditSequenceDiagramMessageIndexCommand(thisDiagram.getID(), selectedIndex, --selectedIndex));
                this.updateView();
            } catch (Exception e) {
                showErrorMessage("Unable to move message backward", e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
    }

    public void handleObjectForward() {
        int selectedIndex = this.classTreeView.getSelectionModel().getSelectedIndex();
        SequenceDiagram thisDiagram = this.dataModel.getData().getSequenceByName(this.selectedDiagram);
        int objCount = thisDiagram.getObjects().size();

        if (selectedIndex < objCount - 1) {
            try {
                this.dataModel.executeCommand(new EditSequenceDiagramObjectIndexCommand(thisDiagram.getID(), selectedIndex, ++selectedIndex));
                this.updateView();
            } catch (Exception e) {
                showErrorMessage("Unable to move object forward", e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
    }

    public void handleObjectBackward() {
        int selectedIndex = this.classTreeView.getSelectionModel().getSelectedIndex();
        SequenceDiagram thisDiagram = this.dataModel.getData().getSequenceByName(this.selectedDiagram);

        if (selectedIndex > 0) {
            try {
                this.dataModel.executeCommand(new EditSequenceDiagramObjectIndexCommand(thisDiagram.getID(), selectedIndex, --selectedIndex));
                this.updateView();
            } catch (Exception e) {
                showErrorMessage("Unable to move object backward", e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
    }

    public void handleProperties() {
        try {
            propertiesView.setDataModel(this.dataModel);
            propertiesView.setClassTreeView(this.classTreeView);
            propertiesView.setMessagesTreeView(this.messageTreeView);
            SequenceDiagram current = this.dataModel.getData().getSequenceByName(this.selectedDiagram);
            if (this.classTreeView.getSelectionModel().getSelectedItem() != null) {
                propertiesView.resetProperties();
                propertiesView.setGroupType(EPropertyType.SEQ_OBJECT);
                SequenceObjects object = this.classTreeView.getSelectionModel().getSelectedItem().getValue().getSequenceObject();
                propertiesView.setParentID(current.getID());
                propertiesView.setID(object.getObjectName() + ":" + object.getClassName());
                propertiesView.addPropertyLine("Object", object.getObjectName());
                propertiesView.addPropertyLine("Class instance", object.getClassName());
                propertiesView.addPropertyLine("Status", object.getActiveStatusString());
            } else if (this.messageTreeView.getSelectionModel().getSelectedItem() != null) {
                SequenceMessages message = current.getMessageByIndex(this.messageTreeView.getSelectionModel().getSelectedIndex());
                propertiesView.resetProperties();
                propertiesView.setGroupType(EPropertyType.SEQ_MESSAGE);
                propertiesView.setParentID(current.getID());
                propertiesView.setID(message.getID());
                propertiesView.addPropertyLine("Message", this.selectedMessage);
                propertiesView.addPropertyLine("From", message.getSenderString());
                propertiesView.addPropertyLine("To", message.getReceiverString());
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

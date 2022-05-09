/*
 * File: TreeViewDataHolder.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.view.main;

import cz.vutfit.umlapp.model.DataModel;
import cz.vutfit.umlapp.model.uml.*;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.util.Callback;

/**
 * Class that holds data content of individual TreeItems
 */
public class TreeViewDataHolder {
    private final EDataType dataType;

    private SequenceDiagram sequenceDiagram;
    private ClassDiagram classDiagram;
    private Attributes attribute;
    private Methods method;
    private Relationships relationship;
    private SequenceObjects sequenceObject;
    private SequenceMessages sequenceMessage;
    private Integer sequenceMessageIndex;

    /**
     * Create a TreeViewDataHolder with null content
     *
     * @param dataType type of data that is represented
     */
    public TreeViewDataHolder(EDataType dataType) {
        this.dataType = dataType;
    }

    /**
     * Create a TreeViewDataHolder with sequence diagram
     *
     * @param sequenceDiagram sequence diagram to represent
     */
    public TreeViewDataHolder(SequenceDiagram sequenceDiagram) {
        this.dataType = EDataType.DIAGRAM;
        this.sequenceDiagram = sequenceDiagram;
    }

    /**
     * Create a TreeViewDataHolder with class
     *
     * @param classDiagram class
     */
    public TreeViewDataHolder(ClassDiagram classDiagram) {
        this.dataType = EDataType.CLASS;
        this.classDiagram = classDiagram;
    }

    /**
     * Create a TreeViewDataHolder with an attribute
     *
     * @param attribute attribute object
     */
    public TreeViewDataHolder(Attributes attribute) {
        this.dataType = EDataType.ATTRIBUTE;
        this.attribute = attribute;
    }

    /**
     * Create a TreeViewDataHolder with a method
     *
     * @param method method object
     */
    public TreeViewDataHolder(Methods method) {
        this.dataType = EDataType.METHOD;
        this.method = method;
    }

    /**
     * Create a TreeViewDataHolder with
     *
     * @param relationship relationship object
     * @param classDiagram parent class
     */
    public TreeViewDataHolder(Relationships relationship, ClassDiagram classDiagram) {
        this.dataType = EDataType.RELATIONSHIP;
        this.classDiagram = classDiagram;
        this.relationship = relationship;
    }

    /**
     * Create a TreeViewDataHolder with a sequence object
     *
     * @param sequenceObject sequence object
     */
    public TreeViewDataHolder(SequenceObjects sequenceObject) {
        this.dataType = EDataType.SEQ_OBJECTS;
        this.sequenceObject = sequenceObject;
    }

    /**
     * Create a TreeViewDataHolder with a message
     *
     * @param sequenceMessage      message object
     * @param sequenceMessageIndex index of the message
     */
    public TreeViewDataHolder(SequenceMessages sequenceMessage, Integer sequenceMessageIndex) {
        this.dataType = EDataType.SEQ_MESSAGES;
        this.sequenceMessage = sequenceMessage;
        this.sequenceMessageIndex = sequenceMessageIndex;
    }

    /**
     * Get cell factory that produces TreeCells with the correct label
     *
     * @param dataModel linked data model
     * @return CellFactory interface
     */
    public static Callback<TreeView<TreeViewDataHolder>, TreeCell<TreeViewDataHolder>> getCellFactory(DataModel dataModel) {
        return tv -> new TreeCell<>() {
            @Override
            protected void updateItem(TreeViewDataHolder item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(null);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.getLabel(dataModel));
                }
            }
        };
    }

    /**
     * Helper method to get the currently selected class from a tree view
     *
     * @param treeView tree view to search
     * @return class object
     */
    public static ClassDiagram getTreeViewSelectedClass(TreeView<TreeViewDataHolder> treeView) {
        TreeItem<TreeViewDataHolder> selectedTreeItem;
        if (treeView.getSelectionModel().getSelectedItem() != null) { // no item selected / 0 items in tree-view
            selectedTreeItem = treeView.getSelectionModel().getSelectedItem();
        } else {
            return null;
        }
        if (selectedTreeItem.getValue().getDataType() == EDataType.CLASS) {
            return selectedTreeItem.getValue().getClassDiagram();
        } else if (selectedTreeItem.getParent() != null && selectedTreeItem.getParent().getValue().getDataType() == EDataType.CLASS) {
            // if not selected class but some attribute/method/relationship in that class
            return selectedTreeItem.getParent().getValue().getClassDiagram();
        }
        return null;
    }

    /**
     * Get a label to display in the TreeView
     *
     * @param dataModel data model
     * @return label to display
     */
    public String getLabel(DataModel dataModel) {
        if (this.dataType == EDataType.DIAGRAM) {
            if (this.sequenceDiagram == null) {
                return "Class diagram";
            }
            return sequenceDiagram.getName();
        } else if (this.dataType == EDataType.CLASS) {
            return classDiagram.getName();
        } else if (this.dataType == EDataType.ATTRIBUTE) {
            return attribute.getNameWithPrefix();
        } else if (this.dataType == EDataType.METHOD) {
            return method.getNameWithPrefix();
        } else if (this.dataType == EDataType.RELATIONSHIP) {
            int classID = this.classDiagram.getID();
            if (relationship.getFromClassID() == classID && relationship.getToClassID() == classID) {
                return ">< " + classDiagram.getName();
            } else if (relationship.getToClassID() == classID) {
                if (dataModel == null) return "< " + relationship.getFromClassID();
                return "< " + dataModel.getData().getClassByID(relationship.getFromClassID()).getName();
            } else if (relationship.getFromClassID() == classID) {
                if (dataModel == null) return "> " + relationship.getToClassID();
                return "> " + dataModel.getData().getClassByID(relationship.getToClassID()).getName();
            }
        } else if (this.dataType == EDataType.SEQ_OBJECTS) {
            return sequenceObject.getObjectName() + ":" + sequenceObject.getClassName();
        } else if (this.dataType == EDataType.SEQ_MESSAGES) {
            return sequenceMessageIndex + ". " + sequenceMessage.getContent();
        }
        return null;
    }

    public TreeItem<TreeViewDataHolder> getTreeItem() {
        return new TreeItem<>(this);
    }

    public EDataType getDataType() {
        return dataType;
    }

    public SequenceDiagram getSequenceDiagram() {
        return sequenceDiagram;
    }

    public ClassDiagram getClassDiagram() {
        return classDiagram;
    }

    public Attributes getAttribute() {
        return attribute;
    }

    public Methods getMethod() {
        return method;
    }

    public Relationships getRelationship() {
        return relationship;
    }

    public SequenceObjects getSequenceObject() {
        return sequenceObject;
    }

    public SequenceMessages getSequenceMessage() {
        return sequenceMessage;
    }

    public String toString() {
        return getLabel(null);
    }
}

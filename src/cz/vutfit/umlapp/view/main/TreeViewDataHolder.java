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

    public TreeViewDataHolder(EDataType dataType) {
        this.dataType = dataType;
    }

    public TreeViewDataHolder(SequenceDiagram sequenceDiagram) {
        this.dataType = EDataType.DIAGRAM;
        this.sequenceDiagram = sequenceDiagram;
    }

    public TreeViewDataHolder(ClassDiagram classDiagram) {
        this.dataType = EDataType.CLASS;
        this.classDiagram = classDiagram;
    }

    public TreeViewDataHolder(Attributes attribute) {
        this.dataType = EDataType.ATTRIBUTE;
        this.attribute = attribute;
    }

    public TreeViewDataHolder(Methods method) {
        this.dataType = EDataType.METHOD;
        this.method = method;
    }

    public TreeViewDataHolder(Relationships relationship, ClassDiagram classDiagram) {
        this.dataType = EDataType.RELATIONSHIP;
        this.classDiagram = classDiagram;
        this.relationship = relationship;
    }

    public TreeViewDataHolder(SequenceObjects sequenceObject) {
        this.dataType = EDataType.SEQ_OBJECTS;
        this.sequenceObject = sequenceObject;
    }

    public TreeViewDataHolder(SequenceMessages sequenceMessage, Integer sequenceMessageIndex) {
        this.dataType = EDataType.SEQ_MESSAGES;
        this.sequenceMessage = sequenceMessage;
        this.sequenceMessageIndex = sequenceMessageIndex;
    }

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
                if (dataModel == null) return "< " + relationship.getToClassID();
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

/*
 * File: TreeViewItemModel.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.view.main;

import cz.vutfit.umlapp.model.DataModel;
import cz.vutfit.umlapp.model.uml.*;
import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.util.HashSet;
import java.util.Set;

/**
 * Class for TreeViewItem used for working with TreeViews and its items
 */
public class TreeViewItemModel {
    /**
     * TreeView
     */
    public final TreeView<TreeViewDataHolder> view;
    /**
     * Type of the TreeItem @see EDataType
     */
    public final EDataType itemType;
    /**
     * Data model
     */
    public final DataModel dataModel;
    /**
     * Root of TreeView
     */
    public TreeItem<TreeViewDataHolder> root;
    /**
     * Currently selected sequence diagram
     */
    public SequenceDiagram selectedSequence;

    /**
     * Constructor
     *
     * @param model    data model
     * @param view     TreeView to use
     * @param itemType type of TreeItem
     * @see EDataType
     */
    public TreeViewItemModel(DataModel model, TreeView<TreeViewDataHolder> view, EDataType itemType) {
        this.view = view;
        this.dataModel = model;
        this.root = new TreeItem<>();
        this.itemType = itemType;
    }

    /**
     * This method handles showing (creating and adding to rootItem) TreeItems depending on TreeItem type.
     */
    public void buildTree() {
        root = new TreeItem<>();
        switch (this.itemType) {
            case DIAGRAM:
                root.getChildren().add(new TreeViewDataHolder(EDataType.DIAGRAM).getTreeItem());
                for (SequenceDiagram diagram : this.dataModel.getData().getSequenceDiagrams()) {
                    root.getChildren().add(new TreeViewDataHolder(diagram).getTreeItem());
                }
                break;
            case CLASS:
                for (ClassDiagram classDiagram : this.dataModel.getData().getClasses()) {
                    TreeItem<TreeViewDataHolder> classItem = new TreeViewDataHolder(classDiagram).getTreeItem();
                    for (Attributes attributes : classDiagram.getAttribs()) {
                        classItem.getChildren().add(new TreeViewDataHolder(attributes).getTreeItem());
                    }
                    for (Methods methods : classDiagram.getMethods()) {
                        classItem.getChildren().add(new TreeViewDataHolder(methods).getTreeItem());
                    }
                    for (Relationships relations : this.dataModel.getData().getRelationships()) {
                        if (
                                (relations.getFromClassID() == classDiagram.getID() || relations.getToClassID() == classDiagram.getID())
                                        && dataModel.getData().getClassByID(relations.getFromClassID()) != null
                                        && dataModel.getData().getClassByID(relations.getToClassID()) != null
                        ) {
                            classItem.getChildren().add(new TreeViewDataHolder(relations, classDiagram).getTreeItem());
                        }
                    }
                    root.getChildren().add(classItem);
                }
                break;
            case SEQ_OBJECTS:
                if (this.selectedSequence == null)
                    return;
                for (SequenceObjects object : this.selectedSequence.getObjects()) {
                    root.getChildren().add(new TreeViewDataHolder(object).getTreeItem());
                }
                break;
            case SEQ_MESSAGES:
                if (this.selectedSequence == null)
                    return;
                Integer i = 1;
                for (SequenceMessages message : this.selectedSequence.getMessages()) {
                    this.root.getChildren().add(new TreeViewDataHolder(message, i).getTreeItem());
                    i++;
                }
                break;
        }
    }

    public void setSelectedSequence(String ID) {
        this.selectedSequence = this.dataModel.getData().getSequenceByName(ID);
    }

    /**
     * Used for updating root TreeItem and TreeView to correctly display all items in view.
     */
    public void rootViewUpdate() {
        // Remember expanded items
        if (this.view.getRoot() != null && this.root != null) {
            Set<String> previousExpanded = new HashSet<>();
            this.view.getRoot().getChildren().forEach(child -> {
                if (child.isExpanded()) {
                    previousExpanded.add(child.getValue().toString());
                }
            });
            root.getChildren().forEach(child -> {
                if (previousExpanded.contains(child.getValue().toString())) {
                    child.setExpanded(true);
                }
            });
        }
        // Remember selection
        int selectedIndex = this.view.getSelectionModel().getSelectedIndex();

        this.view.setShowRoot(false);
        this.view.setRoot(root);

        // Restore selection, diagrams are handled in MainController
        if (selectedIndex >= 0 && this.itemType != EDataType.DIAGRAM) {
            Platform.runLater(() -> {
                this.view.getSelectionModel().clearAndSelect(selectedIndex);
            });
        }
    }
}

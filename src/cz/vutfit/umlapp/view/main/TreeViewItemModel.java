/*
 * File: TreeViewItemModel.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.view.main;

import cz.vutfit.umlapp.model.DataModel;
import cz.vutfit.umlapp.model.uml.Attributes;
import cz.vutfit.umlapp.model.uml.ClassDiagram;
import cz.vutfit.umlapp.model.uml.Methods;
import cz.vutfit.umlapp.model.uml.Relationships;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.util.HashSet;
import java.util.Set;

/**
 * Class for TreeViewItem used for working with TreeViews and its items
 */
public class TreeViewItemModel {
    public TreeView<String> view; /** TreeView */
    public TreeItem<String> root; /** Root of TreeView */
    public EDataType itemType; /** Type of the TreeItem @see EDataType */
    public DataModel dataModel;

    /**
     * Constructor
     * @param model
     * @param view
     * @param itemType type of TreeItem
     * @see EDataType
     */
    public TreeViewItemModel(DataModel model, TreeView<String> view, EDataType itemType) {
        this.view = view;
        this.dataModel = model;
        this.root = new TreeItem<>();
        this.itemType = itemType;
    }

    /**
     * This method handles showing (creating and adding to rootItem) TreeItems depending on TreeItem type.
     */
    public void showTreeItem() {
        TreeItem<String> item;
        switch (this.itemType) {
            case CLASS_DIAGRAM:
                item = new TreeItem<>("Class diagram");
                this.root.getChildren().add(item);
                break;
            case SEQUENCE_DIAGRAM:
                break;
            case CLASS:
                String relationString = null;
                int classID = 0;
                for (ClassDiagram classDiagram : this.dataModel.getData().getClasses()) {
                    item = new TreeItem<>(classDiagram.getName());
                    classID = classDiagram.getID();
                    for (Attributes attributes : classDiagram.getAttribs()) {
                        item.getChildren().add(new TreeItem<>(attributes.getNameWithPrefix()));
                    }
                    for (Methods methods : classDiagram.getMethods()) {
                        item.getChildren().add(new TreeItem<>(methods.getNameWithPrefix()));
                    }
                    for (Relationships relations : this.dataModel.getData().getRelationships()) {
                        if (relations.getFromClassID() == classID && relations.getToClassID() == classID) {
                            relationString = ">< " + classDiagram.getName();
                        } else if (relations.getToClassID() == classID) {
                            relationString = "< " + this.dataModel.getData().getClassByID(relations.getFromClassID()).getName();
                        } else if  (relations.getFromClassID() == classID) {
                            relationString = "> " + this.dataModel.getData().getClassByID(relations.getToClassID()).getName();
                        }

                        if (relationString != null)
                            item.getChildren().add(new TreeItem<>(relationString));
                    }
                    this.root.getChildren().add(item);
                    relationString = null;
                }
                break;
        }
    }

    /**
     * Getter function
     * @param name
     * @return returns item if present in root or null
     */
    public TreeItem<String> getTreeItem(String name) {
        for (TreeItem<String> i : this.root.getChildren()) {
            if (i.getValue().equals(name)) {
                return i;
            }
        }
        return null;
    }

    /**
     * Adds new treeItem (child) to another treeItem (parent)
     * @param item
     * @param name
     */
    public void addTreeItemChild(TreeItem<String> item, String name) {
        TreeItem<String> child = null;
        switch (this.itemType) {
            case METHOD:
            case ATTRIBUTE:
            case RELATIONSHIP:
                child = new TreeItem<>(name);
                break;
        }
        item.getChildren().add(child);
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
                    previousExpanded.add(child.getValue());
                }
            });
            root.getChildren().forEach(child -> {
                if (previousExpanded.contains(child.getValue())) {
                    child.setExpanded(true);
                }
            });
        }

        this.view.setShowRoot(false);
        this.view.setRoot(root);
    }
}

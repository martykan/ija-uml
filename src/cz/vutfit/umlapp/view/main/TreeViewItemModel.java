/*
 * File: TreeViewItemModel.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.view.main;

import cz.vutfit.umlapp.model.DataModel;
import cz.vutfit.umlapp.model.uml.Attributes;
import cz.vutfit.umlapp.model.uml.ClassDiagram;
import cz.vutfit.umlapp.model.uml.Methods;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.util.HashSet;
import java.util.Set;

public class TreeViewItemModel {
    public TreeView<String> view;
    public TreeItem<String> root;
    public EDataType itemType;
    public DataModel dataModel;

    public TreeViewItemModel(DataModel model, TreeView<String> view, EDataType itemType) {
        this.view = view;
        this.dataModel = model;
        this.root = new TreeItem<>();
        this.itemType = itemType;
    }

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
                for (ClassDiagram classDiagram : this.dataModel.getData().getClasses()) {
                    item = new TreeItem<>(classDiagram.getName());
                    for (Attributes attributes : classDiagram.getAttribs()) {
                        item.getChildren().add(new TreeItem<>(attributes.getNameWithPrefix()));
                    }

                    for (Methods methods : classDiagram.getMethods()) {
                        item.getChildren().add(new TreeItem<>(methods.getNameWithPrefix()));
                    }
                    this.root.getChildren().add(item);
                }
                break;
        }
    }

    public TreeItem<String> getTreeItem(String name) {
        for (TreeItem<String> i : this.root.getChildren()) {
            if (i.getValue().equals(name)) {
                return i;
            }
        }
        return null;
    }

    public void addTreeItemChild(TreeItem<String> item, String name) {
        TreeItem<String> child = null;
        switch (this.itemType) {
            case METHOD:
            case ATTRIBUTE:
                child = new TreeItem<>(name);
                break;
            case RELATIONSHIP:
                break;  // todo
        }
        item.getChildren().add(child);
    }

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

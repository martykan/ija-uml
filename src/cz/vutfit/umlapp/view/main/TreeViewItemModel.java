package cz.vutfit.umlapp.view.main;

import com.sun.source.tree.Tree;
import javafx.scene.control.*;
import cz.vutfit.umlapp.model.uml.Attributes;
import cz.vutfit.umlapp.model.uml.ClassDiagram;
import cz.vutfit.umlapp.model.uml.EAttribVisibility;
import cz.vutfit.umlapp.model.uml.Methods;
import cz.vutfit.umlapp.model.DataModel;
import java.util.ArrayList;

public class TreeViewItemModel {
    public TreeView<String> view;
    public TreeItem<String> root;
    public EDataType itemType;
    public DataModel dataModel;

    public TreeViewItemModel(DataModel model, TreeView<String> view, EDataType itemType) {
        this.view = view;
        this.dataModel = model;
        this.root = new TreeItem<String>();
        this.itemType = itemType;
    }

    public void showTreeItem() {
        TreeItem<String> item = null;
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
                        item.getChildren().add(new TreeItem<String>(attributes.getNameWithPrefix()));
                    }

                    for (Methods methods : classDiagram.getMethods()) {
                        item.getChildren().add(new TreeItem<String>(methods.getNameWithPrefix()));
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
        this.view.setShowRoot(false);
        this.view.setRoot(root);
    }
}

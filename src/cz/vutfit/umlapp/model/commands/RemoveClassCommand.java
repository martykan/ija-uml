/*
 * File: RemoveClassCommand.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.commands;

import cz.vutfit.umlapp.model.uml.ClassDiagram;
import cz.vutfit.umlapp.model.uml.Relationships;
import cz.vutfit.umlapp.model.uml.UMLFileData;
import cz.vutfit.umlapp.model.uml.exceptions.DuplicateClassNameException;
import cz.vutfit.umlapp.model.uml.exceptions.DuplicateRelationshipException;

import java.util.ArrayList;

/**
 * Class for removing class command
 */
public class RemoveClassCommand implements ICommand {
    private ClassDiagram removed;
    private ArrayList<Relationships> removedRelationships;
    private final Integer classId;

    /**
     * Constructor
     * @param ID ID of class
     */
    public RemoveClassCommand(Integer ID) {
        this.classId = ID;
    }

    /**
     * Execute command - remove class
     * @param file
     */
    @Override
    public void execute(UMLFileData file) {
        ClassDiagram x = file.getClassByID(this.classId);
        String name = x.getName();
        this.removed = new ClassDiagram(this.classId);
        this.removed.setAll(name, x.getAttribs(), x.getMethods());
        file.removeClass(this.classId);

        this.removedRelationships = new ArrayList<>();
        for (Relationships relationship : file.getRelationships()) {
            if (relationship.getFromClassID() == this.classId || relationship.getToClassID() == this.classId) {
                removedRelationships.add(relationship);
            }
        }
        for (Relationships relationship : this.removedRelationships) {
            file.removeRelation(relationship.getID());
        }
    }

    /**
     * Undo command - add removed class back
     * @param file
     */
    @Override
    public void undo(UMLFileData file) {
        if (removed != null) {
            try {
                int classId = file.addClass(removed.getName());
                file.getClassByID(classId).setAll(removed.getName(), this.removed.getAttribs(), this.removed.getMethods());
                for (Relationships relationship : removedRelationships) {
                    int relationId;
                    if (relationship.getFromClassID() == this.classId) {
                        relationId = file.addRelation(classId, relationship.getToClassID(), relationship.getType());
                    } else {
                        relationId = file.addRelation(relationship.getFromClassID(), classId, relationship.getType());
                    }
                    file.getRelationByID(relationId).setName(relationship.name);
                    file.getRelationByID(relationId).setFromDesc(relationship.fromDesc);
                    file.getRelationByID(relationId).setToDesc(relationship.toDesc);
                }
            } catch (DuplicateClassNameException e) {
                System.out.println("DuplicateClassName EXCEPTION");
            } catch (DuplicateRelationshipException e) {
                System.out.println("DuplicateRelationshipException EXCEPTION");
            }
        }
    }
}

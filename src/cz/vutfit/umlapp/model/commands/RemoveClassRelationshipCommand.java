/*
 * File: AddClassAttributeCommand.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.commands;

import cz.vutfit.umlapp.model.uml.ERelationType;
import cz.vutfit.umlapp.model.uml.Relationships;
import cz.vutfit.umlapp.model.uml.UMLFileData;

/**
 * Class for adding new relationship to class command
 */
public class RemoveClassRelationshipCommand implements ICommand {
    private Integer relationID;
    private Integer fromClassID;
    private Integer toClassID;
    private ERelationType type;
    private String fromDesc;
    private String toDesc;

    /**
     * Constructor
     * @param relationID ID of relationship
     */
    public RemoveClassRelationshipCommand(Integer relationID) {
        this.relationID = relationID;
    }

    /**
     * Executing command - adding new relationship between two classes.
     *
     * @param file
     */
    @Override
    public void execute(UMLFileData file) {
        Relationships removal = file.getRelationByID(this.relationID);
        if (removal == null) {
            System.out.println("Error executing command RemoveClassRelationship - relation not found, ID: ".concat(String.valueOf(this.relationID)));
            return;
        }
        this.fromClassID = removal.getFromClassID();
        this.toClassID = removal.getToClassID();
        this.type = removal.getType();
        this.fromDesc = removal.getFromDesc();
        this.toDesc = removal.getToDesc();
        file.removeRelation(this.relationID);
    }

    /**
     * Undo this command (remove added attribute).
     * @param file
     */
    @Override
    public void undo(UMLFileData file) throws Exception {
        this.relationID = file.addRelation(this.fromClassID, this.toClassID, this.type);
        file.getRelationByID(this.relationID).setFromDesc(this.fromDesc);
        file.getRelationByID(this.relationID).setToDesc(this.toDesc);
    }
}

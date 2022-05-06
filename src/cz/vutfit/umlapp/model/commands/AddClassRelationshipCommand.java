/*
 * File: AddClassAttributeCommand.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.commands;

import cz.vutfit.umlapp.model.uml.ERelationType;
import cz.vutfit.umlapp.model.uml.UMLFileData;

/**
 * Class for adding new relationship to class command
 */
public class AddClassRelationshipCommand implements ICommand {
    private Integer relationID;
    private final String name;
    private final Integer fromClassID;
    private final Integer toClassID;
    private final ERelationType type;
    private final String fromDesc;
    private final String toDesc;

    /**
     * Constructor
     * @param fromClassID ID of class from which relationship goes
     * @param toClassID ID of class to which relationship goes
     * @param type type of relationship
     * @see ERelationType
     */
    public AddClassRelationshipCommand(Integer fromClassID, Integer toClassID, ERelationType type) {
        this.fromClassID = fromClassID;
        this.toClassID = toClassID;
        this.type = type;
        this.fromDesc = null;
        this.toDesc = null;
        this.name = null;
    }

    /**
     * Constructor with from / to (class) relationship descriptions
     * @param fromClassID ID of class from which relationship goes
     * @param toClassID ID of class to which relationship goes
     * @param type type of relationship
     * @param fromDesc description of relationship on fromClassID side
     * @param toDesc description of relationship on toClassID side
     * @see ERelationType
     */
    public AddClassRelationshipCommand(Integer fromClassID, Integer toClassID, ERelationType type, String name, String fromDesc, String toDesc) {
        this.fromClassID = fromClassID;
        this.toClassID = toClassID;
        this.type = type;
        this.fromDesc = fromDesc;
        this.toDesc = toDesc;
        this.name = name;
    }

    /**
     * Executing command - adding new relationship between two classes.
     * @param file
     * @throws Exception
     */
    @Override
    public void execute(UMLFileData file) throws Exception {
        this.relationID = file.addRelation(this.fromClassID, this.toClassID, this.type);
        file.getRelationByID(this.relationID).setFromDesc(this.fromDesc);
        file.getRelationByID(this.relationID).setToDesc(this.toDesc);
        file.getRelationByID(this.relationID).setName(this.name);
    }

    /**
     * Undo this command (remove added attribute).
     * @param file
     */
    @Override
    public void undo(UMLFileData file) {
        file.removeRelation(this.relationID);
    }
}

/*
 * File: AddClassAttributeCommand.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.commands;

import cz.vutfit.umlapp.model.uml.ERelationType;
import cz.vutfit.umlapp.model.uml.UMLFileData;

public class EditClassRelationshipTypeCommand implements ICommand {
    private final int relID;
    private final ERelationType newType;
    private ERelationType oldType;

    public EditClassRelationshipTypeCommand(Integer relationID, ERelationType newType) {
        this.relID = relationID;
        this.newType = newType;
    }

    @Override
    public void execute(UMLFileData file) {
        this.oldType = file.getRelationByID(this.relID).getType();
        file.getRelationByID(this.relID).setType(this.newType);
    }

    @Override
    public void undo(UMLFileData file) {
        file.getRelationByID(this.relID).setType(oldType);
    }
}

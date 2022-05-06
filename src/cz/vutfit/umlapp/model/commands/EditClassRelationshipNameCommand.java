/*
 * File: AddClassAttributeCommand.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.commands;

import cz.vutfit.umlapp.model.uml.UMLFileData;

public class EditClassRelationshipNameCommand implements ICommand {
    private final int relID;
    private final String newString;
    private String oldString;

    public EditClassRelationshipNameCommand(Integer relationID, String newName) {
        this.relID = relationID;
        this.newString = newName;
    }

    @Override
    public void execute(UMLFileData file) {
        this.oldString = file.getRelationByID(this.relID).getName();
        file.getRelationByID(this.relID).setName(this.newString);
    }

    @Override
    public void undo(UMLFileData file) {
        file.getRelationByID(this.relID).setName(this.oldString);
    }
}

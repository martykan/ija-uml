/*
 * File: AddClassAttributeCommand.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.commands;

import cz.vutfit.umlapp.model.uml.UMLFileData;

public class EditClassRelationshipFromDescCommand implements ICommand {
    private final int relID;
    private final String newString;
    private String oldString;

    public EditClassRelationshipFromDescCommand(Integer relationID, String newFromDesc) {
        this.relID = relationID;
        this.newString = newFromDesc;
    }

    @Override
    public void execute(UMLFileData file) {
        this.oldString = file.getRelationByID(this.relID).getFromDesc();
        file.getRelationByID(this.relID).setFromDesc(this.newString);
    }

    @Override
    public void undo(UMLFileData file) {
        file.getRelationByID(this.relID).setFromDesc(this.oldString);
    }
}

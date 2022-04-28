/*
 * File: AddClassAttributeCommand.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.commands;

import cz.vutfit.umlapp.model.uml.Attributes;
import cz.vutfit.umlapp.model.uml.EAttribVisibility;
import cz.vutfit.umlapp.model.uml.UMLFileData;

public class EditClassRelationshipToDescCommand implements ICommand {
    private final int relID;
    private final String newString;
    private String oldString;

    public EditClassRelationshipToDescCommand(Integer relationID, String newToDesc) {
        this.relID = relationID;
        this.newString = newToDesc;
    }

    @Override
    public void execute(UMLFileData file) {
        this.oldString = file.getRelationByID(this.relID).getToDesc();
        file.getRelationByID(this.relID).setToDesc(this.newString);
    }

    @Override
    public void undo(UMLFileData file) {
        file.getRelationByID(this.relID).setToDesc(this.oldString);
    }
}

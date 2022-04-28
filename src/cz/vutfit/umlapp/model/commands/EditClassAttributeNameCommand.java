/*
 * File: AddClassAttributeCommand.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.commands;

import cz.vutfit.umlapp.model.uml.Attributes;
import cz.vutfit.umlapp.model.uml.EAttribVisibility;
import cz.vutfit.umlapp.model.uml.UMLFileData;

public class EditClassAttributeNameCommand implements ICommand {
    private final int classID;
    private final String oldAttribName;
    private final String newAttribName;

    public EditClassAttributeNameCommand(Integer classID, String oldAttribName, String newName) {
        this.classID = classID;
        this.oldAttribName = oldAttribName;
        this.newAttribName = newName;
    }

    @Override
    public void execute(UMLFileData file) {
        file.getClassByID(this.classID).getAttribute(oldAttribName).setName(this.newAttribName);
    }

    @Override
    public void undo(UMLFileData file) {
        file.getClassByID(this.classID).getAttribute(this.newAttribName).setName(this.oldAttribName);
    }
}

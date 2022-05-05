/*
 * File: AddClassAttributeCommand.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.commands;

import cz.vutfit.umlapp.model.uml.Attributes;
import cz.vutfit.umlapp.model.uml.EAttribVisibility;
import cz.vutfit.umlapp.model.uml.EClassElementType;
import cz.vutfit.umlapp.model.uml.UMLFileData;

public class EditClassAttributeTypeCommand implements ICommand {
    private final int classID;
    private final String attribName;
    private final EClassElementType newType;
    private EClassElementType oldType;

    public EditClassAttributeTypeCommand(Integer classID, String attribName, EClassElementType newType) {
        this.classID = classID;
        this.attribName = attribName;
        this.newType = newType;
    }

    @Override
    public void execute(UMLFileData file) {
        this.oldType = file.getClassByID(this.classID).getAttribute(attribName).getType();
        file.getClassByID(this.classID).getAttribute(this.attribName).setType(this.newType);
    }

    @Override
    public void undo(UMLFileData file) {
        file.getClassByID(this.classID).getAttribute(this.attribName).setType(this.oldType);
    }
}

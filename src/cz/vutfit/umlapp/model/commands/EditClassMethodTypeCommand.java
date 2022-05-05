/*
 * File: AddClassAttributeCommand.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.commands;

import cz.vutfit.umlapp.model.uml.UMLFileData;

public class EditClassMethodTypeCommand implements ICommand {
    private final int classID;
    private final String attribName;
    private final String newType;
    private String oldType;

    public EditClassMethodTypeCommand(Integer classID, String attribName, String newType) {
        this.classID = classID;
        this.attribName = attribName;
        this.newType = newType;
    }

    @Override
    public void execute(UMLFileData file) {
        this.oldType = file.getClassByID(this.classID).getMethod(attribName).getType();
        file.getClassByID(this.classID).getMethod(this.attribName).setType(this.newType);
    }

    @Override
    public void undo(UMLFileData file) {
        file.getClassByID(this.classID).getMethod(this.attribName).setType(this.oldType);
    }
}

/*
 * File: AddClassAttributeCommand.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.commands;

import cz.vutfit.umlapp.model.uml.EAttribVisibility;
import cz.vutfit.umlapp.model.uml.UMLFileData;

public class EditClassAttributeVisibilityCommand implements ICommand {
    private final int classID;
    private final String attribName;
    private final EAttribVisibility newVisibility;
    private EAttribVisibility oldVisibility;

    public EditClassAttributeVisibilityCommand(Integer classID, String attribName, EAttribVisibility newVisibility) {
        this.classID = classID;
        this.attribName = attribName;
        this.newVisibility = newVisibility;
    }

    @Override
    public void execute(UMLFileData file) {
        this.oldVisibility = file.getClassByID(this.classID).getAttribute(attribName).getVisibility();
        file.getClassByID(this.classID).getAttribute(this.attribName).setVisibility(this.newVisibility);
    }

    @Override
    public void undo(UMLFileData file) {
        file.getClassByID(this.classID).getAttribute(this.attribName).setVisibility(this.oldVisibility);
    }
}

/*
 * File: AddClassMethodCommand.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.commands;

import cz.vutfit.umlapp.model.uml.EAttribVisibility;
import cz.vutfit.umlapp.model.uml.UMLFileData;

public class AddClassMethodCommand implements ICommand {
    private final Integer classId;
    private final String attrName;
    private final EAttribVisibility visibility;

    public AddClassMethodCommand(Integer classId, String attrName, EAttribVisibility visibility) {
        this.classId = classId;
        this.attrName = attrName;
        this.visibility = visibility;
    }

    @Override
    public void execute(UMLFileData file) throws Exception {
        file.getClassByID(classId).addMethod(attrName, visibility);
    }

    @Override
    public void undo(UMLFileData file) {
        file.getClassByID(classId).removeMethod(attrName);
    }
}

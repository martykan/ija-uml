/*
 * File: AddClassMethodCommand.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.commands;

import cz.vutfit.umlapp.model.uml.EAttribVisibility;
import cz.vutfit.umlapp.model.uml.EClassElementType;
import cz.vutfit.umlapp.model.uml.UMLFileData;

/**
 * Class for adding new method to class command
 */
public class AddClassMethodCommand implements ICommand {
    private final Integer classId;
    private final String attrName;
    private final EAttribVisibility visibility;
    private final EClassElementType type;

    /**
     * Constructor
     * @param classId ID of class
     * @param attrName new method name
     * @param visibility new method visiblity
     * @see EAttribVisibility
     */
    public AddClassMethodCommand(Integer classId, String attrName, EAttribVisibility visibility, EClassElementType type) {
        this.classId = classId;
        this.attrName = attrName;
        this.visibility = visibility;
        this.type = type;
    }

    /**
     * Execute command - add new method to class
     * @param file
     * @throws Exception
     */
    @Override
    public void execute(UMLFileData file) throws Exception {
        file.getClassByID(classId).addMethod(attrName, visibility, type);
    }

    /**
     * Undo command - remove added method from class
     * @param file
     */
    @Override
    public void undo(UMLFileData file) {
        file.getClassByID(classId).removeMethod(attrName);
    }
}

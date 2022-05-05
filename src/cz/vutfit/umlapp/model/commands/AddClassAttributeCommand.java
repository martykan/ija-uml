/*
 * File: AddClassAttributeCommand.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.commands;

import cz.vutfit.umlapp.model.uml.EAttribVisibility;
import cz.vutfit.umlapp.model.uml.EClassElementType;
import cz.vutfit.umlapp.model.uml.UMLFileData;

/**
 * Class for adding new attribute to class command
 */
public class AddClassAttributeCommand implements ICommand {
    private final Integer classId;
    private final String attrName;
    private final EAttribVisibility visibility;
    private final EClassElementType type;

    /**
     * Constructor
     * @param classId ID of class
     * @param attrName name of new attribute
     * @param visibility visibility of attribute
     * @see EAttribVisibility
     */
    public AddClassAttributeCommand(Integer classId, String attrName, EAttribVisibility visibility, EClassElementType type) {
        this.classId = classId;
        this.attrName = attrName;
        this.visibility = visibility;
        this.type = type;
    }

    /**
     * Executing command - adding new attribute to class.
     * @param file
     * @throws Exception
     */
    @Override
    public void execute(UMLFileData file) throws Exception {
        file.getClassByID(classId).addAttribute(attrName, visibility, type);
    }

    /**
     * Undo this command (remove added attribute).
     * @param file
     */
    @Override
    public void undo(UMLFileData file) {
        file.getClassByID(classId).removeAttribute(attrName);
    }
}

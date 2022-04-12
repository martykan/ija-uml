/*
 * File: AddClassCommand.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.commands;

import cz.vutfit.umlapp.model.uml.UMLFileData;
import cz.vutfit.umlapp.model.uml.exceptions.DuplicateClassNameException;

/**
 * Class for adding new class command
 */
public class AddClassCommand implements ICommand {
    private final String className;
    private Integer classId;

    /**
     * Constructor
     * @param className
     */
    public AddClassCommand(String className) {
        this.className = className;
    }

    /**
     * Executing command - add new class to model
     * @param file
     */
    @Override
    public void execute(UMLFileData file) {
        try {
            classId = file.addClass(className);
        } catch (DuplicateClassNameException e) {
            System.out.println("Duplicate Class Name - user is idiot");
        }
    }

    /**
     * Undo command - remove added class
     * @param file
     */
    @Override
    public void undo(UMLFileData file) {
        if (classId != null) {
            file.removeClass(classId);
        }
    }
}

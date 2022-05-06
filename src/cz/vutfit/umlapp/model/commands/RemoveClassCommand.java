/*
 * File: RemoveClassCommand.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.commands;

import cz.vutfit.umlapp.model.uml.ClassDiagram;
import cz.vutfit.umlapp.model.uml.UMLFileData;
import cz.vutfit.umlapp.model.uml.exceptions.DuplicateClassNameException;

/**
 * Class for removing class command
 */
public class RemoveClassCommand implements ICommand {
    private ClassDiagram removed;
    private final Integer classId;

    /**
     * Constructor
     * @param ID ID of class
     */
    public RemoveClassCommand(Integer ID) {
        this.classId = ID;
    }

    /**
     * Execute command - remove class
     * @param file
     */
    @Override
    public void execute(UMLFileData file) {
        ClassDiagram x = file.getClassByID(this.classId);
        String name = x.getName();
        this.removed = new ClassDiagram(this.classId);
        this.removed.setAll(name, x.getAttribs(), x.getMethods());
        file.removeClass(this.classId);
    }

    /**
     * Undo command - add removed class back
     * @param file
     */
    @Override
    public void undo(UMLFileData file) {
        if (removed != null) {
            try {
                file.addClass(removed.getName());
            } catch (DuplicateClassNameException e) {
                System.out.println("DuplicateClassName EXCEPTION");
            }
            file.getClassByID(this.classId).setAll(removed.getName(), this.removed.getAttribs(), this.removed.getMethods());
        }
    }
}

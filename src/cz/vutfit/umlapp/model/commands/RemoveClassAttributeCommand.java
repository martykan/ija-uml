/*
 * File: RemoveClassAttributeCommand.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.commands;

import cz.vutfit.umlapp.model.uml.ClassDiagram;
import cz.vutfit.umlapp.model.uml.EAttribVisibility;
import cz.vutfit.umlapp.model.uml.UMLFileData;
import cz.vutfit.umlapp.model.uml.exceptions.DuplicateAttributeNameException;

/**
 * Class for removing attribute from class command
 */
public class RemoveClassAttributeCommand implements ICommand {
    private final ClassDiagram myClass;
    private final String id;
    private final EAttribVisibility visibility;

    /**
     * Constructor
     * @param myClass class from which attribute will be removed
     * @param ID ID-name of attribute
     * @param visibility removed attribute visibility
     * @see EAttribVisibility
     */
    public RemoveClassAttributeCommand(ClassDiagram myClass, String ID, EAttribVisibility visibility) {
        this.myClass = myClass;
        this.id = ID;
        this.visibility = visibility;
    }

    /**
     * Execute command - remove attribute from class
     * @param file
     */
    @Override
    public void execute(UMLFileData file) {
        this.myClass.removeAttribute(this.id);
    }

    /**
     * Undo command - add back removed attribute
     * @param file
     */
    @Override
    public void undo(UMLFileData file) {
        if (this.myClass != null) {
            try {
                this.myClass.addAttribute(this.id, this.visibility);
            } catch (DuplicateAttributeNameException e) {
                System.out.println("DuplicateAttribNameException");
            }
        }
    }

}

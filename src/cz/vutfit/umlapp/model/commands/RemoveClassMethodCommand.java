/*
 * File: RemoveClassMethodCommand.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.commands;

import cz.vutfit.umlapp.model.uml.ClassDiagram;
import cz.vutfit.umlapp.model.uml.EAttribVisibility;
import cz.vutfit.umlapp.model.uml.UMLFileData;
import cz.vutfit.umlapp.model.uml.exceptions.DuplicateMethodNameException;

/**
 * Class for remove method from class command
 */
public class RemoveClassMethodCommand implements ICommand {
    public ClassDiagram myClass;
    public String id;
    public EAttribVisibility visibility;
    public String type;

    /**
     * Constructor
     * @param myClass class from which method will be removed
     * @param ID ID-name of method
     * @param visibility visibility of removed method
     */
    public RemoveClassMethodCommand(ClassDiagram myClass, String ID, EAttribVisibility visibility, String type) {
        this.myClass = myClass;
        this.id = ID;
        this.visibility = visibility;
        this.type = type;
    }

    /**
     * Execute command - remove method from class
     * @param file
     */
    @Override
    public void execute(UMLFileData file) {
        ClassDiagram rem = this.myClass;
        rem.removeMethod(this.id);
    }

    /**
     * Undo command - add removed method back
     * @param file
     */
    @Override
    public void undo(UMLFileData file) {
        if (this.myClass != null) {
            try {
                this.myClass.addMethod(this.id, this.visibility, this.type);
            } catch (DuplicateMethodNameException e) {
                System.out.println("DuplicateAttribNameException");
            }
        }
    }

}

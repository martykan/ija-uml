package cz.vutfit.umlapp.model.commands;

import cz.vutfit.umlapp.model.uml.ClassDiagram;
import cz.vutfit.umlapp.model.uml.EAttribVisibility;
import cz.vutfit.umlapp.model.uml.UMLFileData;
import cz.vutfit.umlapp.model.uml.exceptions.DuplicateAttributeNameException;

public class RemoveClassAttributeCommand implements ICommand {
    private ClassDiagram myClass;
    private String id;
    private EAttribVisibility visibility;

    public RemoveClassAttributeCommand(ClassDiagram myClass, String ID, EAttribVisibility visibility) {
        this.myClass = myClass;
        this.id = ID;
        this.visibility = visibility;
    }

    @Override
    public void execute(UMLFileData file) {
        this.myClass.removeAttribute(this.id);
    }

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

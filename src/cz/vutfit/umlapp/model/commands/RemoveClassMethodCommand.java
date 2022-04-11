package cz.vutfit.umlapp.model.commands;

import cz.vutfit.umlapp.model.uml.ClassDiagram;
import cz.vutfit.umlapp.model.uml.EAttribVisibility;
import cz.vutfit.umlapp.model.uml.UMLFileData;
import cz.vutfit.umlapp.model.uml.exceptions.DuplicateMethodNameException;

public class RemoveClassMethodCommand implements ICommand {
    public ClassDiagram myClass;
    public String id;
    public EAttribVisibility visibility;

    public RemoveClassMethodCommand(ClassDiagram myClass, String ID, EAttribVisibility visibility) {
        this.myClass = myClass;
        this.id = ID;
        this.visibility = visibility;
    }

    @Override
    public void execute(UMLFileData file) {
        ClassDiagram rem = this.myClass;
        rem.removeMethod(this.id);
    }

    @Override
    public void undo(UMLFileData file) {
        if (this.myClass != null) {
            try {
                this.myClass.addMethod(this.id, this.visibility);
            } catch (DuplicateMethodNameException e) {
                System.out.println("DuplicateAttribNameException");
            }
        }
    }

}

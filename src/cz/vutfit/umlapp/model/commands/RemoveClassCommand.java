package cz.vutfit.umlapp.model.commands;

import cz.vutfit.umlapp.model.uml.ClassDiagram;
import cz.vutfit.umlapp.model.uml.UMLFileData;
import cz.vutfit.umlapp.model.uml.exceptions.DuplicateClassNameException;

public class RemoveClassCommand implements ICommand {
    private ClassDiagram removed;
    private final Integer classId;

    public RemoveClassCommand(Integer ID) {
        this.classId = ID;
    }

    @Override
    public void execute(UMLFileData file) {
        ClassDiagram x = file.getClassByID(this.classId);
        String name = x.getName();
        this.removed = new ClassDiagram(this.classId);
        this.removed.setAll(name, x.getAttribs(), x.getMethods(), x.getSeqdigs());
        file.removeClass(this.classId);
    }

    @Override
    public void undo(UMLFileData file) {
        if (removed != null) {
            try {
                file.addClass(removed.getName());
            } catch (DuplicateClassNameException e) {
                System.out.println("DuplicateClassName EXCEPTION");
            }
            file.getClassByID(this.classId).setAll(removed.getName(), this.removed.getAttribs(), this.removed.getMethods(), this.removed.getSeqdigs());
        }
    }
}

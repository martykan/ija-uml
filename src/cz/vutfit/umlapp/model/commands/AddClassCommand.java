package cz.vutfit.umlapp.model.commands;

import cz.vutfit.umlapp.model.uml.UMLFileData;
import cz.vutfit.umlapp.model.uml.exceptions.DuplicateClassNameException;

public class AddClassCommand implements ICommand {
    private final String className;
    private Integer classId;

    public AddClassCommand(String className) {
        this.className = className;
    }

    @Override
    public void execute(UMLFileData file) {
        try {
            classId = file.addClass(className);
        } catch (DuplicateClassNameException e) {
            System.out.println("Duplicate Class Name - user is idiot");
        }
    }

    @Override
    public void undo(UMLFileData file) {
        if (classId != null) {
            file.removeClass(classId);
        }
    }
}

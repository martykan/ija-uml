package cz.vutfit.umlapp.model.commands;

import cz.vutfit.umlapp.model.uml.UMLFileData;

public class AddClassCommand implements ICommand {
    private final String className;
    private Integer classId;

    public AddClassCommand(String className) {
        this.className = className;
    }

    @Override
    public void execute(UMLFileData file) {
        classId = file.addClass(className);
    }

    @Override
    public void undo(UMLFileData file) {
        if (classId != null) {
            file.removeClass(classId);
        }
    }
}

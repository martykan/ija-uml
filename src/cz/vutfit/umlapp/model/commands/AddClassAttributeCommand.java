package cz.vutfit.umlapp.model.commands;

import cz.vutfit.umlapp.model.uml.EAttribVisibility;
import cz.vutfit.umlapp.model.uml.UMLFileData;

public class AddClassAttributeCommand implements ICommand {
    private final Integer classId;
    private final String attrName;
    private final EAttribVisibility visibility;

    public AddClassAttributeCommand(Integer classId, String attrName, EAttribVisibility visibility) {
        this.classId = classId;
        this.attrName = attrName;
        this.visibility = visibility;
    }

    @Override
    public void execute(UMLFileData file) throws Exception {
        file.getClassByID(classId).addAttribute(attrName, visibility);
    }

    @Override
    public void undo(UMLFileData file) {
        file.getClassByID(classId).removeAttribute(attrName);
    }
}

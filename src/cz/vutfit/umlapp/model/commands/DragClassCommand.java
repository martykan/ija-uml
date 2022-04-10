package cz.vutfit.umlapp.model.commands;

import cz.vutfit.umlapp.model.uml.UMLFileData;

public class DragClassCommand implements ICommand {
    private final Integer classId;
    private final Double newPositionX;
    private final Double newPositionY;
    private Double oldPositionX;
    private Double oldPositionY;

    public DragClassCommand(int classId, double newPositionX, double newPositionY) {
        this.classId = classId;
        this.newPositionX = newPositionX;
        this.newPositionY = newPositionY;
    }

    @Override
    public void execute(UMLFileData file) throws Exception {
        oldPositionX = file.getClassByID(classId).positionX;
        oldPositionY = file.getClassByID(classId).positionY;
        file.getClassByID(classId).positionX = newPositionX;
        file.getClassByID(classId).positionY = newPositionY;
    }

    @Override
    public void undo(UMLFileData file) throws Exception {
        file.getClassByID(classId).positionX = oldPositionX;
        file.getClassByID(classId).positionY = oldPositionY;
    }
}

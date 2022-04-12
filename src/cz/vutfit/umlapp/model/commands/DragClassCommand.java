/*
 * File: DragClassCommand.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.commands;

import cz.vutfit.umlapp.model.uml.UMLFileData;

/**
 * Class for dragging-moving class box (in View, UI)
 */
public class DragClassCommand implements ICommand {
    private final Integer classId;
    private final Double newPositionX;
    private final Double newPositionY;
    private Double oldPositionX;
    private Double oldPositionY;

    /**
     * Constructor
     * @param classId ID of class
     * @param newPositionX
     * @param newPositionY
     */
    public DragClassCommand(int classId, double newPositionX, double newPositionY) {
        this.classId = classId;
        this.newPositionX = newPositionX;
        this.newPositionY = newPositionY;
    }

    /**
     * Execute command - move class to new position
     * @param file
     */
    @Override
    public void execute(UMLFileData file) {
        oldPositionX = file.getClassByID(classId).positionX;
        oldPositionY = file.getClassByID(classId).positionY;
        file.getClassByID(classId).positionX = newPositionX;
        file.getClassByID(classId).positionY = newPositionY;
    }

    /**
     * Undo command - move class back to original position
     * @param file
     * @throws Exception
     */
    @Override
    public void undo(UMLFileData file) throws Exception {
        file.getClassByID(classId).positionX = oldPositionX;
        file.getClassByID(classId).positionY = oldPositionY;
    }
}

/*
 * File: AddDiagramCommand.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.commands;

import cz.vutfit.umlapp.model.uml.UMLFileData;

public class AddSequenceDiagramCommand implements ICommand {
    String diagramName;
    Integer diagramId;

    public AddSequenceDiagramCommand(String diagramName) {
        this.diagramName = diagramName;
    }

    @Override
    public void execute(UMLFileData file) throws Exception {
        diagramId = file.addSequence(diagramName);
    }

    @Override
    public void undo(UMLFileData file) throws Exception {
        if (diagramId != null) {
            file.removeSequence(diagramId);
        }
    }
}

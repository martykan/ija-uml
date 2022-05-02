/*
 * File: RemoveSequenceDiagramObjectCommand.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.commands;

import cz.vutfit.umlapp.model.uml.UMLFileData;

public class RemoveSequenceDiagramObjectCommand implements ICommand {
    private final int sequenceID;
    private final String objectName;

    public RemoveSequenceDiagramObjectCommand(int sequenceID, String objectName) {
        this.sequenceID = sequenceID;
        this.objectName = objectName;
    }

    @Override
    public void execute(UMLFileData file) throws Exception {
        file.getSequenceByID(this.sequenceID).removeObject(this.objectName);
    }

    @Override
    public void undo(UMLFileData file) throws Exception {
        file.getSequenceByID(this.sequenceID).addObject(this.objectName);
    }
}

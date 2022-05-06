/*
 * File: AddSequenceDiagramObjectCommand.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.commands;

import cz.vutfit.umlapp.model.uml.UMLFileData;

public class AddSequenceDiagramObjectCommand implements ICommand {
    private final int sequenceID;
    private final String objectName;
    private final String objectClassName;

    public AddSequenceDiagramObjectCommand(int sequenceID, String objectClassName, String objectName) {
        this.sequenceID = sequenceID;
        this.objectClassName = objectClassName;
        this.objectName = objectName;
    }

    @Override
    public void execute(UMLFileData file) throws Exception {
        file.getSequenceByID(this.sequenceID).addObject(this.objectClassName, this.objectName);
    }

    @Override
    public void undo(UMLFileData file) {
        file.getSequenceByID(this.sequenceID).removeObject(this.objectClassName, this.objectName);
    }
}

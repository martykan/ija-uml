/*
 * File: AddClassAttributeCommand.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.commands;

import cz.vutfit.umlapp.model.uml.*;

public class EditSequenceDiagramMessageTypeCommand implements ICommand {
    private final int seqID;
    private final int messID;
    private final EMessageType newType;
    private EMessageType oldType;

    public EditSequenceDiagramMessageTypeCommand(Integer sequenceID, Integer messageID, EMessageType newType) {
        this.seqID = sequenceID;
        this.messID = messageID;
        this.newType = newType;
    }

    @Override
    public void execute(UMLFileData file) {
        this.oldType = file.getSequenceByID(this.seqID).getMessageByID(this.messID).getType();
        file.getSequenceByID(this.seqID).getMessageByID(this.messID).setType(this.newType);
    }

    @Override
    public void undo(UMLFileData file) {
        file.getSequenceByID(this.seqID).getMessageByID(this.messID).setType(this.oldType);
    }
}

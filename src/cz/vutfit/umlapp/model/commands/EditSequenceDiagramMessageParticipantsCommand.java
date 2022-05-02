/*
 * File: EditSequenceDiagramMessageContentCommand.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.commands;

import cz.vutfit.umlapp.model.uml.UMLFileData;

public class EditSequenceDiagramMessageParticipantsCommand implements ICommand {
    private final int sequenceID;
    private final int messageID;
    private String oldSender;
    private String oldReceiver;
    private final String newSender;
    private final String newReceiver;

    public EditSequenceDiagramMessageParticipantsCommand(Integer sequenceID, Integer messageID, String newSender, String newReceiver) {
        this.sequenceID = sequenceID;
        this.messageID = messageID;
        this.newSender = newSender;
        this.newReceiver = newReceiver;
    }

    @Override
    public void execute(UMLFileData file) {
        this.oldSender = file.getSequenceByID(sequenceID).getMessageByID(messageID).getSender();
        this.oldReceiver = file.getSequenceByID(sequenceID).getMessageByID(messageID).getReceiver();
        file.getSequenceByID(sequenceID).getMessageByID(messageID).setParticipants(this.newSender, this.newReceiver);
    }

    @Override
    public void undo(UMLFileData file) {
        file.getSequenceByID(this.sequenceID).getMessageByID(this.messageID).setParticipants(this.oldSender, this.oldReceiver);
    }
}

/*
 * File: RemoveSequenceDiagramMessageCommand.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.commands;

import cz.vutfit.umlapp.model.uml.EMessageType;
import cz.vutfit.umlapp.model.uml.UMLFileData;

public class RemoveSequenceDiagramMessageCommand implements ICommand {
    private final int sequenceID;
    private final int msgIndex;
    private int msgID;
    private String content;
    private EMessageType type;
    private String senderName;
    private String receiverName;

    public RemoveSequenceDiagramMessageCommand(int sequenceID, int messageIndex) {
        this.sequenceID = sequenceID;
        this.msgIndex = messageIndex;
    }

    @Override
    public void execute(UMLFileData file) throws Exception {
        this.msgID = file.getSequenceByID(this.sequenceID).getMessageByIndex(this.msgIndex - 1).getID();
        this.content = file.getSequenceByID(this.sequenceID).getMessageByID(this.msgID).getContent();
        this.type = file.getSequenceByID(this.sequenceID).getMessageByID(this.msgID).getType();
        this.senderName = file.getSequenceByID(this.sequenceID).getMessageByID(this.msgID).getSender();
        this.receiverName = file.getSequenceByID(this.sequenceID).getMessageByID(this.msgID).getReceiver();
        file.getSequenceByID(this.sequenceID).removeMessage(msgID);
    }

    @Override
    public void undo(UMLFileData file) throws Exception {
        int newID = file.getSequenceByID(this.sequenceID).addMessage(content);
        file.getSequenceByID(this.sequenceID).getMessageByID(newID).setContent(this.content);
        file.getSequenceByID(this.sequenceID).getMessageByID(newID).setType(this.type);
        file.getSequenceByID(this.sequenceID).getMessageByID(newID).setParticipants(this.senderName, this.receiverName);
    }
}

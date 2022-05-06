/*
 * File: RemoveSequenceDiagramMessageCommand.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.commands;

import cz.vutfit.umlapp.model.uml.EMessageType;
import cz.vutfit.umlapp.model.uml.SequenceMessages;
import cz.vutfit.umlapp.model.uml.UMLFileData;
import javafx.util.Pair;

public class RemoveSequenceDiagramMessageCommand implements ICommand {
    private final int sequenceID;
    private final int msgIndex;
    private String content;
    private EMessageType type;
    private Pair<String, String> senderName;
    private Pair<String, String> receiverName;

    public RemoveSequenceDiagramMessageCommand(int sequenceID, int messageIndex) {
        this.sequenceID = sequenceID;
        this.msgIndex = messageIndex;
    }

    @Override
    public void execute(UMLFileData file) {
        int msgID = file.getSequenceByID(this.sequenceID).getMessageByIndex(this.msgIndex).getID();
        this.content = file.getSequenceByID(this.sequenceID).getMessageByID(msgID).getContent();
        this.type = file.getSequenceByID(this.sequenceID).getMessageByID(msgID).getType();
        this.senderName = file.getSequenceByID(this.sequenceID).getMessageByID(msgID).getSender();
        this.receiverName = file.getSequenceByID(this.sequenceID).getMessageByID(msgID).getReceiver();
        file.getSequenceByID(this.sequenceID).removeMessage(msgID);

        boolean senderActive = false;
        boolean receiverActive = false;
        for (SequenceMessages m : file.getSequenceByID(this.sequenceID).getMessages()) {
            if (m.getSender().equals(this.senderName) || m.getReceiver().equals(this.senderName))
                senderActive = true;
            if (m.getSender().equals(this.receiverName) || m.getReceiver().equals(this.receiverName))
                receiverActive = true;
            if (receiverActive && senderActive)
                break;
        }

        file.getSequenceByID(this.sequenceID).getObject(this.senderName).setActiveStatus(senderActive);
        file.getSequenceByID(this.sequenceID).getObject(this.receiverName).setActiveStatus(receiverActive);
    }

    @Override
    public void undo(UMLFileData file) {
        int newID = file.getSequenceByID(this.sequenceID).addMessage(content);
        file.getSequenceByID(this.sequenceID).getMessageByID(newID).setContent(this.content);
        file.getSequenceByID(this.sequenceID).getMessageByID(newID).setType(this.type);
        file.getSequenceByID(this.sequenceID).getMessageByID(newID).setParticipants(this.senderName, this.receiverName);
        file.getSequenceByID(this.sequenceID).getObject(this.senderName).setActiveStatus(true);
        file.getSequenceByID(this.sequenceID).getObject(this.receiverName).setActiveStatus(true);
    }
}

/*
 * File: AddSequenceDiagramMessageCommand.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.commands;

import cz.vutfit.umlapp.model.uml.EMessageType;
import cz.vutfit.umlapp.model.uml.UMLFileData;
import javafx.util.Pair;

public class AddSequenceDiagramMessageCommand implements ICommand {
    private final int sequenceID;
    private final String content;
    private int messageID;
    private final Pair<String, String> senderName;
    private final Pair<String, String> receiverName;
    private final EMessageType type;


    public AddSequenceDiagramMessageCommand(int sequenceID, String content, Pair<String, String> senderName, Pair<String, String> receiverName, EMessageType type) {
        this.sequenceID = sequenceID;
        this.content = content;
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.type = type;
    }

    @Override
    public void execute(UMLFileData file) {
        this.messageID = file.getSequenceByID(this.sequenceID).addMessage(this.content);
        file.getSequenceByID(this.sequenceID).getMessageByID(this.messageID).setType(this.type);
        file.getSequenceByID(this.sequenceID).getMessageByID(this.messageID).setParticipants(senderName, receiverName);
        file.getSequenceByID(this.sequenceID).getObject(senderName).setActiveStatus(true);
        file.getSequenceByID(this.sequenceID).getObject(receiverName).setActiveStatus(this.type != EMessageType.RELEASE_OBJECT);
    }

    @Override
    public void undo(UMLFileData file) {
        file.getSequenceByID(this.sequenceID).removeMessage(this.messageID);
        file.getSequenceByID(this.sequenceID).getObject(senderName).setActiveStatus(false);
        file.getSequenceByID(this.sequenceID).getObject(receiverName).setActiveStatus(this.type == EMessageType.RELEASE_OBJECT);
    }
}

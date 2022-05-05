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
    public void execute(UMLFileData file) throws Exception {
        this.messageID = file.getSequenceByID(this.sequenceID).addMessage(this.content);
        file.getSequenceByID(this.sequenceID).getMessageByID(this.messageID).setType(this.type);
        file.getSequenceByID(this.sequenceID).getMessageByID(this.messageID).setParticipants(senderName, receiverName);
        file.getSequenceByID(this.sequenceID).getObject(senderName).setActiveStatus(true);
        if (this.type == EMessageType.NEW_OBJECT) {
            file.getSequenceByID(this.sequenceID).addObject(receiverName);
        } else if (this.type == EMessageType.RELEASE_OBJECT)
            file.getSequenceByID(this.sequenceID).getObject(receiverName).setActiveStatus(false);
        else
            file.getSequenceByID(this.sequenceID).getObject(receiverName).setActiveStatus(true);
    }

    @Override
    public void undo(UMLFileData file) throws Exception {
        file.getSequenceByID(this.sequenceID).removeMessage(this.messageID);
        file.getSequenceByID(this.sequenceID).getObject(senderName).setActiveStatus(false);
        if (this.type == EMessageType.NEW_OBJECT)
            file.getSequenceByID(this.sequenceID).removeObject(receiverName.getKey(), receiverName.getValue());
        else if (this.type == EMessageType.RELEASE_OBJECT)
            file.getSequenceByID(this.sequenceID).getObject(receiverName).setActiveStatus(true);
        else
                file.getSequenceByID(this.sequenceID).getObject(receiverName).setActiveStatus(false);
    }
}

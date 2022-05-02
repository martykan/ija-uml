/*
 * File: AddSequenceDiagramMessageCommand.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.commands;

import cz.vutfit.umlapp.model.uml.EMessageType;
import cz.vutfit.umlapp.model.uml.UMLFileData;

public class AddSequenceDiagramMessageCommand implements ICommand {
    private final int sequenceID;
    private final String content;
    private int messageID;
    private final boolean direction;
    private final EMessageType type;


    public AddSequenceDiagramMessageCommand(int sequenceID, String content, boolean direction, EMessageType type) {
        this.sequenceID = sequenceID;
        this.content = content;
        this.direction = direction;
        this.type = type;
    }

    @Override
    public void execute(UMLFileData file) throws Exception {
        this.messageID = file.getSequenceByID(this.sequenceID).addMessage(this.content);
        file.getSequenceByID(this.sequenceID).getMessageByID(this.messageID).setType(this.type);
        file.getSequenceByID(this.sequenceID).getMessageByID(this.messageID).setDirection(this.direction);
    }

    @Override
    public void undo(UMLFileData file) throws Exception {
        file.getSequenceByID(this.sequenceID).removeMessage(this.messageID);
    }
}
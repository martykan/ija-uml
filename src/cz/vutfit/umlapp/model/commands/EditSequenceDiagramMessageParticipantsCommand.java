/*
 * File: EditSequenceDiagramMessageContentCommand.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.commands;

import cz.vutfit.umlapp.model.uml.UMLFileData;
import javafx.util.Pair;

public class EditSequenceDiagramMessageParticipantsCommand implements ICommand {
    private final int sequenceID;
    private final int messageID;
    private Pair<String, String> oldSender;
    private Pair<String, String> oldReceiver;
    private final Pair<String, String> newSender;
    private final Pair<String, String> newReceiver;
    private final String newContent;
    private String oldContent;

    public EditSequenceDiagramMessageParticipantsCommand(Integer sequenceID, Integer messageID, Pair<String, String> newSender, Pair<String, String> newReceiver, String newContent) {
        this.sequenceID = sequenceID;
        this.messageID = messageID;
        this.newSender = newSender;
        this.newReceiver = newReceiver;
        this.newContent = newContent;
    }

    @Override
    public void execute(UMLFileData file) {
        this.oldSender = file.getSequenceByID(sequenceID).getMessageByID(messageID).getSender();
        this.oldReceiver = file.getSequenceByID(sequenceID).getMessageByID(messageID).getReceiver();
        this.oldContent = file.getSequenceByID(sequenceID).getMessageByID(messageID).getContent();
        file.getSequenceByID(sequenceID).getMessageByID(messageID).setParticipants(this.newSender, this.newReceiver);
        file.getSequenceByID(sequenceID).getMessageByID(messageID).setContent(this.newContent);
    }

    @Override
    public void undo(UMLFileData file) {
        file.getSequenceByID(this.sequenceID).getMessageByID(this.messageID).setParticipants(this.oldSender, this.oldReceiver);
        file.getSequenceByID(sequenceID).getMessageByID(messageID).setContent(this.oldContent);
    }
}

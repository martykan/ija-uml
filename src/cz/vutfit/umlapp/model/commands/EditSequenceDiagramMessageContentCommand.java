/*
 * File: EditSequenceDiagramMessageContentCommand.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.commands;

import cz.vutfit.umlapp.model.uml.UMLFileData;

public class EditSequenceDiagramMessageContentCommand implements ICommand {
    private final int sequenceID;
    private final int messageID;
    private String oldContent;
    private final String newContent;

    public EditSequenceDiagramMessageContentCommand(Integer sequenceID, Integer messageID, String newContent) {
        this.sequenceID = sequenceID;
        this.messageID = messageID;
        this.newContent = newContent;
    }

    @Override
    public void execute(UMLFileData file) {
        this.oldContent = file.getSequenceByID(sequenceID).getMessageByID(messageID).getContent();
        file.getSequenceByID(sequenceID).getMessageByID(messageID).setContent(this.newContent);
    }

    @Override
    public void undo(UMLFileData file) {
        file.getSequenceByID(this.sequenceID).getMessageByID(this.messageID).setContent(this.oldContent);
    }
}

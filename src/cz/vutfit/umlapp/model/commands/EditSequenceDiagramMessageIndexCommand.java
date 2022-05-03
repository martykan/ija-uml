/*
 * File: EditSequenceDiagramMessageContentCommand.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.commands;

import cz.vutfit.umlapp.model.uml.SequenceDiagram;
import cz.vutfit.umlapp.model.uml.SequenceMessages;
import cz.vutfit.umlapp.model.uml.UMLFileData;

import java.util.ArrayList;
import java.util.Collections;

public class EditSequenceDiagramMessageIndexCommand implements ICommand {
    private final int sequenceID;
    private final int oldMessageIndex;
    private final int newMessageIndex;

    public EditSequenceDiagramMessageIndexCommand(Integer sequenceID, Integer oldMessageIndex, Integer newMessageIndex) {
        this.sequenceID = sequenceID;
        this.newMessageIndex = newMessageIndex;
        this.oldMessageIndex = oldMessageIndex;
    }

    @Override
    public void execute(UMLFileData file) {
        SequenceDiagram currentDiagram = file.getSequenceByID(sequenceID);
        SequenceMessages oldIndexMessage = currentDiagram.getMessageByIndex(oldMessageIndex);
        SequenceMessages newIndexMessage = currentDiagram.getMessageByIndex(newMessageIndex);
        ArrayList<SequenceMessages> messages = currentDiagram.getMessages();
        Collections.swap(messages, oldMessageIndex, newMessageIndex);
    }

    @Override
    public void undo(UMLFileData file) {
        SequenceDiagram currentDiagram = file.getSequenceByID(sequenceID);
        SequenceMessages oldIndexMessage = currentDiagram.getMessageByIndex(oldMessageIndex);
        SequenceMessages newIndexMessage = currentDiagram.getMessageByIndex(newMessageIndex);
        ArrayList<SequenceMessages> messages = currentDiagram.getMessages();
        Collections.swap(messages, oldMessageIndex, newMessageIndex);
    }
}

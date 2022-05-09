/*
 * File: RemoveSequenceDiagramObjectCommand.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.commands;

import cz.vutfit.umlapp.model.uml.SequenceMessages;
import cz.vutfit.umlapp.model.uml.SequenceObjects;
import cz.vutfit.umlapp.model.uml.UMLFileData;
import javafx.util.Pair;

import java.util.ArrayList;

public class RemoveSequenceDiagramObjectCommand implements ICommand {
    private final int sequenceID;
    private final String objectName;
    private final String className;
    private final ArrayList<SequenceMessages> removedMessages;
    private int objIndex;

    public RemoveSequenceDiagramObjectCommand(int sequenceID, SequenceObjects sequenceObject) {
        this.sequenceID = sequenceID;
        this.objectName = sequenceObject.getObjectName();
        this.className = sequenceObject.getClassName();
        this.removedMessages = new ArrayList<>();
    }

    @Override
    public void execute(UMLFileData file) {
        ArrayList<Integer> removalIDs = new ArrayList<>();
        Pair<String, String> objectName = new Pair<>(this.className, this.objectName);
        this.objIndex = file.getSequenceByID(this.sequenceID).getObjectIndex(this.className, this.objectName);
        for (SequenceMessages m : file.getSequenceByID(this.sequenceID).getMessages()) {
            if (m.getSender().equals(objectName) || m.getReceiver().equals(objectName)) {
                removalIDs.add(m.getID());
            }
        }
        for (int id : removalIDs) {
            SequenceMessages removed = new SequenceMessages(id, file.getSequenceByID(this.sequenceID).getMessageByID(id).getContent());
            removed.setType(file.getSequenceByID(this.sequenceID).getMessageByID(id).getType());
            removed.setParticipants(file.getSequenceByID(this.sequenceID).getMessageByID(id).getSender(), file.getSequenceByID(this.sequenceID).getMessageByID(id).getReceiver());
            this.removedMessages.add(removed);
            file.getSequenceByID(this.sequenceID).removeMessage(id);
        }
        file.getSequenceByID(this.sequenceID).removeObject(objectName);
    }

    @Override
    public void undo(UMLFileData file) throws Exception {
        Pair<String, String> objectName = new Pair<>(this.className, this.objectName);
        file.getSequenceByID(this.sequenceID).addObjectToIndex(objectName, this.objIndex);

        for (SequenceMessages restore : this.removedMessages) {
            int newID = file.getSequenceByID(this.sequenceID).addMessage(restore.getContent());
            file.getSequenceByID(this.sequenceID).getMessageByID(newID).setType(restore.getType());
            file.getSequenceByID(this.sequenceID).getMessageByID(newID).setParticipants(restore.getSender(), restore.getReceiver());
        }
    }
}

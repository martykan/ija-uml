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

public class RemoveSequenceDiagramClassInstanceCommand implements ICommand {
    private final int sequenceID;
    private final String className;
    private ArrayList<String> objectNames;
    private ArrayList<SequenceMessages> removedMessages;

    public RemoveSequenceDiagramClassInstanceCommand(int sequenceID, String className) {
        this.sequenceID = sequenceID;
        this.className = className;
        this.objectNames = new ArrayList<>();
        this.removedMessages = new ArrayList<>();
    }

    @Override
    public void execute(UMLFileData file) throws Exception {
        ArrayList<Pair<String, String>> removalObjectID = new ArrayList<>();
        for (SequenceObjects objects : file.getSequenceByID(this.sequenceID).getObjects()) {
            if (objects.getClassName().equals(this.className)) {
                ArrayList<Integer> removalIDs = new ArrayList<>();
                Pair<String, String> objectName = new Pair<>(this.className, objects.getObjectName());
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
                removalObjectID.add(objectName);
                this.objectNames.add(objects.getObjectName());
            }
        }
        for (Pair<String, String> objectName : removalObjectID) {
            file.getSequenceByID(this.sequenceID).removeObject(objectName);
        }
    }

    @Override
    public void undo(UMLFileData file) throws Exception {
        for (String oName : this.objectNames) {
            Pair<String, String> objectName = new Pair<>(this.className, oName);
            file.getSequenceByID(this.sequenceID).addObject(objectName);
        }
        for (SequenceMessages restore : this.removedMessages) {
            int newID = file.getSequenceByID(this.sequenceID).addMessage(restore.getContent());
            file.getSequenceByID(this.sequenceID).getMessageByID(newID).setType(restore.getType());
            file.getSequenceByID(this.sequenceID).getMessageByID(newID).setParticipants(restore.getSender(), restore.getReceiver());
        }
    }
}

/*
 * File: RemoveSequenceDiagramObjectCommand.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.commands;

import cz.vutfit.umlapp.model.uml.SequenceMessages;
import cz.vutfit.umlapp.model.uml.UMLFileData;
import javafx.util.Pair;

import java.util.ArrayList;

public class RemoveSequenceDiagramObjectCommand implements ICommand {
    private final int sequenceID;
    private final String objectName;
    private final String className;

    public RemoveSequenceDiagramObjectCommand(int sequenceID, String className, String objectName) {
        this.sequenceID = sequenceID;
        this.objectName = objectName;
        this.className = className;
    }

    @Override
    public void execute(UMLFileData file) throws Exception {
        ArrayList<Integer> removalIDs = new ArrayList<>();
        Pair<String, String> objectName = new Pair<>(this.className, this.objectName);
        for (SequenceMessages m : file.getSequenceByID(this.sequenceID).getMessages()) {
            if (m.getSender().equals(objectName) || m.getReceiver().equals(objectName)) {
                removalIDs.add(m.getID());
            }
        }
        for (int id : removalIDs) {
            file.getSequenceByID(this.sequenceID).removeMessage(id);
        }
        file.getSequenceByID(this.sequenceID).removeObject(objectName);
    }

    @Override
    public void undo(UMLFileData file) throws Exception {
        Pair<String, String> objectName = new Pair<>(this.className, this.objectName);
        file.getSequenceByID(this.sequenceID).addObject(objectName);
        System.out.println("RemoveSequenceDiagramObjectCommand: Operation undo completed only partially. (messages not restored)");
    }
}

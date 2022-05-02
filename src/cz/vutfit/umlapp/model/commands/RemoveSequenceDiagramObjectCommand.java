/*
 * File: RemoveSequenceDiagramObjectCommand.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.commands;

import cz.vutfit.umlapp.model.uml.SequenceMessages;
import cz.vutfit.umlapp.model.uml.UMLFileData;

import java.util.ArrayList;

public class RemoveSequenceDiagramObjectCommand implements ICommand {
    private final int sequenceID;
    private final String objectName;

    public RemoveSequenceDiagramObjectCommand(int sequenceID, String objectName) {
        this.sequenceID = sequenceID;
        this.objectName = objectName;
    }

    @Override
    public void execute(UMLFileData file) throws Exception {
        ArrayList<Integer> removalIDs = new ArrayList<>();
        for (SequenceMessages m : file.getSequenceByID(this.sequenceID).getMessages()) {
            if (m.getSender().equals(this.objectName) || m.getReceiver().equals(this.objectName)) {
                removalIDs.add(m.getID());
            }
        }
        for (int id : removalIDs) {
            file.getSequenceByID(this.sequenceID).removeMessage(id);
        }
        file.getSequenceByID(this.sequenceID).removeObject(this.objectName);
    }

    @Override
    public void undo(UMLFileData file) throws Exception {
        file.getSequenceByID(this.sequenceID).addObject(this.objectName);
        System.out.println("RemoveSequenceDiagramObjectCommand: Operation undo completed only partially. (messages not restored)");
    }
}

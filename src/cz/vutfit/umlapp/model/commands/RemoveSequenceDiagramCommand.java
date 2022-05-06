/*
 * File: RemoveSequenceDiagramCommand.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.commands;

import cz.vutfit.umlapp.model.uml.SequenceDiagram;
import cz.vutfit.umlapp.model.uml.UMLFileData;

public class RemoveSequenceDiagramCommand implements ICommand {
    private final int seqID;
    private SequenceDiagram removedSequence;

    public RemoveSequenceDiagramCommand(int ID) {
        this.seqID = ID;
    }

    @Override
    public void execute(UMLFileData file) {
        SequenceDiagram x = file.getSequenceByID(this.seqID);
        String name = x.getName();
        this.removedSequence = new SequenceDiagram(this.seqID, name);
        this.removedSequence.setAll(x.getObjects(), x.getMessages());
        file.removeSequence(this.seqID);
    }

    @Override
    public void undo(UMLFileData file) {
        if (removedSequence != null) {
            try {
                file.addSequence(this.removedSequence.getName());
            } catch (Exception e) {
                System.out.println("Some exception in RemoveSequenceDiagramCommand happened");
            }
            file.getSequenceByID(this.seqID).setAll(this.removedSequence.getObjects(), this.removedSequence.getMessages());
        }
    }
}

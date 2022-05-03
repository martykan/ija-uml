/*
 * File: EditSequenceDiagramMessageContentCommand.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.commands;

import cz.vutfit.umlapp.model.uml.SequenceDiagram;
import cz.vutfit.umlapp.model.uml.SequenceMessages;
import cz.vutfit.umlapp.model.uml.SequenceObjects;
import cz.vutfit.umlapp.model.uml.UMLFileData;

import java.util.ArrayList;
import java.util.Collections;

public class EditSequenceDiagramObjectIndexCommand implements ICommand {
    private final int sequenceID;
    private final int oldIndex;
    private final int newIndex;

    public EditSequenceDiagramObjectIndexCommand(Integer sequenceID, Integer oldIndex, Integer newIndex) {
        this.sequenceID = sequenceID;
        this.newIndex = newIndex;
        this.oldIndex = oldIndex;
    }

    @Override
    public void execute(UMLFileData file) {
        SequenceDiagram currentDiagram = file.getSequenceByID(sequenceID);
        ArrayList<SequenceObjects> objects = currentDiagram.getObjects();
        Collections.swap(objects, oldIndex, newIndex);
    }

    @Override
    public void undo(UMLFileData file) {
        SequenceDiagram currentDiagram = file.getSequenceByID(sequenceID);
        ArrayList<SequenceObjects> objects = currentDiagram.getObjects();
        Collections.swap(objects, oldIndex, newIndex);
    }
}

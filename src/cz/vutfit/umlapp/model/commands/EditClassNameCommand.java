/*
 * File: AddClassAttributeCommand.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.commands;

import cz.vutfit.umlapp.model.uml.*;
import javafx.util.Pair;


public class EditClassNameCommand implements ICommand {
    private final int classID;
    private String oldClassName;
    private final String newClassName;

    public EditClassNameCommand(Integer classID, String newClassName) {
        this.classID = classID;
        this.newClassName = newClassName;
    }

    @Override
    public void execute(UMLFileData file) {
        oldClassName = file.getClassByID(classID).getName();
        file.getClassByID(classID).setName(newClassName);

        for (SequenceDiagram seq : file.getSequenceDiagrams()) {
            for (SequenceObjects obj : seq.getObjects()) {
                if (obj.getClassName().equals(oldClassName))
                    obj.setClassName(this.newClassName);
            }
            for (SequenceMessages mes : seq.getMessages()) {
                if (mes.getSender().getKey().equals(oldClassName))
                    mes.setParticipants(new Pair<>(this.newClassName, mes.getSender().getValue()), mes.getReceiver());
                if (mes.getReceiver().getKey().equals(oldClassName))
                    mes.setParticipants(mes.getSender(), new Pair<>(this.newClassName, mes.getReceiver().getValue()));
            }
        }
    }

    @Override
    public void undo(UMLFileData file) {
        file.getClassByID(classID).setName(oldClassName);

        for (SequenceDiagram seq : file.getSequenceDiagrams()) {
            for (SequenceObjects obj : seq.getObjects()) {
                if (obj.getClassName().equals(newClassName))
                    obj.setClassName(this.oldClassName);
            }
            for (SequenceMessages mes : seq.getMessages()) {
                if (mes.getSender().getKey().equals(newClassName))
                    mes.setParticipants(new Pair<>(this.oldClassName, mes.getSender().getValue()), mes.getReceiver());
                if (mes.getReceiver().getKey().equals(newClassName))
                    mes.setParticipants(mes.getSender(), new Pair<>(this.oldClassName, mes.getReceiver().getValue()));
            }
        }
    }
}

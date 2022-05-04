/*
 * File: AddClassAttributeCommand.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.commands;

import cz.vutfit.umlapp.model.uml.EAttribVisibility;
import cz.vutfit.umlapp.model.uml.SequenceDiagram;
import cz.vutfit.umlapp.model.uml.SequenceObjects;
import cz.vutfit.umlapp.model.uml.UMLFileData;


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
        }
    }
}

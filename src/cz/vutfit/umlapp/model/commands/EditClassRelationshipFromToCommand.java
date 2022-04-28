/*
 * File: AddClassAttributeCommand.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.commands;

import cz.vutfit.umlapp.model.uml.Attributes;
import cz.vutfit.umlapp.model.uml.EAttribVisibility;
import cz.vutfit.umlapp.model.uml.UMLFileData;
import cz.vutfit.umlapp.model.uml.exceptions.DuplicateRelationshipException;

public class EditClassRelationshipFromToCommand implements ICommand {
    private final int relID;
    private final int fromID;
    private final int toID;
    private int oldFromID;
    private int oldToID;

    public EditClassRelationshipFromToCommand(Integer relationID, Integer fromID, Integer toID) {
        this.relID = relationID;
        this.fromID = fromID;
        this.toID = toID;
    }

    @Override
    public void execute(UMLFileData file) throws Exception {
        this.oldFromID = file.getRelationByID(this.relID).getFromClassID();
        this.oldToID = file.getRelationByID(this.relID).getToClassID();
        if (file.checkRelationshipDuplicates(this.fromID, this.toID)) {
            file.getRelationByID(this.relID).setToClassID(this.toID);
            file.getRelationByID(this.relID).setFromClassID(this.toID);
        } else {
            throw new DuplicateRelationshipException();
        }
    }

    @Override
    public void undo(UMLFileData file) {
        file.getRelationByID(this.relID).setToClassID(this.oldToID);
        file.getRelationByID(this.relID).setFromClassID(this.oldFromID);
    }
}

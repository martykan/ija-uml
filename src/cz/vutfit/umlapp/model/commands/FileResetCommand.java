/*
 * File: FileResetCommand.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.commands;

import cz.vutfit.umlapp.model.uml.*;

import java.util.ArrayList;

public class FileResetCommand implements ICommand {
    private final UMLFileData oldFile;
    private ArrayList<ClassDiagram> oldClasses;
    private ArrayList<SequenceDiagram> oldSequences;
    private ArrayList<Relationships> oldRelationships;

    public FileResetCommand(UMLFileData file) {
        this.oldFile = file;
    }

    @Override
    public void execute(UMLFileData file) {
        this.oldClasses = new ArrayList<>(file.getClasses());
        this.oldRelationships = new ArrayList<>(file.getRelationships());
        this.oldSequences = new ArrayList<>(file.getSequenceDiagrams());

        file.getClasses().clear();
        file.getRelationships().clear();
        file.getSequenceDiagrams().clear();
    }

    @Override
    public void undo(UMLFileData file) {
        file.getClasses().addAll(this.oldClasses);
        file.getRelationships().addAll(this.oldRelationships);
        file.getSequenceDiagrams().addAll(this.oldSequences);
    }
}

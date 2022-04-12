/*
 * File: ICommand.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.commands;

import cz.vutfit.umlapp.model.uml.UMLFileData;

/**
 * Interface for commands applied in View
 */
public interface ICommand {
    void execute(UMLFileData file) throws Exception;

    void undo(UMLFileData file) throws Exception;
}

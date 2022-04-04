package cz.vutfit.umlapp.model.commands;

import cz.vutfit.umlapp.model.uml.UMLFileData;

public interface ICommand {
    void execute(UMLFileData file);

    void undo(UMLFileData file);
}

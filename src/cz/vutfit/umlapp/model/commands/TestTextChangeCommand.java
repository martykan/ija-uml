package cz.vutfit.umlapp.model.commands;

import cz.vutfit.umlapp.model.uml.UMLFileData;

public class TestTextChangeCommand implements ICommand {
    private final String newText;
    private final String oldText;

    public TestTextChangeCommand(String oldText, String newText) {
        this.oldText = oldText;
        this.newText = newText;
    }


    @Override
    public void execute(UMLFileData file) {
        file.test = newText;
    }

    @Override
    public void undo(UMLFileData file) {
        file.test = oldText;
    }
}

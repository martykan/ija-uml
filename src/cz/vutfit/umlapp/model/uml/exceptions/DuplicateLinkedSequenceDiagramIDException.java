package cz.vutfit.umlapp.model.uml.exceptions;

public class DuplicateLinkedSequenceDiagramIDException extends Throwable {
    @Override
    public String getMessage() {
        return "Duplicate linked sequence diagram ID";
    }
}

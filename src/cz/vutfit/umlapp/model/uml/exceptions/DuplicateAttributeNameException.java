package cz.vutfit.umlapp.model.uml.exceptions;

public class DuplicateAttributeNameException extends Throwable {
    @Override
    public String getMessage() {
        return "Duplicate attribute name";
    }
}

package cz.vutfit.umlapp.model.uml.exceptions;

public class DuplicateMethodNameException extends Exception {
    public DuplicateMethodNameException() {
        super("Duplicate method name");
    }
}

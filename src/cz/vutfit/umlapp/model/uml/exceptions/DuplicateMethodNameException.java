package cz.vutfit.umlapp.model.uml.exceptions;

public class DuplicateMethodNameException extends Throwable {
    @Override
    public String getMessage() {
        return "Duplicate method name";
    }
}

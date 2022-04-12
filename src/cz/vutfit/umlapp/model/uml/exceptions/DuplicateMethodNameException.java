/*
 * File: DuplicateMethodNameException.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.uml.exceptions;

/**
 * Class for exception to duplicate name of method
 */
public class DuplicateMethodNameException extends Exception {
    public DuplicateMethodNameException() {
        super("Duplicate method name");
    }
}

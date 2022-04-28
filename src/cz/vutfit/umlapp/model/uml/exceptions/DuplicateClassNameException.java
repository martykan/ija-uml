/*
 * File: DuplicateClassNameException.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.uml.exceptions;

/**
 * Class for exception to duplicate name of class
 */
public class DuplicateClassNameException extends Exception {
    public DuplicateClassNameException() {
        super("Class with this name already exists");
    }
}

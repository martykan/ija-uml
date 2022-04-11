/*
 * File: DuplicateClassNameException.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.uml.exceptions;

public class DuplicateClassNameException extends Exception {
    public DuplicateClassNameException() {
        super("Duplicate class name");
    }
}

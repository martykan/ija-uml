/*
 * File: DuplicateObjectException.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.uml.exceptions;

public class DuplicateObjectException extends Exception {
    public DuplicateObjectException() {
        super("Object (with this name) in this sequence diagram already exists");
    }
}

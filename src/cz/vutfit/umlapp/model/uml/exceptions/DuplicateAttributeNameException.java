/*
 * File: DuplicateAttributeNameException.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.uml.exceptions;

/**
 * Class for exception to duplicate name of attribute
 */
public class DuplicateAttributeNameException extends Exception {
    public DuplicateAttributeNameException() {
        super("Attribute in this class with this name already exists");
    }
}

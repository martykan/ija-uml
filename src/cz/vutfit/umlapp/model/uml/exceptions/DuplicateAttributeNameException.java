/*
 * File: DuplicateAttributeNameException.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.uml.exceptions;

public class DuplicateAttributeNameException extends Exception {
    public DuplicateAttributeNameException() {
        super("Duplicate attribute name");
    }
}

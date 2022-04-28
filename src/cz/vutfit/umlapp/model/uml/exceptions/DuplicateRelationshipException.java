/*
 * File: DuplicateAttributeNameException.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.uml.exceptions;

/**
 * Class for exception to duplicate name of attribute
 */
public class DuplicateRelationshipException extends Exception {
    public DuplicateRelationshipException() {
        super("Relationship with same 'from class' and 'to class' already exists");
    }
}

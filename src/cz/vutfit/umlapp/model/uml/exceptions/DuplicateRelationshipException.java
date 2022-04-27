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
        super("Duplicate relationship (relationship with 'from class' and 'to class' already exists)");
    }
}

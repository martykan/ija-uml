/*
 * File: DuplicateLinkedSequenceDiagramIDException.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.uml.exceptions;

/**
 * Class for exception to duplicate ID of linked sequence diagram
 */
public class DuplicateLinkedSequenceDiagramIDException extends Exception {
    public DuplicateLinkedSequenceDiagramIDException() {
        super("Duplicate linked sequence diagram ID");
    }
}

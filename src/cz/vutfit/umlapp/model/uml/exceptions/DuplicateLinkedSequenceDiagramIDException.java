/*
 * File: DuplicateLinkedSequenceDiagramIDException.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.uml.exceptions;

public class DuplicateLinkedSequenceDiagramIDException extends Exception {
    public DuplicateLinkedSequenceDiagramIDException() {
        super("Duplicate linked sequence diagram ID");
    }
}

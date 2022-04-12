/*
 * File: SequenceDiagram.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.uml;

/**
 * TODO
 * Class for one sequence diagram
 */
public class SequenceDiagram {
    public int id;
    public String name;

    /** Getters **/
    public int getID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    /** Constructor **/
    public SequenceDiagram() {
    }

    /**
     * TODO
     * Constructor of new sequence diagram
     * @param id id of diagram
     * @param name name of diagram
     */
    public SequenceDiagram(int id, String name) {
        this.id = id;
        this.name = name;
    }
}

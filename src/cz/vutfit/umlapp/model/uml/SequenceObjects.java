/*
 * File: SequenceObjects.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.uml;

public class SequenceObjects {
    public String name;

    public SequenceObjects (String name) {
        this.name = name;
    }

    public String getName() { return this.name; }

    public void setName(String name) { this.name = name; }
}

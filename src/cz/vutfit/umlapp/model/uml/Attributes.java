/*
 * File: Attributes.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.uml;

/**
 * Class for Class diagram - Class Attributes
 */
public class Attributes {
    public String name;
    public EAttribVisibility visibility;
    public EClassElementType type;

    /** getters **/
    public String getName() {
        return this.name;
    }

    public EAttribVisibility getVisibility() {
        return this.visibility;
    }

    public EClassElementType getType() {
        return this.type;
    }

    public String getNameWithPrefix() {
        return this.getVisibility().getPrefix() + this.getName();
    }

    /** setters **/
    public void setName(String name) {
        this.name = name;
    }

    public void setVisibility(EAttribVisibility value) {
        this.visibility = value;
    }

    public void setType(EClassElementType type) { this.type = type; }

    /**
     * Sets all values in one function
     * @param name
     * @param value
     */
    public void setAttribute(String name, EAttribVisibility value, EClassElementType type) {
        this.visibility = value;
        this.name = name;
        this.type = type;
    }
}

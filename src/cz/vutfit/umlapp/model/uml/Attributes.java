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
    public String type;

    /** getters **/
    public String getName() {
        return this.name;
    }

    public EAttribVisibility getVisibility() {
        return this.visibility;
    }

    public String getType() {
        return this.type;
    }

    public String getNameWithPrefix() {
        return this.getVisibility().getPrefix() + this.getName();
    }

    public String getNameWithPrefixWithType() {
        if (this.getType() != null)
            return this.getNameWithPrefix() + ":" + this.getType();
        return this.getNameWithPrefix();
    }

    /**
     * setters
     **/
    public void setName(String name) {
        this.name = name;
    }

    public void setVisibility(EAttribVisibility value) {
        this.visibility = value;
    }

    public void setType(String type) { this.type = type; }

    /**
     * Sets all values in one function
     * @param name
     * @param value
     */
    public void setAttribute(String name, EAttribVisibility value, String type) {
        this.visibility = value;
        this.name = name;
        this.type = type;
    }
}

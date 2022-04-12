/*
 * File: Methods.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.uml;

/**
 * Class for methods in class
 */
public class Methods {
    public String name;
    public EAttribVisibility visibility;

    /** Getters **/
    public String getName() {
        return this.name;
    }

    public EAttribVisibility getVisibility() {
        return this.visibility;
    }

    public String getNameWithPrefix() {
        return this.getVisibility().getPrefix() + this.getName() + "()";
    }

    /** Setters **/
    public void setName(String name) {
        this.name = name;
    }

    public void setVisibility(EAttribVisibility value) {
        this.visibility = value;
    }

    /**
     * Sets all values in one function
     * @param name
     * @param value
     * @see EAttribVisibility
     */
    public void setMethod(String name, EAttribVisibility value) {
        this.visibility = value;
        this.name = name;
    }
}

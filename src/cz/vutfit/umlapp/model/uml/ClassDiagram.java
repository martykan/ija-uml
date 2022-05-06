/*
 * File: ClassDiagram.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.uml;

import cz.vutfit.umlapp.model.uml.exceptions.DuplicateAttributeNameException;
import cz.vutfit.umlapp.model.uml.exceptions.DuplicateMethodNameException;

import java.util.ArrayList;

/**
 * Class representing one class in Class diagram
 */
public class ClassDiagram {
    public Integer id;
    public String name;
    public ArrayList<Attributes> attribs;
    public ArrayList<Methods> methods;
    public Double positionX = 5000.0; /** X-axis position in View **/
    public Double positionY = 5000.0; /** Y-axis position in View **/

    public ClassDiagram() {
    }

    /**
     * Constructor - with name
     * @param id
     * @param name
     */
    public ClassDiagram(Integer id, String name) {
        this.id = id;
        this.name = name;
        this.attribs = new ArrayList<>();
        this.methods = new ArrayList<>();
    }

    /**
     * Constructor - unnamed class
     * @param id
     */
    public ClassDiagram(Integer id) {
        this.id = id;
        this.name = "Unnamed class";
        this.attribs = new ArrayList<>();
        this.methods = new ArrayList<>();
    }

    /** Getters **/
    public int getID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public ArrayList<Attributes> getAttribs() {
        return this.attribs;
    }

    public ArrayList<Methods> getMethods() {
        return this.methods;
    }

    /**
     * Setters
     **/
    public void setAll(String name, ArrayList<Attributes> attribs, ArrayList<Methods> methods) {
        this.name = name;
        this.attribs = attribs;
        this.methods = methods;
    }

    public void setName(String name) { this.name = name; }

    /**
     * Gets one attribute from list
     * @param name name of attribute
     * @return attribute or null if not found by name
     */
    public Attributes getAttribute(String name) {
        for (Attributes x : this.attribs) {
            if (x.getName().equals(name))
                return x;
        }
        return null;
    }

    /**
     * Adds new attribute to list
     * @param name
     * @param visibility
     * @throws DuplicateAttributeNameException if new attribute has same name as any other existing
     * @see EAttribVisibility
     */
    public void addAttribute(String name, EAttribVisibility visibility, String type) throws DuplicateAttributeNameException {
        Attributes x = new Attributes();
        x.setAttribute(name, visibility, type);

        for (Attributes a : this.attribs) { // do not allow duplicates - same "name"'s
            if (a.getName().equals(x.getName())) {
                throw new DuplicateAttributeNameException();
            }
        }

        (this.attribs).add(x);
    }

    /**
     * Removes attribute from list
     * @param name
     * @return true if found and removed, false if not found by name
     */
    public boolean removeAttribute(String name) {
        for (int i = 0; i < (this.attribs).size(); i++) {
            if ((this.attribs).get(i).getName().equals(name)) {
                (this.attribs).remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * @param name full name with parenthesis
     * @return method if found or null
     */
    public Methods getMethod(String name) {
        for (Methods x : this.methods) {
            if (x.getName().equals(name))
                return x;
        }
        return null;
    }

    /**
     * @param name method name
     * @return method if found or null
     */
    public Methods getMethodByNameOnly(String name) {
        for (Methods x : this.methods) {
            if (x.getName().startsWith(name + "("))
                return x;
        }
        return null;
    }

    /**
     * Adds new method to methods list.
     *
     * @param name
     * @param visibility
     * @throws DuplicateMethodNameException if new method has same name as any other existing
     * @see EAttribVisibility
     */
    public void addMethod(String name, EAttribVisibility visibility, String type) throws DuplicateMethodNameException {
        Methods x = new Methods();
        x.setMethod(name, visibility, type);

        for (Methods a : this.methods) { // do not allow duplicates - same "name"'s
            if (a.getName().equals(x.getName())) {
                throw new DuplicateMethodNameException();
            }
        }

        (this.methods).add(x);
    }

    /**
     * Removes method from methods list.
     * @param name
     * @return true if removed or false if not found by name
     */
    public boolean removeMethod(String name) {
        for (int i = 0; i < (this.methods).size(); i++) {
            if ((this.methods).get(i).getName().equals(name)) {
                (this.methods).remove(i);
                return true;
            }
        }
        return false;
    }
}
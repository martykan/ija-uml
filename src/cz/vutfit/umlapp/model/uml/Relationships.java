/*
 * File: Relationships.java
 * Authors: Dominik Horký, Tomáš Martykán
 */


package cz.vutfit.umlapp.model.uml;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for relationship between two classes
 */
public class Relationships {
    public Integer id;
    /**
     * ID of class from which relationship starts
     **/
    public Integer fromId;
    /**
     * ID of class to which relationship goes
     **/
    public Integer toId;
    /**
     * type of relationship
     **/
    public ERelationType relationship;
    /**
     * additional description of relationship in fromID part
     **/
    public String fromDesc;
    /**
     * additional description of relationship in toID part
     **/
    public String toDesc;
    /**
     * midpoints of the relationship line
     **/
    public List<Point2D> lineMidpoints = new ArrayList<>();

    /**
     * Constructor
     */
    public Relationships() {
    }

    /**
     * Constructor.
     * Additional descriptions (fromDesc, toDesc) are set to null.
     * @param id id of relationship
     * @param fromId id of class from relationship starts
     * @param toId id of class to relationship goes
     * @param type type of relationship
     * @see #fromId
     * @see #toId
     * @see ERelationType
     */
    public Relationships(Integer id, Integer fromId, Integer toId, ERelationType type) {
        this.id = id;
        this.fromId = fromId;
        this.toId = toId;
        this.relationship = type;
        this.fromDesc = null;
        this.toDesc = null;
    }

    /** Getters **/
    public int getID() {
        return this.id;
    }

    public String getFromDesc() {
        return this.fromDesc;
    }

    public String getToDesc() {
        return this.toDesc;
    }

    public ERelationType getType() {
        return this.relationship;
    }

    public int getFromClassID() {
        return this.fromId;
    }

    public int getToClassID() {
        return this.toId;
    }

    /** Setters **/
    public void setFromDesc(String text) {
        this.fromDesc = text;
    }

    public void setToDesc(String text) {
        this.toDesc = text;
    }

    public void setType(ERelationType newType) {
        this.relationship = newType;
    }

    public void setFromClassID(Integer ID) {
        this.fromId = ID;
    }

    public void setToClassID(Integer ID) {
        this.toId = ID;
    }

    public void setAll(String fromDesc, String toDesc, ERelationType newType) {
        this.fromDesc = fromDesc;
        this.toDesc = toDesc;
        this.relationship = newType;
    }

    public void setAll(String fromDesc, String toDesc, ERelationType newType, int from, int to) {
        this.fromDesc = fromDesc;
        this.toDesc = toDesc;
        this.relationship = newType;
        this.fromId = from;
        this.toId = to;
    }
}

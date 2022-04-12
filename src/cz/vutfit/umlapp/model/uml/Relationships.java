/*
 * File: Relationships.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.uml;

/**
 * TODO
 * Enumeration for relation type between two classes
 */
enum ERelationType {
    ASSOCIATION,
    AGGREGATION,
    COMPOSITION,
    GENERALIZATION
}

/**
 * TODO
 * Class for relationship between two classes
 */
public class Relationships {
    public Integer id;
    public Integer fromId;  /** ID of class from which relationship starts **/
    public Integer toId;    /** ID of class to which relationship goes **/
    public ERelationType relationship;  /** type of relationship **/
    public String fromDesc; /** additional description of relationship in fromID part **/
    public String toDesc;   /** additional description of relationship in toID part   **/

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

    /** Setters **/
    public void setFromDesc(String text) {
        this.fromDesc = text;
    }

    public void setToDesc(String text) {
        this.toDesc = text;
    }
}

/*
 * File: SequenceMessages.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.uml;

import javafx.util.Pair;

public class SequenceMessages {
    public int ID;
    public String content;

    /**
     * message participant objects
     *  1st:    LEFT    (from/sender)
     *  2nd:    RIGHT   (to/receiver)
     */
    public Pair<String, String> fromObject;
    public Pair<String, String> toObject;
    public EMessageType type;

    /**
     * Constructor. If not given direction or message type, sets default value(s).
     *
     * Default values:
     *  direction    true
     *  type         NORMAL
     * @param ID
     * @param content
     */
    public SequenceMessages (int ID, String content) {
        this.ID = ID;
        this.content = content;
        this.fromObject = new Pair<String, String>(null, null);
        this.toObject = new Pair<String, String>(null, null);
        this.type = EMessageType.SYNC;
    }

    public int getID() { return this.ID; }

    public String getContent() { return this.content; }

    public Pair<String, String> getSender() { return this.fromObject; }
    public Pair<String, String> getReceiver() { return this.toObject; }

    public EMessageType getType() { return this.type; }

    public void setContent(String x) { this.content = x; }

    public void setParticipants(Pair<String, String> objectFrom, Pair<String, String> objectTo) {
        this.fromObject = objectFrom;
        this.toObject = objectTo;
    }

    public void setType(EMessageType x) { this.type = x; }
}

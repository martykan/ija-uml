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
    public Pair<String, String> fromToPair;
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
        this.fromToPair = new Pair<String, String>(null, null);
        this.type = EMessageType.SYNC;
    }

    public int getID() { return this.ID; }

    public String getContent() { return this.content; }

    public Pair<String, String> getParticipants() { return this.fromToPair; }
    public String getSender() { return this.fromToPair.getKey(); }
    public String getReceiver() { return this.fromToPair.getValue(); }

    public EMessageType getType() { return this.type; }

    public void setContent(String x) { this.content = x; }

    public void setParticipants(String from, String to) { this.fromToPair = new Pair<String, String>(from, to); }
    public void setParticipants(Pair<String, String> fromToPair) { this.fromToPair = fromToPair; }

    public void setType(EMessageType x) { this.type = x; }
}

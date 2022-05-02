/*
 * File: SequenceMessages.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.uml;

public class SequenceMessages {
    public int ID;
    public String content;

    /**
     * message direction
     *  true: A -> B;
     *  false: A <- B;
     */
    public boolean direction;
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
        this.direction = true;
        this.type = EMessageType.SYNC;
    }

    public SequenceMessages (int ID, String content, boolean direction) {
        this.ID = ID;
        this.content = content;
        this.direction = direction;
        this.type = EMessageType.SYNC;
    }

    public SequenceMessages (int ID, String content, boolean direction, EMessageType type) {
        this.ID = ID;
        this.content = content;
        this.direction = direction;
        this.type = type;
    }

    public int getID() { return this.ID; }

    public String getContent() { return this.content; }

    public boolean getDirection() { return this.direction; }

    public EMessageType getType() { return this.type; }

    public void setContent(String x) { this.content = x; }

    public void setDirection(boolean x) { this.direction = x; }

    public void setType(EMessageType x) { this.type = x; }
}

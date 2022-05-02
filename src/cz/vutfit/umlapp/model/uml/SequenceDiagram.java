/*
 * File: SequenceDiagram.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.uml;

import cz.vutfit.umlapp.model.uml.exceptions.DuplicateObjectException;

import java.util.ArrayList;

/**
 * TODO
 * Class for one sequence diagram
 */
public class SequenceDiagram {
    public int id;
    public String name;
    public ArrayList<SequenceObjects> objects = new ArrayList<>();
    public ArrayList<SequenceMessages> messages = new ArrayList<>();

    /** Constructor **/
    public SequenceDiagram() {
    }

    /**
     * Constructor of new sequence diagram
     * @param id id of diagram
     * @param name name of diagram
     */
    public SequenceDiagram(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /** Getters **/
    public int getID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public ArrayList<SequenceObjects> getObjects() { return this.objects; }

    public ArrayList<SequenceMessages> getMessages() { return this.messages; }

    /** Setters **/
    public void setName(String x) { this.name = x; }

    public void setAll(ArrayList<SequenceObjects> objects, ArrayList<SequenceMessages> messages) {
        this.objects = objects;
        this.messages = messages;
    }

    /** Working with object list **/
    public void addObject(String name) throws DuplicateObjectException {
        SequenceObjects x = new SequenceObjects(name);

        for (SequenceObjects a : this.objects) { // do not allow duplicates - same "name"'s
            if (a.getName().equals(x.getName())) {
                throw new DuplicateObjectException();
            }
        }

        (this.objects).add(x);
    }

    // returns true if removed, false if not found
    public boolean removeObject(String name) {
        for (int i = 0; i < (this.objects).size(); i++) {
            if ((this.objects).get(i).getName().equals(name)) {
                (this.objects).remove(i);
                return true;
            }
        }
        return false;
    }

    // returns object or null
    public SequenceObjects getObject(String name) {
        for (SequenceObjects x : this.objects) {
            if (x.getName().equals(name))
                return x;
        }
        return null;
    }

    /** Working with message list **/
    public int addMessage(String content) {
        int id = 0;
        if (id != (this.messages).size()) {
            int i = 0;
            boolean found = false;
            for (SequenceMessages c : this.messages) {
                if (i != c.getID()) {
                    id = i;
                    found = true;
                    break;
                }
                i++;
            }
            if (id == 0 && !found) id = (this.messages).size();
        }

        SequenceMessages x = new SequenceMessages(id, content);
        (this.messages).add(x);
        return id;
    }

    // returns true if removed or false if not found
    public boolean removeMessage(int ID) {
        for (SequenceMessages x : this.messages) {
            if (x.getID() == ID) {
                (this.messages).remove(x);
                return true;
            }
        }
        return false;
    }

    public SequenceMessages getMessageByID(int ID) {
        for (SequenceMessages x : this.messages) {
            if (x.getID() == ID) {
                return x;
            }
        }
        return null;
    }

    public SequenceMessages getMessageByIndex(int i) {
        return (this.messages).get(i);
    }
}

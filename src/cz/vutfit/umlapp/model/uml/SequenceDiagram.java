/*
 * File: SequenceDiagram.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.uml;

import cz.vutfit.umlapp.model.uml.exceptions.DuplicateObjectException;
import javafx.util.Pair;

import java.util.ArrayList;

/**
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
    public void addObject(String className, String objectName) throws DuplicateObjectException {
        SequenceObjects x = new SequenceObjects(className, objectName);
        for (SequenceObjects a : this.objects) { // do not allow duplicates - same "name"'s
            if (a.getClassName().equals(x.getClassName()) && a.getObjectName().equals(x.getObjectName())) {
                throw new DuplicateObjectException();
            }
        }
        (this.objects).add(x);
    }

    public void addObject(Pair<String, String> classObjectName) throws DuplicateObjectException {
        SequenceObjects x = new SequenceObjects(classObjectName);
        for (SequenceObjects a : this.objects) { // do not allow duplicates - same "name"'s
            if (a.getClassName().equals(x.getClassName()) && a.getObjectName().equals(x.getObjectName())) {
                throw new DuplicateObjectException();
            }
        }
        (this.objects).add(x);
    }

    public void addObjectToIndex(Pair<String, String> classObjectName, int lastIndex) throws DuplicateObjectException {
        SequenceObjects x = new SequenceObjects(classObjectName);
        for (SequenceObjects a : this.objects) { // do not allow duplicates - same "name"'s
            if (a.getClassName().equals(x.getClassName()) && a.getObjectName().equals(x.getObjectName())) {
                throw new DuplicateObjectException();
            }
        }
        (this.objects).add(lastIndex, x);
    }

    // returns true if removed, false if not found
    public boolean removeObject(String className, String objectName) {
        for (int i = 0; i < (this.objects).size(); i++) {
            if ((this.objects).get(i).getClassName().equals(className) && (this.objects).get(i).getObjectName().equals(objectName)) {
                (this.objects).remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean removeObject(Pair<String, String> classObjectName) {
        String className = classObjectName.getKey();
        String objectName = classObjectName.getValue();
        for (int i = 0; i < (this.objects).size(); i++) {
            if ((this.objects).get(i).getClassName().equals(className) && (this.objects).get(i).getObjectName().equals(objectName)) {
                (this.objects).remove(i);
                return true;
            }
        }
        return false;
    }

    // returns object or null
    public SequenceObjects getObject(Pair<String, String> classObjectName) {
        for (SequenceObjects x : this.objects) {
            if (x.getClassName().equals(classObjectName.getKey()) && x.getObjectName().equals(classObjectName.getValue()))
                return x;
        }
        return null;
    }

    public SequenceObjects getObject(String className, String objectName) {
        for (SequenceObjects x : this.objects) {
            if (x.getClassName().equals(className) && x.getObjectName().equals(objectName))
                return x;
        }
        return null;
    }

    public int getObjectIndex(String className, String objectName) {
        int i = 0;
        for (SequenceObjects x : this.objects) {
            if (x.getClassName().equals(className) && x.getObjectName().equals(objectName))
                return i;
            i++;
        }
        return -1;
    }

    public SequenceObjects getObjectByIndex(int index) {
        return this.getObjects().get(index);
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

    public int addMessageToIndex(String content, int lastIndex) {
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
        (this.messages).add(lastIndex, x);

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

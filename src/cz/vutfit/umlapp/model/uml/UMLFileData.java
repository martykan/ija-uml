/*
 * File: UMLFileData.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.uml;

import cz.vutfit.umlapp.model.uml.exceptions.DuplicateClassNameException;
import cz.vutfit.umlapp.model.uml.exceptions.DuplicateRelationshipException;

import java.util.ArrayList;

/**
 * Root class for data in file.
 * @see ClassDiagram
 * @see SequenceDiagram
 * @see Relationships
 */
public class UMLFileData {
    /**
     * ArrayList of classes in class diagram
     */
    public final ArrayList<ClassDiagram> classDiagram = new ArrayList<>();

    /**
     * ArrayList of sequence diagrams
     */
    public final ArrayList<SequenceDiagram> sequenceDiagrams = new ArrayList<>();

    /**
     * ArrayList of relationships
     */
    public final ArrayList<Relationships> relationships = new ArrayList<>();

    /**
     * Getter
     * @return arrayList of classes in class diagram
     */
    public ArrayList<ClassDiagram> getClasses() {
        return this.classDiagram;
    }

    /**
     * Getter
     * @return arrayList of sequence diagrams
     */
    public ArrayList<SequenceDiagram> getSequenceDiagrams() {
        return this.sequenceDiagrams;
    }

    /**
     * Getter
     * @return arrayList of relationships between classes
     */
    public ArrayList<Relationships> getRelationships() {
        return this.relationships;
    }

    /**
     * Note: index can be out of bonds or classDiagram array can be completely empty / null - cases are handled by user
     * @param index index of class in arrayList
     * @return class in that index
     */
    public ClassDiagram getClassByIndex(int index) {
        return (this.classDiagram).get(index);
    }

    /**
     * @param id internal identification number of class
     * @return class with this ID or null
     */
    public ClassDiagram getClassByID(int id) {
        for (ClassDiagram c : this.classDiagram) {
            if (c.getID() == id) {
                return c;
            }
        }
        return null;
    }

    /**
     * @param name name of the class
     * @return class with that name or null
     */
    public ClassDiagram getClassByName(String name) {
        for (ClassDiagram c : this.classDiagram) {
            if (c.getName().equals(name)) {
                return c;
            }
        }
        return null;
    }

    /**
     * Adds new class to classDiagram list.
     * Class ID is assigned automatically. Default value of ID is size of classDiagram list.
     * If list is not sorted by ID (sorted, ascending linearly), new class ID will be N + 1, where
     * N is last sorted class ID.
     * @param name name of new class
     * @return id of new class
     * @throws DuplicateClassNameException if new class has same name as any of existing one
     */
    public int addClass(String name) throws DuplicateClassNameException {
        System.out.print("addClass() - id: ");
        int id = 0;
        boolean found = false;
        if (id != (this.classDiagram).size()) { // there are classes in list
            int i = 0;
            for (ClassDiagram c : this.classDiagram) {  // check if classes ID's are sorted, linear, start with 0, end with size-1
                if (i != c.getID()) { // condition up is not true
                    id = i;
                    found = true;
                    break;
                }
                i++;
            }
            if (id == 0 && !found) id = (this.classDiagram).size(); // all sorted
        }

        // classes cannot have same name, so I need to check this too
        if (!checkClassNameDuplicates(name))
            throw new DuplicateClassNameException();


        ClassDiagram x = new ClassDiagram(id, name);
        System.out.println(String.valueOf(id).concat(", name: ").concat(name));
        (this.classDiagram).add(id, x);
        return id;
    }

    /**
     * Support function, checks for duplicate class names in entire classes array
     * [[ if given string (name) as first arguments, compares with that name - faster ]]
     */
    public boolean checkClassNameDuplicates() {
        int counter = 0;
        for (ClassDiagram fst : this.classDiagram) {
            for (ClassDiagram snd : this.classDiagram) {
                if (fst.getName().equals(snd.getName())) {
                    counter++;
                    if (counter > 1) {
                        return false;
                    }
                }
            }
            counter = 0;
        }
        return true;
    }

    public boolean checkClassNameDuplicates(String name) {
        for (ClassDiagram fst : this.classDiagram) {
            if (fst.getName().equals(name)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Remove class from list.
     * @param id ID of class
     * @return true if class with that ID was found and removed, false if class was not found
     */
    public boolean removeClass(int id) {
        System.out.print("removeClass() - id: ".concat(String.valueOf(id)).concat(", name: "));
        for (ClassDiagram c : this.classDiagram) {
            if (c.getID() == id) {
                (this.classDiagram).remove(c);
                System.out.println(c.getName());
                return true;
            }
        }
        return false;
    }

    /**
     * TODO
     * @param index index of array in sequenceDiagrams list
     * @return sequence diagram by index
     * @see #getClassByIndex(int)
     */
    public SequenceDiagram getSequenceByIndex(int index) {
        return (this.sequenceDiagrams).get(index);
    }

    /**
     * @param id ID of Sequence diagram
     * @return sequence diagram with that ID or null
     */
    public SequenceDiagram getSequenceByID(int id) {
        for (SequenceDiagram c : this.sequenceDiagrams) {
            if (c.getID() == id) {
                return c;
            }
        }
        return null;
    }

    public SequenceDiagram getSequenceByName(String name) {
        for (SequenceDiagram c : this.sequenceDiagrams) {
            if (c.getName().equals(name))
                return c;
        }
        return null;
    }

    /**
     * TODO
     * Adds sequence diagram to list.
     *
     * @param name name of diagram
     * @see #addClass(String)
     */
    public int addSequence(String name) {
        int id = 0;
        if (id != (this.sequenceDiagrams).size()) {
            int i = 0;
            boolean found = false;
            for (SequenceDiagram c : this.sequenceDiagrams) {
                if (i != c.getID()) {
                    id = i;
                    found = true;
                    break;
                }
                i++;
            }
            if (id == 0 && !found) id = (this.sequenceDiagrams).size();
        }

        SequenceDiagram x = new SequenceDiagram(id, name);
        (this.sequenceDiagrams).add(id, x);
        return id;
    }

    /**
     * Deleted sequence diagram from arrayList.
     * @param id ID of sequence diagram
     * @return true if successfully (found by ID and) removed, false if not found
     */
    public boolean removeSequence(int id) {
        for (SequenceDiagram c : this.sequenceDiagrams) {
            if (c.getID() == id) {
                (this.sequenceDiagrams).remove(id);
                return true;
            }
        }
        return false;
    }

    /**
     * @param index index of relationship in list
     * @return relationships (ArrayList)
     * @see #getClassByIndex(int)
     */
    public Relationships getRelationByIndex(int index) { return (this.relationships).get(index); }

    /**
     * @param id ID of relationship
     * @return relationship with that ID or null
     */
    public Relationships getRelationByID(int id) {
        for (Relationships c : this.relationships) {
            if (c.getID() == id) {
                return c;
            }
        }
        return null;
    }

    /**
     * Adds new relationship to relationships array.
     * @param fromId class ID from which relationship begins
     * @param toId class ID to which relationship goes
     * @param type type of relationship
     * @return ID of new relationship
     * @see ERelationType
     */
    public int addRelation(int fromId, int toId, ERelationType type) throws DuplicateRelationshipException {
        int id = 0;
        boolean found = false;
        if (id != (this.relationships).size()) {
            int i = 0;
            for (Relationships c : this.relationships) {
                if (i != c.getID()) {
                    id = i;
                    found = true;
                    break;
                }
                i++;
            }
            if (id == 0 && !found) id = (this.relationships).size();
        }

        // relationships cannot have same fromID, toID
        if (!checkRelationshipDuplicates(fromId, toId)) {
            throw new DuplicateRelationshipException();
        }

        Relationships x = new Relationships(id, fromId, toId, type);
        (this.relationships).add(id, x);
        return id;
    }

    /**
     * Looks for duplicate relationships
     * @param fromID ID of class from which relationship goes
     * @param toID ID of class to which relationship goes
     * @return true if no duplicates (check is OK) or false if found (check failed)
     */
    public boolean checkRelationshipDuplicates(int fromID, int toID) {
        for (Relationships fst : this.relationships) {
            if (fst.getFromClassID() == fromID && fst.getToClassID() == toID) {
                return false;
            }
        }
        return true;
    }

    /**
     * Removes relationship from list
     * @param id ID of relationship
     * @return true if removed or false if not found
     */
    public boolean removeRelation(int id) {
        for (Relationships c : this.relationships) {
            if (c.getID() == id) {
                (this.relationships).remove(c);
                return true;
            }
        }
        return false;
    }

    /**
     * Converts string to enum ERelationType (compatible with relationToString() method in ERelationType)
     * @param string valid string to be converted
     * @return enum value of string or null if invalid
     * @see ERelationType
     */
    public ERelationType stringToRelation(String string) {
        if (string.equals("Association")) {
            return ERelationType.ASSOCIATION;
        } else if (string.equals("Aggregation")) {
            return ERelationType.AGGREGATION;
        } else if (string.equals("Composition")) {
            return ERelationType.COMPOSITION;
        } else if (string.equals("Generalization")) {
            return ERelationType.GENERALIZATION;
        }
        return null;
    }
}













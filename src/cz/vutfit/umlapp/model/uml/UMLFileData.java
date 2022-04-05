package cz.vutfit.umlapp.model.uml;

import java.util.ArrayList;

public class UMLFileData {
    public ArrayList<ClassDiagram> class_diagram;
    public ArrayList<SequenceDiagram> sequence_diagram;
    public ArrayList<Relationships> relationships;

    // getters -> vraci listy
    public ArrayList<ClassDiagram> getClasses() { return this.class_diagram; }

    public ArrayList<SequenceDiagram> getSequenceDiagrams() { return this.sequence_diagram; }

    public ArrayList<Relationships> getRelationships() { return this.relationships; }

    // prace s listem trid v class diagramu
    public ClassDiagram getClassByIndex(int index) { return (this.class_diagram).get(index); }

    public ClassDiagram getClassByID(int id) {
        for (ClassDiagram c : this.class_diagram) {
            if (c.getID() == id) {
                return c;
            }
        }
        return null;
    }

    public void addClass(String name) {
        int id = 0;
        if (id != (this.class_diagram).size()) { // there are classes in list
            int i = 0;
            for (ClassDiagram c : this.class_diagram) {  // check if classes ID's are sorted, linear, start with 0, end with size-1
                if (i != c.getID()) { // condition up is not true
                    id = i;
                    break;
                }
                i++;
            }
            if (id == 0) id = (this.class_diagram).size(); // all sorted
        }

        ClassDiagram x = new ClassDiagram();
        x.classInit(id, name);
        (this.class_diagram).add(id, x);
    }

    public boolean removeClass(int id) {
        for (ClassDiagram c : this.class_diagram) {
            if (c.getID() == id) {
                (this.class_diagram).remove(id);
                return true;
            }
        }
        return false;
    }

    // prace s listem sekvencich diagramu !! TODO !!
    public SequenceDiagram getSequenceByIndex(int index) { return (this.sequence_diagram).get(index); }

    public SequenceDiagram getSequenceByID(int id) {
        for (SequenceDiagram c : this.sequence_diagram) {
            if (c.getID() == id) {
                return c;
            }
        }
        return null;
    }

    public void addSequence(String name) {
        int id = 0;
        if (id != (this.sequence_diagram).size()) {
            int i = 0;
            for (SequenceDiagram c : this.sequence_diagram) {
                if (i != c.getID()) {
                    id = i;
                    break;
                }
                i++;
            }
            if (id == 0) id = (this.sequence_diagram).size();
        }

        SequenceDiagram x = new SequenceDiagram();
        x.sequenceInit(id, name);
        (this.sequence_diagram).add(id, x);
    }

    public boolean removeSequence(int id) {
        for (SequenceDiagram c : this.sequence_diagram) {
            if (c.getID() == id) {
                (this.sequence_diagram).remove(id);
                return true;
            }
        }
        return false;
    }


    // prace s listem vztahu
    public Relationships getRelationByIndex(int index) { return (this.relationships).get(index); }

    public Relationships getRelationByID(int id) {
        for (Relationships c : this.relationships) {
            if (c.getID() == id) {
                return c;
            }
        }
        return null;
    }

    public void addRelation(int from_id, int to_id, relation_type type) {
        int id = 0;
        if (id != (this.relationships).size()) {
            int i = 0;
            for (Relationships c : this.relationships) {
                if (i != c.getID()) {
                    id = i;
                    break;
                }
                i++;
            }
            if (id == 0) id = (this.relationships).size();
        }

        Relationships x = new Relationships();
        x.relationshipInit(id, from_id, to_id, type);
        (this.relationships).add(id, x);
    }

    public boolean removeRelation(int id) {
        for (Relationships c : this.relationships) {
            if (c.getID() == id) {
                (this.relationships).remove(id);
                return true;
            }
        }
        return false;
    }

    // needed for random test ... remove later!
    public String getSomeData() {
        return (this.relationships).get(0).getFromDesc();
    }
}













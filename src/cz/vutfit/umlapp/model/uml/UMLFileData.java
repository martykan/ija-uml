package cz.vutfit.umlapp.model.uml;

import java.util.ArrayList;
import cz.vutfit.umlapp.model.uml.exceptions.DuplicateClassNameException;

public class UMLFileData {
    public final ArrayList<ClassDiagram> classDiagram = new ArrayList<>();
    public final ArrayList<SequenceDiagram> sequenceDiagrams = new ArrayList<>();
    public final ArrayList<Relationships> relationships = new ArrayList<>();

    // getters -> vraci listy
    public ArrayList<ClassDiagram> getClasses() {
        return this.classDiagram;
    }

    public ArrayList<SequenceDiagram> getSequenceDiagrams() {
        return this.sequenceDiagrams;
    }

    public ArrayList<Relationships> getRelationships() {
        return this.relationships;
    }

    // prace s listem trid v class diagramu
    public ClassDiagram getClassByIndex(int index) {
        return (this.classDiagram).get(index);
    }

    public ClassDiagram getClassByID(int id) {
        for (ClassDiagram c : this.classDiagram) {
            if (c.getID() == id) {
                return c;
            }
        }
        return null;
    }

    public ClassDiagram getClassByName(String name) {
        for (ClassDiagram c : this.classDiagram) {
            if (c.getName().equals(name)) {
                return c;
            }
        }
        return null;
    }

    public int addClass(String name) throws DuplicateClassNameException {
        System.out.print("addClass() - id: ");
        int id = 0;
        boolean found = false;
        if (id != (this.classDiagram).size()) { // there are classes in list
            //System.out.println("addClass: id != 0 (".concat(String.valueOf((this.classDiagram).size())).concat(")"));
            int i = 0;
            for (ClassDiagram c : this.classDiagram) {  // check if classes ID's are sorted, linear, start with 0, end with size-1
                if (i != c.getID()) { // condition up is not true
                    //System.out.println("addClass: found blbost: ".concat(String.valueOf(i).concat(" != ").concat(String.valueOf(c.getID()))));
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

    // support function, checks for duplicate class names in entire classes array
    // [[ if given string (name) as first arguments, compares with that name - faster ]]
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
                counter = 0;
            }
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

    // prace s listem sekvencich diagramu !! TODO !!
    public SequenceDiagram getSequenceByIndex(int index) {
        return (this.sequenceDiagrams).get(index);
    }

    public SequenceDiagram getSequenceByID(int id) {
        for (SequenceDiagram c : this.sequenceDiagrams) {
            if (c.getID() == id) {
                return c;
            }
        }
        return null;
    }

    public void addSequence(String name) {
        int id = 0;
        if (id != (this.sequenceDiagrams).size()) {
            int i = 0;
            for (SequenceDiagram c : this.sequenceDiagrams) {
                if (i != c.getID()) {
                    id = i;
                    break;
                }
                i++;
            }
            if (id == 0) id = (this.sequenceDiagrams).size();
        }

        SequenceDiagram x = new SequenceDiagram(id, name);
        (this.sequenceDiagrams).add(id, x);
    }

    public boolean removeSequence(int id) {
        for (SequenceDiagram c : this.sequenceDiagrams) {
            if (c.getID() == id) {
                (this.sequenceDiagrams).remove(id);
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

    public void addRelation(int fromId, int toId, ERelationType type) {
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

        Relationships x = new Relationships(id, fromId, toId, type);
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
}













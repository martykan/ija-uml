package cz.vutfit.umlapp.model.uml;

import java.util.ArrayList;

public class ClassDiagram {
    public int id;
    public String name;
    public ArrayList<Attributes> attribs;
    public ArrayList<Methods> methods;
    public ArrayList<ClassDiagramSequences> seqdigs;

    // getters
    public int getID() { return this.id; }

    public String getName() { return this.name; }

    public ArrayList<Attributes> getAttribs() { return this.attribs; }

    public ArrayList<Methods> getMethods() { return this.methods; }

    public ArrayList<ClassDiagramSequences> getSeqdigs() { return this.seqdigs; }

    // setters
    public void setName(String name) { this.name = name; }

    // working with attributes list
    public Attributes getAttribute(String name) {
        for (Attributes x : this.attribs) {
            if (x.getName().equals(name))
                return x;
        }
        return null;
    }

    public void addAttribute(String name, attrib_visibility visibility) {
        Attributes x = new Attributes();
        x.setAttribute(name, visibility);

        for (Attributes a : this.attribs) { // do not allow duplicates - same "name"'s
            if (a.getName().equals(x.getName())) {
                throw new Exception("DuplicateAttributeName"); // TODO -> not sure what to do
                return;
            }
        }

        (this.attribs).add(x);
    }

    public boolean removeAttribute(String name) {
        for (int i = 0; i < (this.attribs).size(); i++) {
            if ((this.attribs).get(i).getName().equals(name)) {
                (this.attribs).remove(i);
                return true;
            }
        }
        return false;
    }

    // working with method list
    public Methods getMethod(String name) {
        for (Methods x : this.methods) {
            if (x.getName().equals(name))
                return x;
        }
        return null;
    }

    public void addMethod(String name, attrib_visibility visibility) {
        Methods x = new Methods();
        x.setMethod(name, visibility);

        for (Methods a : this.methods) { // do not allow duplicates - same "name"'s
            if (a.getName().equals(x.getName())) {
                throw new Exception("DuplicateMethodName"); // TODO -> not sure what to do
                return;
            }
        }

        (this.methods).add(x);
    }

    public boolean removeMethod(String name) {
        for (int i = 0; i < (this.methods).size(); i++) {
            if ((this.methods).get(i).getName().equals(name)) {
                (this.methods).remove(i);
                return true;
            }
        }
        return false;
    }

    // working with list of linked sequence diagrams
    public ClassDiagramSequences getLinkedSequence(int id) {
        for (ClassDiagramSequences x : this.seqdigs) {
            if (x.getID() == id)
                return x;
        }
        return null;
    }

    public void addLinkedSequence(int id) {
        ClassDiagramSequences x = new ClassDiagramSequences();
        x.setID(id);

        for (ClassDiagramSequences a : this.seqdigs) { // do not allow duplicates - same "id"'s
            if (a.getID() == id) {
                throw new Exception("DuplicateLinkedSequenceDiagramID"); // TODO -> not sure what to do
                return;
            }
        }

        (this.seqdigs).add(x);
    }

    public boolean removeLinkedSequence(int id) {
        for (int i = 0; i < (this.seqdigs).size(); i++) {
            if ((this.seqdigs).get(i).getID() == id) {
                (this.seqdigs).remove(i);
                return true;
            }
        }
        return false;
    }

    // other
    public void classInit(int id, String name) {
        this.id = id;
        this.name = name;
        this.attribs = new ArrayList<Attributes>();
        this.methods = new ArrayList<Methods>();
        this.seqdigs = new ArrayList<ClassDiagramSequences>();
    }

}
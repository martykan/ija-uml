package cz.vutfit.umlapp.model.uml;

public class SequenceDiagram {
    public int id;
    public String name;

    // getters
    public int getID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    // constructor
    public SequenceDiagram() {
    }

    public SequenceDiagram(int id, String name) {
        this.id = id;
        this.name = name;
    }
}

package cz.vutfit.umlapp.model.uml;

import java.util.ArrayList;

public class SequenceDiagram {
    public int id;
    public String name;

    // getters
    public int getID() { return this.id; }

    public String getName() { return this.name; }

    // other
    public void sequenceInit(int id, String name) {
        this.id = id;
        this.name = name;
    }
}

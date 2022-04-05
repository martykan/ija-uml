package cz.vutfit.umlapp.model.uml;
import java.util.ArrayList;

enum relation_type {
    ASSOCIATION,
    AGGREGATION,
    COMPOSITION,
    GENERALIZATION
}

public class Relationships {
    public int id;
    public int from_id;
    public int to_id;
    public relation_type relationship;
    public String from_desc;
    public String to_desc;

    // getters
    public int getID () { return this.id; }

    public String getFromDesc() { return this.from_desc; }


    // other
    public void relationshipInit(int id, int from_id, int to_id, relation_type type) {
        this.id = id;
        this.from_id = from_id;
        this.to_id = to_id;
        this.relationship = type;
        this.from_desc = null;
        this.to_desc = null;
    }

    public void setFromDesc(String text) { this.from_desc = text; }

    public void setToDesc(String text) { this.to_desc = text; }
}

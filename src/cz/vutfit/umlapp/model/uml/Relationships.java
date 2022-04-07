package cz.vutfit.umlapp.model.uml;

enum ERelationType {
    ASSOCIATION,
    AGGREGATION,
    COMPOSITION,
    GENERALIZATION
}

public class Relationships {
    public Integer id;
    public Integer fromId;
    public Integer toId;
    public ERelationType relationship;
    public String fromDesc;
    public String toDesc;

    // constructor
    public Relationships() {
    }

    public Relationships(Integer id, Integer fromId, Integer toId, ERelationType type) {
        this.id = id;
        this.fromId = fromId;
        this.toId = toId;
        this.relationship = type;
        this.fromDesc = null;
        this.toDesc = null;
    }

    // getters
    public int getID() {
        return this.id;
    }

    public String getFromDesc() {
        return this.fromDesc;
    }

    public void setFromDesc(String text) {
        this.fromDesc = text;
    }

    public void setToDesc(String text) {
        this.toDesc = text;
    }
}

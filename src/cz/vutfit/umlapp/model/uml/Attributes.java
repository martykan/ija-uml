package cz.vutfit.umlapp.model.uml;

enum EAttribVisibility {
    PUBLIC,     // +
    PRIVATE,    // -
    PROTECTED,  // #
    PACKAGE     // ~
}

public class Attributes {
    public String name;
    public EAttribVisibility visibility;

    // getters
    public String getName() {
        return this.name;
    }

    public EAttribVisibility getVisibility() {
        return this.visibility;
    }

    // setters
    public void setName(String name) {
        this.name = name;
    }

    public void setVisibility(EAttribVisibility value) {
        this.visibility = value;
    }

    // set all in one
    public void setAttribute(String name, EAttribVisibility value) {
        this.visibility = value;
        this.name = name;
    }
}

package cz.vutfit.umlapp.model.uml;

public class Methods {
    public String name;
    public attrib_visibility visibility;

    // getters
    public String getName() { return this.name; }

    public attrib_visibility getVisibility() { return this.visibility; }

    // setters
    public void setName(String name) { this.name = name; }

    public void setVisibility(attrib_visibility value) { this.visibility = value; }

    // set all in one
    public void setMethod(String name, attrib_visibility value) {
        this.visibility = value;
        this.name = name;
    }
}

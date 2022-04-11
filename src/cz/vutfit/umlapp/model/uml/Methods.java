package cz.vutfit.umlapp.model.uml;

public class Methods {
    public String name;
    public EAttribVisibility visibility;

    // getters
    public String getName() {
        return this.name;
    }

    public EAttribVisibility getVisibility() {
        return this.visibility;
    }

    public String getNameWithPrefix() {
        return this.getVisibility().getPrefix() + this.getName() + "()";
    }

    // setters
    public void setName(String name) {
        this.name = name;
    }

    public void setVisibility(EAttribVisibility value) {
        this.visibility = value;
    }

    // set all in one
    public void setMethod(String name, EAttribVisibility value) {
        this.visibility = value;
        this.name = name;
    }
}

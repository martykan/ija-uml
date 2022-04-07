package cz.vutfit.umlapp.model.uml;

public enum EAttribVisibility {
    PUBLIC,     // +
    PRIVATE,    // -
    PROTECTED,  // #
    PACKAGE;    // ~

    public String getPrefix() {
        switch (this) {
            case PUBLIC:
                return "+";
            case PRIVATE:
                return "-";
            case PROTECTED:
                return "#";
            case PACKAGE:
                return "~";
        }
        return "";
    }
}

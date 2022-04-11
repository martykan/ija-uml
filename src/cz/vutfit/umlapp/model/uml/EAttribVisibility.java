/*
 * File: EAttribVisibility.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

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

    public String getVisiblityString() {
        switch (this) {
            case PUBLIC:
                return "Public";
            case PRIVATE:
                return "Private";
            case PROTECTED:
                return "Protected";
            case PACKAGE:
                return "Package";
        }
        return "";
    }
}

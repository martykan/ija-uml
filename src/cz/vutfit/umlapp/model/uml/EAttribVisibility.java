/*
 * File: EAttribVisibility.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.uml;

/**
 * Enumeration for attribute/method visibility in class
 */
public enum EAttribVisibility {
    PUBLIC,     /** + -> public **/
    PRIVATE,    /** - -> private **/
    PROTECTED,  /** # -> protected **/
    PACKAGE;    /** ~ -> package **/

    /**
     * Converts enum value to string - prefix.
     * @return visibility prefix
     */
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

    /**
     * Converts enum value to string.
     * @return visibility in string
     */
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

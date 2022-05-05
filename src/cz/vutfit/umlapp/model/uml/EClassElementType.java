/*
 * File: EClassElementType.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.uml;

public enum EClassElementType {
    INT,
    VOID,
    STRING,
    BOOLEAN,
    FLOAT,
    LONG,
    CHAR,
    DOUBLE;

    public String typeToString() {
        switch (this) {
            case INT:
                return "int";
            case VOID:
                return "void";
            case STRING:
                return "String";
            case BOOLEAN:
                return "boolean";
            case FLOAT:
                return "float";
            case LONG:
                return "long";
            case DOUBLE:
                return "double";
            case CHAR:
                return "char";
        }
        return null;
    }
}

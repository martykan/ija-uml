/*
 * File: ErrorCheckSequenceItem.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model;

public class ErrorCheckSequenceItem {
    public ESequenceCheckError errorType;
    public String mainID;
    public EElementType elementType;
    public String elementID;
    public EElementType subelementType;
    public String subelementID;

    public ErrorCheckSequenceItem(ESequenceCheckError errorType, String mainID, EElementType elementType, String elementID, EElementType subelementType, String subelementID) {
        this.errorType = errorType;
        this.mainID = mainID;
        this.elementType = elementType;
        this.elementID = elementID;
        this.subelementType = subelementType;
        this.subelementID = subelementID;
    }

    public ErrorCheckSequenceItem(ESequenceCheckError errorType, String mainID, EElementType elementType, String elementID) {
        this(errorType, mainID, elementType, elementID, null, null);
    }

    public ErrorCheckSequenceItem(ESequenceCheckError errorType, String mainID, EElementType elementType, Integer elementID) {
        this(errorType, mainID, elementType, String.valueOf(elementID), null, null);
    }

    public ESequenceCheckError getErrorType() {
        return errorType;
    }

    public String getMainID() {
        return mainID;
    }

    public EElementType getElementType() {
        return elementType;
    }

    public String getElementID() {
        return elementID;
    }

    public EElementType getSubelementType() {
        return subelementType;
    }

    public String getSubelementID() {
        return subelementID;
    }

    public String toString() {
        String output = mainID;
        if (elementType != null)
            output += "/" + elementType.name();
        if (elementID != null)
            output += "[" + elementID + "]";
        if (subelementType != null)
            output += "/" + subelementType.name();
        if (subelementID != null)
            output += "[" + subelementID + "]";
        return output;
    }
}

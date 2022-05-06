/*
 * File: ErrorCheckItem.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

/*
 * File: ErrorCheckSequenceItem.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.errors;

import cz.vutfit.umlapp.model.EElementType;

public class ErrorCheckItem {
    public final ECheckError errorType;
    public final String mainID;
    public final EElementType elementType;
    public final String elementID;
    public final EElementType subelementType;
    public final String subelementID;

    public ErrorCheckItem(ECheckError errorType, String mainID, EElementType elementType, String elementID, EElementType subelementType, String subelementID) {
        this.errorType = errorType;
        this.mainID = mainID;
        this.elementType = elementType;
        this.elementID = elementID;
        this.subelementType = subelementType;
        this.subelementID = subelementID;
    }

    public ErrorCheckItem(ECheckError errorType, String mainID, EElementType elementType, String elementID) {
        this(errorType, mainID, elementType, elementID, null, null);
    }

    public ErrorCheckItem(ECheckError errorType, String mainID, EElementType elementType, Integer elementID) {
        this(errorType, mainID, elementType, String.valueOf(elementID), null, null);
    }

    public ECheckError getErrorType() {
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

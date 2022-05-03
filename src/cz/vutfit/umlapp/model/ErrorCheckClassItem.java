/*
 * File: ErrorCheckClassItem.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model;

public class ErrorCheckClassItem {
    /**
     *  Identification of type of inconsistency, error
     *  EClassCheckError    type of the error
     *  String              identification of element, where and what is wrong (so it can be marked in UI)
     *                      format:     classname:element_type:element_id:subelement_type:subelement_id:(...)
     *                      example:    class42:attribute:myattribute
     *
     */
    public EClassCheckError errorType;
    public String errorContent;

    public ErrorCheckClassItem(EClassCheckError errorType, String errorContent) {
        this.errorType = errorType;
        this.errorContent = errorContent;
    }

    public EClassCheckError getErrorType() {
        return this.errorType;
    }

    public String getErrorContent() {
        return this.errorContent;
    }

    public int getErrorContentMaxIndex() {
        return this.errorContent.split(":").length;
    }

    public Object getErrorContentData(int index) {
        if (index != 2)
            return this.errorContent.split(":")[index];
        else {
            if (this.errorContent.split(":")[2].split("\\[")[0].equals("String"))
                return this.errorContent.split(":")[2].split("\\[")[1].split("\\]")[0];
            else if (this.errorContent.split(":")[2].split("\\[")[0].equals("int"))
                return Integer.valueOf(this.errorContent.split(":")[2].split("\\[")[1].split("\\]")[0]);
        }
        return null;
    }
}

/*
 * File: ERelationType.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.uml;

/**
 * Enumeration for relation type between two classes
 */
public enum ERelationType {
    ASSOCIATION,
    AGGREGATION,
    COMPOSITION,
    GENERALIZATION;

    public String relationToString() {
        switch (this) {
            case ASSOCIATION:
                return "Association";
            case AGGREGATION:
                return "Aggregation";
            case COMPOSITION:
                return "Composition";
            case GENERALIZATION:
                return "Generalization";
        }
        return null;
    }

    /**
     * Converts string to enum ERelationType (compatible with relationToString() method in ERelationType)
     *
     * @param string valid string to be converted
     * @return enum value of string or null if invalid
     * @see ERelationType
     */
    public static ERelationType fromString(String string) {
        switch (string) {
            case "Association":
                return ERelationType.ASSOCIATION;
            case "Aggregation":
                return ERelationType.AGGREGATION;
            case "Composition":
                return ERelationType.COMPOSITION;
            case "Generalization":
                return ERelationType.GENERALIZATION;
        }
        return null;
    }
}
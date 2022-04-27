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
}
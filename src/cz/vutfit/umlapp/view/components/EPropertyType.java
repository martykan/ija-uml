/*
 * File: EPropertyType.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.view.components;

/**
 *  Type of properties in propetiesView (used for binding actions)
 */
public enum EPropertyType {
    CLASS,

    ATTRIBUTE,
    METHOD,
    RELATIONSHIP,

    SEQ_MESSAGE,
    SEQ_OBJECT,

    EMPTY
}

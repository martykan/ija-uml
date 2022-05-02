/*
 * File: EDataType.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.view.main;

/**
 * Enumeration for determining, which data we will be working with in TreeView.
 *
 * @see TreeViewItemModel
 */
public enum EDataType {
    DIAGRAM,

    CLASS,
    METHOD,
    ATTRIBUTE,
    RELATIONSHIP,

    SEQ_OBJECTS,
    SEQ_MESSAGES
}

/*
 * File: ESequenceCheckError.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model;

public enum ESequenceCheckError {
    MSG_RET_DIRECTION,
    MSG_NONRET_DIRECTION,
    MSG_NEW_OBJECT_INVALID,
    MSG_DESTROY_OBJECT_INVALID,

    OBJ_CLASS_NONEXISTENT,
    OBJ_METHOD_NONEXISTENT,

    CHECK_OK
}

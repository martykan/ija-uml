/*
 * File: EMessageType.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.uml;

public enum EMessageType {
    SYNC,
    ASYNC,
    RETURN,
    NEW_OBJECT,
    RELEASE_OBJECT;

    public String typeToString() {
        switch (this) {
            case SYNC:
                return "Synchronized";
            case ASYNC:
                return "Asynchronous";
            case RETURN:
                return "Return message";
            case NEW_OBJECT:
                return "Object creation";
            case RELEASE_OBJECT:
                return "Object release";
        }
        return null;
    }
}

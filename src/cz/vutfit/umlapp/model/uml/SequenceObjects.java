/*
 * File: SequenceObjects.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.uml;

import javafx.util.Pair;

public class SequenceObjects {
    public String className;
    public String objectName;
    public boolean active;

    public SequenceObjects (String className, String objectName) {
        this.className = className;
        this.objectName = objectName;
        this.active = false;
    }

    public SequenceObjects (Pair<String, String> classObjectName) {
        this.className = classObjectName.getKey();
        this.objectName = classObjectName.getValue();
        this.active = false;
    }

    public String getClassName() { return this.className; }

    public String getObjectName() { return this.objectName; }

    public boolean getActiveStatus() { return this.active; }

    public void setActiveStatus(boolean status) { this.active = status; }

    public void setClassName(String name) { this.className = name; }

    public void setObjectName(String name) { this.objectName = name; }

    public String getActiveStatusString() {
        if (this.active)
            return "Active";
        else
            return "Inactive";
    }

    public void setActiveStatusString(String statusString) {
        if (statusString.equals("Active"))
            this.active = true;
        else if (statusString.equals("Inactive"))
            this.active = false;
        else
            System.out.println("Error: Unknown String in setActiveStatusString");
    }

    public String getObjectClassName() {
        return this.objectName + ":" + this.className;
    }

    public boolean equals(Pair<String, String> classObjectName) {
        return this.className.equals(classObjectName.getKey()) && this.objectName.equals(classObjectName.getValue());
    }
}

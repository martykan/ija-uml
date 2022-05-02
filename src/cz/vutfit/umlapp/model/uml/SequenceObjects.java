/*
 * File: SequenceObjects.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.uml;

public class SequenceObjects {
    public String name;
    public boolean active;

    public SequenceObjects (String name) {
        this.name = name;
        this.active = false;
    }

    public String getName() { return this.name; }

    public boolean getActiveStatus() { return this.active; }

    public void setActiveStatus(boolean status) { this.active = status; }

    public void setName(String name) { this.name = name; }

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
}

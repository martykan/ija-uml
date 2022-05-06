/*
 * File: ErrorCheckClass.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

/*
 * File: ErrorCheckClass.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model.errors;

import cz.vutfit.umlapp.model.DataModel;
import cz.vutfit.umlapp.model.EElementType;
import cz.vutfit.umlapp.model.uml.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ErrorCheckClass {
    public final ArrayList<ErrorCheckItem> classErrors;
    public final ArrayList<ErrorCheckItem> sequenceErrors;
    public final DataModel dataModel;

    public ErrorCheckClass(DataModel dataModel) {
        this.classErrors = new ArrayList<>();
        this.sequenceErrors = new ArrayList<>();
        this.dataModel = dataModel;
    }

    public ArrayList<ErrorCheckItem> getClassErrors() {
        return this.classErrors;
    }

    public ArrayList<ErrorCheckItem> getSequenceErrors() {
        return this.sequenceErrors;
    }

    public ErrorCheckItem getLastClassError() {
        if (this.classErrors.size() > 0)
            return this.classErrors.get(this.classErrors.size() - 1);
        else
            return null;
    }

    public ErrorCheckItem getLastSequenceError() {
        if (this.sequenceErrors.size() > 0)
            return this.sequenceErrors.get(this.sequenceErrors.size() - 1);
        else
            return null;
    }

    public boolean isClassDiagramCorrect() {
        return this.classErrors.size() == 0;
    }

    public boolean isSequenceDiagramCorrect() {
        return this.sequenceErrors.size() == 0;
    }

    public void printClassErrors() {
        if (!this.isClassDiagramCorrect()) {
            for (ErrorCheckItem error : this.getClassErrors()) {
                System.out.println("CLASS_ERROR[" + error.getErrorType() + "; " + error + "]");
            }
        } else {
            System.out.println("NO CLASS_ERROR FOUND");
        }
    }

    public void printSequenceErrors() {
        if (!this.isSequenceDiagramCorrect()) {
            for (ErrorCheckItem error : this.getSequenceErrors()) {
                System.out.println("SEQUENCE_ERROR[" + error.getErrorType() + "; " + error + "]");
            }
        } else {
            System.out.println("NO SEQUENCE_ERROR FOUND");
        }
    }

    public void addClassError(ErrorCheckItem error) {
        this.classErrors.add(error);
    }

    public void addSequenceError(ErrorCheckItem error) {
        this.sequenceErrors.add(error);
    }

    public void solveClassErrors() {
        this.classErrors.clear();
    }

    public void solveSequenceErrors() {
        this.sequenceErrors.clear();
    }

    public boolean isSeqObjectCorrect(String diagram, String objectID) {
        return this.sequenceErrors.stream().anyMatch(it ->
                it.getMainID().equals(diagram) && it.getElementType() == EElementType.SEQ_OBJECT &&
                        it.getElementID().equals(objectID)
        );
    }

    public boolean isSeqMessageCorrect(String diagram, int messageID) {
        return this.sequenceErrors.stream().anyMatch(it ->
                it.getMainID().equals(diagram) && it.getElementType() == EElementType.SEQ_MESSAGE &&
                        it.getElementID().equals(String.valueOf(messageID))
        );
    }

    public void checkClassDiagram() {
        this.printClassErrors();
    }

    public void checkSequenceDiagram(String selectedDiagram) {
        // checks all inconsitencies of sequence diagram
        // if there is inconsitency, sets check result to false!
        SequenceDiagram currentDiagram = this.dataModel.getData().getSequenceByName(selectedDiagram);
        ArrayList<SequenceMessages> allMessages = currentDiagram.getMessages();
        ArrayList<SequenceObjects> allObjects = currentDiagram.getObjects();
        ErrorCheckClass errorClass = this.dataModel.getErrorClass();
        ErrorCheckItem error;
        ECheckError errorType;

        // clear all errors
        errorClass.solveSequenceErrors();

        // Messages: type=return only opposite direction of other (last) messages
        // + must be response to something (atleast 1 message was received)
        int index = 0;
        for (SequenceMessages msg : allMessages) {
            if (msg.getType() == EMessageType.RETURN) {
                boolean returnOk = false;
                if (index != 0) {
                    for (SequenceMessages msg2 : allMessages) {
                        if (msg2.getReceiver().equals(msg.getSender()) && msg2.getSender().equals(msg.getReceiver()) && msg2.getType() != EMessageType.RETURN) {
                            returnOk = true;
                            break;
                        }
                    }
                    if (!returnOk) {
                        errorType = ECheckError.MSG_RET_DIRECTION;
                        error = new ErrorCheckItem(errorType, currentDiagram.getName(), EElementType.SEQ_MESSAGE, msg.getID());
                        errorClass.addSequenceError(error);
                    }
                }
            }
            index++;
        }

        // Class instance not existing anymore (but exists in sequence diagram object) because:
        //  1) user removed class from class diagram
        //  2) file load failed
        for (SequenceObjects obj : allObjects) {
            String className = obj.getClassName();
            if (this.dataModel.getData().getClassByName(className) == null) {
                errorType = ECheckError.OBJ_CLASS_NONEXISTENT;
                error = new ErrorCheckItem(errorType, currentDiagram.getName(), EElementType.SEQ_OBJECT, obj.getObjectClassName());
                errorClass.addSequenceError(error);
            }
        }

        // Messages: method must exist on receiver
        for (SequenceMessages message : allMessages) {
            if (message.type == EMessageType.RETURN || message.type == EMessageType.NEW_OBJECT || message.type == EMessageType.RELEASE_OBJECT)
                continue;
            String className = message.getReceiver().getKey();
            String methodName = message.content.split("\\(")[0];
            ClassDiagram classDiagram = this.dataModel.getData().getClassByName(className);
            if (classDiagram != null) {
                if (classDiagram.getMethodByNameOnly(methodName) != null) continue;

                // Search inherited methods
                boolean found = false;
                for (Relationships r : this.dataModel.getData().getRelationships()) {
                    if (r.getToClassID() == classDiagram.getID() && r.getType() == ERelationType.GENERALIZATION) {
                        for (Methods m : this.dataModel.getData().getClassByID(r.getFromClassID()).getMethods()) {
                            if (m.getName().startsWith(methodName + "(")) {
                                found = true;
                                break;
                            }
                        }
                    }
                }

                if (!found) {
                    errorType = ECheckError.OBJ_METHOD_NONEXISTENT;
                    error = new ErrorCheckItem(errorType, currentDiagram.getName(), EElementType.SEQ_OBJECT, message.getSenderString());
                    errorClass.addSequenceError(error);
                    error = new ErrorCheckItem(errorType, currentDiagram.getName(), EElementType.SEQ_MESSAGE, message.getID());
                    errorClass.addSequenceError(error);
                }
            }
        }

        // Messages: message creating object must be first, destroying last
        Set<String> objectsUsed = new HashSet<>();
        Set<String> objectsDestroyed = new HashSet<>();
        for (SequenceMessages message : allMessages) {
            errorType = null;
            if (objectsDestroyed.contains(message.getReceiverString())) {
                errorType = ECheckError.MSG_DESTROY_OBJECT_INVALID;
            } else if (message.getType() == EMessageType.NEW_OBJECT && objectsUsed.contains(message.getReceiverString())) {
                errorType = ECheckError.MSG_NEW_OBJECT_INVALID;
            } else if (message.getType() == EMessageType.RELEASE_OBJECT) {
                objectsDestroyed.add(message.getReceiverString());
            } else {
                objectsUsed.add(message.getReceiverString());
                objectsUsed.add(message.getSenderString());
            }
            if (errorType != null) {
                error = new ErrorCheckItem(errorType, currentDiagram.getName(), EElementType.SEQ_OBJECT, message.getReceiverString());
                errorClass.addSequenceError(error);
                error = new ErrorCheckItem(errorType, currentDiagram.getName(), EElementType.SEQ_MESSAGE, message.getID());
                errorClass.addSequenceError(error);
            }
        }

        this.printSequenceErrors();
    }
}

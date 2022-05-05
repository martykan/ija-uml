/*
 * File: ErrorCheckClass.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model;

import java.util.ArrayList;

public class ErrorCheckClass {
    public ArrayList<ErrorCheckClassItem> classErrors;
    public ArrayList<ErrorCheckSequenceItem> sequenceErrors;

    public ErrorCheckClass() {
        this.classErrors = new ArrayList<>();
        this.sequenceErrors = new ArrayList<>();
    }

    public ArrayList<ErrorCheckClassItem> getClassErrors() {
        return this.classErrors;
    }

    public ArrayList<ErrorCheckSequenceItem> getSequenceErrors() {
        return this.sequenceErrors;
    }

    public ErrorCheckClassItem getLastClassError() {
        if (this.classErrors.size() > 0)
            return this.classErrors.get(this.classErrors.size() - 1);
        else
            return null;
    }

    public ErrorCheckSequenceItem getLastSequenceError() {
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
            for (ErrorCheckClassItem error : this.getClassErrors()) {
                System.out.println("CLASS_ERROR[" + error.getErrorType() + "; " + error.toString() + "]");
            }
        } else {
            System.out.println("NO CLASS_ERROR FOUND");
        }
    }

    public void printSequenceErrors() {
        if (!this.isSequenceDiagramCorrect()) {
            for (ErrorCheckSequenceItem error : this.getSequenceErrors()) {
                System.out.println("SEQUENCE_ERROR[" + error.getErrorType() + "; " + error.toString() + "]");
            }
        } else {
            System.out.println("NO SEQUENCE_ERROR FOUND");
        }
    }

    public void addClassError(ErrorCheckClassItem error) {
        this.classErrors.add(error);
    }

    public void addSequenceError(ErrorCheckSequenceItem error) {
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
}

/*
 * File: DataModel.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import cz.vutfit.umlapp.model.commands.ICommand;
import cz.vutfit.umlapp.model.uml.*;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.util.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for data model of app
 */
public class DataModel {
    private final List<ICommand> commandHistory = new ArrayList<>();
    private File file;
    private UMLFileData data;
    private String activeDiagram;

    private boolean savedFile = false;
    private ErrorCheckClass error;
    public String currentFileDataVersion = "20220505-001";

    /**
     * Execute the given command and save it to history
     *
     * @param command Command to be executed
     */
    public void executeCommand(ICommand command) throws Exception {
        if (data == null) return;
        command.execute(data);
        commandHistory.add(command);
        this.fileUnsaved();
    }

    /**
     * Undo the last command
     */
    public void undo() throws Exception {
        if (commandHistory.isEmpty()) return;
        ICommand command = commandHistory.remove(commandHistory.size() - 1);
        command.undo(data);
        this.fileUnsaved();
    }

    public boolean isCommandHistoryEmpty() { return commandHistory.isEmpty(); }

    /**
     * Create a new instance from existing file
     *
     * @param file File to be opened
     * @throws FileNotFoundException File was not found
     */
    public void openFile(File file) throws FileNotFoundException {
        this.file = file;
        JsonReader reader = new JsonReader(new FileReader(file));
        this.data = new Gson().fromJson(reader, UMLFileData.class);

        if (this.checkFile() == false) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "This file is not entirely correct input for this program.\nProceed with caution.", ButtonType.OK);
            alert.setTitle("File Open");
            alert.setHeaderText("File check failed");
            alert.showAndWait();
        }

        this.commandHistory.clear();
        this.fileSaved();
        this.error = new ErrorCheckClass();
    }

    /**
     * Create a new instance from new file
     *
     * @param file File to be created
     */
    public void newFile(File file) {
        this.file = file;
        this.data = new UMLFileData();
        this.commandHistory.clear();
        this.fileUnsaved();
        this.error = new ErrorCheckClass();
        this.getData().dataVersion = this.currentFileDataVersion;
    }

    /**
     * Save the file
     *
     * @throws IOException File couldn't be written to
     */
    public void saveFile() throws IOException {
        String serialized = new Gson().toJson(data);

        var writer = new BufferedWriter(new FileWriter(file));
        writer.write(serialized);
        writer.close();
        this.fileSaved();
    }

    public void fileSaved() { this.savedFile = true; }
    public void fileUnsaved() { this.savedFile = false; }
    public boolean getFileSaveStatus() { return this.savedFile; }

    public UMLFileData getData() {
        return data;
    }

    public String getFileName() {
        return this.file.getName();
    }

    public String getActiveDiagram() {
        return activeDiagram;
    }

    public void setActiveDiagram(String activeDiagram) {
        this.activeDiagram = activeDiagram;
    }

    public boolean checkFile() {
        UMLFileData data = this.getData();
        ArrayList<ClassDiagram> c;
        ArrayList<SequenceDiagram> s;
        ArrayList<Relationships> r;
        ArrayList<Attributes> attrib;
        ArrayList<Methods> m;
        ArrayList<SequenceMessages> seq_m;
        ArrayList<SequenceObjects> seq_o;
        try {
            c = data.getClasses();
            s = data.getSequenceDiagrams();
            r = data.getRelationships();
            for (ClassDiagram x : c) {
                x.getName().charAt(0);
                int id1 = x.getID();
                attrib = x.getAttribs();
                m = x.getMethods();
                for (Attributes y : attrib) {
                    y.getName().charAt(0);
                    y.getVisibility().getVisiblityString();
                    y.getType().typeToString();
                    y.getNameWithPrefix().charAt(0);
                }
                for (Methods y : m) {
                    y.getName().charAt(0);
                    y.getVisibility().getVisiblityString();
                    y.getType().typeToString();
                    y.getNameWithPrefix().charAt(0);
                }
            }
            for (Relationships x : r) {
                x.getName().charAt(0);
                int id2 = x.getID();
                x.getType().relationToString();
                int fid = x.getFromClassID();
                int tid = x.getToClassID();
            }
            for (SequenceDiagram x : s) {
                int id3 = x.getID();
                x.getName().charAt(0);
                seq_m = x.getMessages();
                seq_o = x.getObjects();
                for (SequenceObjects y : seq_o) {
                    y.getClassName().charAt(0);
                    y.getObjectName().charAt(0);
                    boolean a = y.getActiveStatus();
                    y.getActiveStatusString().charAt(0);
                    y.getObjectClassName().charAt(0);
                }
                for (SequenceMessages y : seq_m) {
                    y.getType().typeToString();
                    y.getSender().getKey().charAt(0);
                    y.getSender().getValue().charAt(0);
                    y.getReceiver().getKey().charAt(0);
                    y.getReceiver().getValue().charAt(0);
                    y.getParticipants().getKey().getKey().charAt(0);
                    y.getParticipants().getKey().getValue().charAt(0);
                    y.getParticipants().getValue().getKey().charAt(0);
                    y.getParticipants().getValue().getValue().charAt(0);
                    int id4 = y.getID();
                    y.getContent().charAt(0);
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public ErrorCheckClass getErrorClass() { return this.error; }
}

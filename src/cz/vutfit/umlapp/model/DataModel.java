/*
 * File: DataModel.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import cz.vutfit.umlapp.model.commands.ICommand;
import cz.vutfit.umlapp.model.uml.UMLFileData;
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

    public ErrorCheckClass getErrorClass() { return this.error; }
}

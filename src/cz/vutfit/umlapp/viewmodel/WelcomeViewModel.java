package cz.vutfit.umlapp.viewmodel;

import cz.vutfit.umlapp.model.DataModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.File;
import java.io.FileNotFoundException;

public class WelcomeViewModel {
    private final DataModel dataModel;

    public StringProperty buttonText;

    public WelcomeViewModel(DataModel dataModel) {
        this.dataModel = dataModel;

        this.buttonText = new SimpleStringProperty("No files open");
    }

    public void handleOpenFile(File file) {
        try {
            dataModel.openFile(file);
            buttonText.set("Opened file " + file.getName());
        } catch (FileNotFoundException exception) {
            buttonText.set("Couldn't open file");
        }
    }

    public void handleNewFile(File file) {
        dataModel.newFile(file);
        buttonText.set("New file " + file.getName());
    }
}

package cz.vutfit.umlapp.view.main;

import cz.vutfit.umlapp.model.DataModel;
import cz.vutfit.umlapp.model.ModelFactory;
import cz.vutfit.umlapp.view.IController;
import cz.vutfit.umlapp.view.ViewHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;

public class MainController implements IController {
    @FXML
    Label label;
    @FXML
    TextField textField;
    private DataModel dataModel;
    private ViewHandler viewHandler;

    @Override
    public void init(ModelFactory modelFactory, ViewHandler viewHandler) {
        this.dataModel = modelFactory.getDataModel();
        this.viewHandler = viewHandler;

        viewHandler.setTitle("IJA UML App - " + this.dataModel.getFileName());
        this.label.setText("Opened file " + this.dataModel.getFileName());
        this.textField.setText(this.dataModel.getData().test);
        this.textField.textProperty().addListener((observable, oldValue, newValue) -> {
            this.dataModel.getData().test = newValue;
        });
    }

    public void handleSave(ActionEvent actionEvent) {
        try {
            this.dataModel.saveFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleClose(ActionEvent actionEvent) {
        try {
            this.viewHandler.openView("Welcome");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

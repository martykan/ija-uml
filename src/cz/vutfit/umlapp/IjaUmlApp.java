package cz.vutfit.umlapp;

import cz.vutfit.umlapp.model.ModelFactory;
import cz.vutfit.umlapp.view.ViewHandler;
import cz.vutfit.umlapp.viewmodel.ViewModelFactory;
import javafx.application.Application;
import javafx.stage.Stage;

public class IjaUmlApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        final var modelFactory = new ModelFactory();
        final var viewModelFactory = new ViewModelFactory(modelFactory);
        final var viewHandler = new ViewHandler(stage, viewModelFactory);
        viewHandler.start();
    }
}

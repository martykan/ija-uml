package cz.vutfit.umlapp.view;

import cz.vutfit.umlapp.view.welcome.WelcomeController;
import cz.vutfit.umlapp.viewmodel.ViewModelFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewHandler {
    private final Stage stage;
    private final ViewModelFactory viewModelFactory;

    public ViewHandler(Stage stage, ViewModelFactory viewModelFactory) {
        this.stage = stage;
        this.viewModelFactory = viewModelFactory;
    }

    public void start() throws Exception {
        openView("Welcome");
    }

    public void openView(String name) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/" + name.toLowerCase() + "/" + name + "View.fxml"));
        Parent root = loader.load();

        if ("Welcome".equals(name)) {
            WelcomeController view = loader.getController();
            view.init(this.viewModelFactory.getWelcomeViewModel());
            this.stage.setTitle("IJA UML App - Welcome");
        }

        Scene scene = new Scene(root);
        this.stage.setScene(scene);
        this.stage.show();
    }
}

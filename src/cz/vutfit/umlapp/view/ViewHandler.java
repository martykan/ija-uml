package cz.vutfit.umlapp.view;

import cz.vutfit.umlapp.model.ModelFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewHandler {
    private final Stage stage;
    private final ModelFactory modelFactory;

    public ViewHandler(Stage stage, ModelFactory modelFactory) {
        this.stage = stage;
        this.modelFactory = modelFactory;
    }

    public void start() throws Exception {
        openView("Welcome");
    }

    public void openView(String name) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/" + name.toLowerCase() + "/" + name + "View.fxml"));
        Parent root = loader.load();

        IController controller = loader.getController();
        controller.init(this.modelFactory, this);

        Scene scene = new Scene(root);
        this.stage.setScene(scene);
        this.stage.show();
    }

    public void setTitle(String title) {
        this.stage.setTitle(title);
    }
}

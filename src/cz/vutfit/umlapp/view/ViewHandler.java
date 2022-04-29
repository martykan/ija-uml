/*
 * File: ViewHandler.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.view;

import cz.vutfit.umlapp.model.ModelFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Class for handling view of this application
 */
public class ViewHandler {
    private final Stage stage;
    private final ModelFactory modelFactory;

    /**
     * Constructor
     * @param stage
     * @param modelFactory
     */
    public ViewHandler(Stage stage, ModelFactory modelFactory) {
        this.stage = stage;
        this.modelFactory = modelFactory;
    }

    /**
     * Beginning of application
     * @throws Exception related to openView
     * @see #openView(String)
     */
    public void start() throws Exception {
        openView("Welcome");
    }

    /**
     * Opens view - main window of app. Welcome screen with request of opening or creating new file.
     * @param name
     * @throws IOException
     */
    public void openView(String name) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        System.out.println("/" + name.toLowerCase() + "/" + name + "View.fxml");
        loader.setLocation(getClass().getResource("/" + name.toLowerCase() + "/" + name + "View.fxml"));
        Parent root = loader.load();

        IController controller = loader.getController();
        controller.init(this.modelFactory, this);

        Scene scene = new Scene(root);
        this.stage.setScene(scene);
        this.stage.show();
    }

    /**
     *
     * @param title
     */
    public void setTitle(String title) {
        this.stage.setTitle(title);
    }
}

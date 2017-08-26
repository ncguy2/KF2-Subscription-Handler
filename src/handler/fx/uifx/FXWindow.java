package handler.fx.uifx;

//
// Author: Nick Guy
// Created at: 25/08/2017
//

import handler.ui.Strings;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class FXWindow extends Application {

    protected WindowController controller;
    protected Stage stage;

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle(Strings.WINDOW_TITLE);
        primaryStage.setResizable(true);
        FXMLLoader loader = new FXMLLoader();
        Parent root =  loader.load(getClass().getResource("Window.fxml").openStream());
        controller = loader.getController();
        controller.context = this;
        stage = primaryStage;

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();
    }

    public WindowController GetController() { return controller; }
    public Stage GetStage() { return stage; }

    public void Post(Runnable task) {
        Platform.runLater(task);
    }

}

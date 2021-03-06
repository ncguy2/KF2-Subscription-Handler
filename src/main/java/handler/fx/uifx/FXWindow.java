package handler.fx.uifx;

//
// Author: Nick Guy
// Created at: 25/08/2017
//

import handler.fx.ThemeManager;
import handler.observable.ObservableValue;
import handler.threading.TaskHandler;
import handler.ui.Strings;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.lang.reflect.Field;

public class FXWindow extends Application {

    protected WindowController controller;
    protected Stage stage;
    protected Scene scene;
    protected TaskHandler taskHandler;
    public static final ObservableValue<Boolean> useTabIcons = new ObservableValue<>(false);

    protected static FXWindow mainContext;

    @Override
    public void start(Stage primaryStage) throws IOException {
        stage = primaryStage;

        taskHandler = new TaskHandler(this);

        if(mainContext == null) mainContext = this;

        TooltipShowDelay(250);

        primaryStage.setTitle(Strings.WINDOW_TITLE);
        primaryStage.setResizable(true);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/handler/fxml/Window.fxml"));
        Parent root = loader.load();
        controller = loader.getController();

        scene = new Scene(root);
        ThemeManager.ApplyTheme(scene);

        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();
    }

    private void TooltipShowDelay(int delayMs) {
        Tooltip tooltip = new Tooltip();
        try {
            Field fieldBehavior = tooltip.getClass().getDeclaredField("BEHAVIOR");
            fieldBehavior.setAccessible(true);
            Object objBehavior = fieldBehavior.get(tooltip);

            Field fieldTimer = objBehavior.getClass().getDeclaredField("activationTimer");
            fieldTimer.setAccessible(true);
            Timeline objTimer = (Timeline) fieldTimer.get(objBehavior);

            objTimer.getKeyFrames().clear();
            objTimer.getKeyFrames().add(new KeyFrame(Duration.millis(delayMs)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public WindowController GetController() { return controller; }
    public Stage GetStage() { return stage; }

    public void Post(Runnable task) {
        Platform.runLater(task);
    }

    public TaskHandler GetTaskHandler() {
        return taskHandler;
    }

    public static void PostStatic(Runnable task) {
        if(mainContext == null) return;
        mainContext.Post(task);
    }

    public static FXWindow GetMainContext() {
        return mainContext;
    }

}

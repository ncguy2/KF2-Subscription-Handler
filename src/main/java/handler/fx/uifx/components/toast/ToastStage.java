package handler.fx.uifx.components.toast;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ToastStage extends Stage {

    public final Point2D bottomRight;

    public ToastStage(AnchorPane pane, StageStyle style) {
        initStyle(style);
        SetSize(pane.getPrefWidth(), pane.getPrefHeight());

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double x = screenBounds.getMinX() + screenBounds.getWidth()  - pane.getPrefWidth()  - 2;
        double y = screenBounds.getMinY() + screenBounds.getHeight() - pane.getPrefHeight() - 2;
        bottomRight = new Point2D(x, y);
    }

    public void SetSize(double width, double height) {
        setWidth(width);
        setHeight(height);
    }

    public Point2D GetOffScreenBounds() {
        return new Point2D(bottomRight.getX() + getWidth(), bottomRight.getY());
    }

    public void SetLocation(Point2D loc) {
        setX(loc.getX());
        setY(loc.getY());
    }

    private SimpleDoubleProperty xLocationProperty = new SimpleDoubleProperty() {
        @Override
        public void set(double newValue) {
            setX(newValue);
        }

        @Override
        public double get() {
            return getX();
        }
    };

    public SimpleDoubleProperty xLocationProperty() {
        return xLocationProperty;
    }

    private SimpleDoubleProperty yLocationProperty = new SimpleDoubleProperty() {
        @Override
        public void set(double newValue) {
            setY(newValue);
        }

        @Override
        public double get() {
            return getY();
        }
    };

    public SimpleDoubleProperty yLocationProperty() {
        return yLocationProperty;
    }
}

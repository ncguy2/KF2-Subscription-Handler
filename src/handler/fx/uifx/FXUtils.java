package handler.fx.uifx;

import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class FXUtils {

    public static SequentialTransition CreateTransition(final ImageView iv, final Image image) {
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.4), iv);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> iv.setImage(image));

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.4), iv);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        return new SequentialTransition(fadeOut, fadeIn);
    }

}

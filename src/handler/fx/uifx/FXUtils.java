package handler.fx.uifx;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.scene.control.Labeled;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.util.Optional;

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

    public static Optional<Animation> TextTransition(final Labeled field, String target, int millis) {
        if(target.equalsIgnoreCase(field.getText())) {
            field.setText(target);
            return Optional.empty();
        }
        return Optional.of(new Transition() {
            private final int length = target.length();
            private final String oldText = field.getText();

            { setCycleDuration(Duration.millis(millis)); }

            @Override
            protected void interpolate(double frac) {
                // NDC = [-1, 1]
                // Remove old text in 25% of the time as it does to write the new text (20% of millis)
                double ndc = (frac * 1.25) - .25;
                String str = target;
                if(ndc < 0) {
                    str = oldText;
                    ndc *= 4;
                }
                final int n = Math.round(length * (float)Math.abs(ndc));
                field.setText(str.substring(0, Math.min(str.length(), n)));
            }
        });
    }

}

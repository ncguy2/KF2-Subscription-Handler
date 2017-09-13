package handler.fx.uifx;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Labeled;
import javafx.scene.control.TextInputControl;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.io.IOException;
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

    public static void AddNumericTextFilter(TextInputControl control) {
        control.textProperty().addListener((observable, oldValue, newValue) -> {
            // if new value is not all digits
            if(!newValue.matches("\\d*")) {
                // Remove all non-digits
                control.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    public static <T> Optional<FXMLFragment<T>> LoadFragmentSafe(String fxmlCp, Class<?> resourceRoot) {
        try {
            FXMLFragment<T> fragment = LoadFragment(fxmlCp, resourceRoot);
            return Optional.of(fragment);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public static <T> FXMLFragment<T> LoadFragment(String fxmlCp, Class<?> resourceRoot) throws IOException {
        FXMLLoader loader = new FXMLLoader(resourceRoot.getResource(fxmlCp));
        loader.setClassLoader(resourceRoot.getClassLoader());
        Parent p = loader.load();
        T ctrlr = loader.getController();
        FXMLFragment<T> fragment = new FXMLFragment<>(p, ctrlr);
        fragment.loader = loader;
        return fragment;
    }

    public static class FXMLFragment<T> {
        public FXMLLoader loader;
        public Node rootNode;
        public T controller;

        public FXMLFragment() { this(null); }
        public FXMLFragment(Node rootNode) { this(rootNode, null); }

        public FXMLFragment(Node rootNode, T controller) {
            this.rootNode = rootNode;
            this.controller = controller;
        }
    }

}

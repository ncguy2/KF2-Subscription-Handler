package handler.fx;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class IconLoader {

    public static Image LoadIcon(Icons icon) {
        return new Image(IconLoader.class.getResourceAsStream("icons/"+icon.path));
    }

    public static ImageView GetIcon(Icons icon) {
        return new ImageView(LoadIcon(icon));
    }

}

package handler.fx;

import handler.observable.ObservableValue;
import handler.ui.Strings;
import handler.utils.StringUtils;
import javafx.scene.Scene;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import static handler.ui.Strings.CACHE_ROOT_DIRECTORY;

public class ThemeManager {

    public static ObservableValue<String> activeTheme = new ObservableValue<>("");
    public static Scene targetScene = null;
    public static final String ExternalThemeDir = CACHE_ROOT_DIRECTORY + "themes";

    static {
        activeTheme.AddListener((observable, oldValue, newValue) -> {
            if(targetScene == null) return;
            if(targetScene.getStylesheets().contains(oldValue))
                targetScene.getStylesheets().remove(oldValue);
            if(!newValue.isEmpty())
                targetScene.getStylesheets().add(newValue);
        });
    }

    public static void ApplyTheme(Scene scene) {
        // TODO remove previous theme when applying to dialogs
        if(scene == null) return;
        if(!activeTheme.GetValue().isEmpty())
            scene.getStylesheets().add(activeTheme.GetValue());
    }

    public static void ApplyTheme(Scene scene, Themes theme) {
        if(theme.theme.isEmpty()) {
            ApplyTheme(scene, theme.theme);
            return;
        }
        URL resource = ThemeManager.class.getResource("/css/themes/" + theme.theme + ".css");
        String s = resource.toExternalForm();
        ApplyTheme(scene, s);
    }

    public static void ApplyTheme(Scene scene, String theme) {
        if(theme.equals(activeTheme.toString())) return;
        targetScene = scene;
        activeTheme.SetValue(theme);
    }

    public static Optional<Map<String, String>> FindExternalThemes() {
        String dir = Strings.Mutable.WORKING_DIRECTORY.GetValue() + ExternalThemeDir;
        File f = new File(dir);
        if(!f.exists()) {
            f.mkdirs();
            return Optional.empty();
        }

        File[] files = f.listFiles((dir1, name) -> name.endsWith(".css"));
        if(files == null) return Optional.empty();

        Map<String, String> paths = new HashMap<>();
        for (File file : files)
            try {
                paths.put(StringUtils.ToDisplayCase(file.getName().replaceAll("_", " ")), new URL("file:///"+file.getAbsolutePath()).toExternalForm());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        return Optional.of(paths);
    }

    public static enum Themes {
        DEFAULT(""),
        BOOTSTRAP_2("bootstrap2"),
        BOOTSTRAP_3("bootstrap3"),
        ;

        Themes(String theme) {
            this.theme = theme;
        }

        public final String theme;

        public String Title() {
            return StringUtils.ToDisplayCase(name().replaceAll("_", " "));
        }

    }

}

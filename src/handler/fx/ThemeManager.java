package handler.fx;

import handler.ui.Strings;
import handler.utils.StringUtils;
import javafx.scene.Scene;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import static handler.ui.Strings.CACHE_ROOT_DIRECTORY;

public class ThemeManager {

    public static String activeTheme = null;
    public static final String ExternalThemeDir = CACHE_ROOT_DIRECTORY + "themes";

    public static void ApplyTheme(Scene scene) {
        ApplyTheme(scene, activeTheme);
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
        if(theme.equals(activeTheme)) return;
        if(scene.getStylesheets().contains(activeTheme))
            scene.getStylesheets().remove(activeTheme);
        if(!theme.isEmpty())
            scene.getStylesheets().add(theme);
        activeTheme = theme;
    }

    public static Optional<Map<String, String>> FindExternalThemes() {
        String dir = Strings.Mutable.WORKING_DIRECTORY + ExternalThemeDir;
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

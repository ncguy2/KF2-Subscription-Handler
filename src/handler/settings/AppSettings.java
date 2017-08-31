package handler.settings;

import handler.fx.ThemeManager;
import handler.ui.Strings;

public class AppSettings {

    @AppSetting(Class = Strings.Mutable.class, Field = "WORKING_DIRECTORY", Observable = true)
    public String directory = "";

    @AppSetting(Class = ThemeManager.class, Field = "activeTheme", Observable = true)
    public String theme = "";

    @AppSetting(Class = Strings.Mutable.class, Field = "STEAMCMD_PATH", Observable = true)
    public String steamCmd = "";

}

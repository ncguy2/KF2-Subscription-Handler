package handler.settings;

import handler.ui.Strings;

public class AppSettings {

    @AppSetting(Class = Strings.Mutable.class, Field = "WORKING_DIRECTORY", Observable = true)
    public String directory = "";

}

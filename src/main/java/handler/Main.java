package handler;

import handler.fx.uifx.FXWindow;
import handler.settings.AppSettingsHandler;
import handler.steam.SteamCache;
import handler.ui.Window;
import javafx.application.Application;

import javax.swing.*;

public class Main {

    public static AppSettingsHandler applicationSettings;

    public static void main(String... args) throws Exception {
        boolean useLegacy = false;

        for(int i = 0; i < args.length; i++) {
            if(args[i].equalsIgnoreCase("-legacy"))
                useLegacy = true;
        }


        applicationSettings = new AppSettingsHandler();
        if(!applicationSettings.Load()) {
            applicationSettings.Pop().Save();
        }else applicationSettings.Push();

        SteamCache.PopulateIndex();

        if(useLegacy)
            SwingUtilities.invokeLater(Window::new);
        else Application.launch(FXWindow.class, args);
        applicationSettings.PopAndSave();
    }
    
}

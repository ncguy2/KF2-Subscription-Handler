package handler.steam;

import java.util.HashMap;
import java.util.Map;

import static handler.steam.SteamCmdProcessCallback.StringPatterns.*;

public class SteamCmdProcessCallback {

    public SteamCmdProcessCallback() {
        patternFunctions = new HashMap<>();
        patternFunctions.put(INIT, this::OnSteamCMDInit);
        patternFunctions.put(LOGIN_SUCCESS, this::OnSteamCMDLoginSuccess);
        patternFunctions.put(LOGIN, this::OnSteamCMDLogin);
        patternFunctions.put(USER_INFO_SUCCESS, this::OnSteamCMDUserInfoSuccess);
        patternFunctions.put(USER_INFO, this::OnSteamCMDUserInfo);
        patternFunctions.put(DOWNLOAD_START, this::OnSteamCMDDownloadStart);
        patternFunctions.put(DOWNLOAD_SUCCESS, this::OnSteamCMDDownloadSuccess);
    }

    protected Map<String, Runnable> patternFunctions;
    protected String currentLine = "";

    public void OnSteamCMDInit() {}
    public void OnSteamCMDLogin() {}
    public void OnSteamCMDLoginSuccess() {}
    public void OnSteamCMDUserInfo() {}
    public void OnSteamCMDUserInfoSuccess() {}
    public void OnSteamCMDDownloadStart() {}
    public void OnSteamCMDDownloadSuccess() {}

    public void OnFinish() {}

    public void CoreFunction(final String line) {
        if(line.matches("\r|\n|(\r\n)")) {
            currentLine = "";
            return;
        }
        currentLine += line;
        for (Map.Entry<String, Runnable> entry : patternFunctions.entrySet()) {
            if(currentLine.toLowerCase().contains(entry.getKey().toLowerCase())) {
                entry.getValue().run();
                return;
            }
        }
    }

    public static class StringPatterns {
        public static final String INIT = "Steam Console Client (c) Valve Corporation";
        public static final String LOGIN_SUCCESS = "to Steam Public...Logged in OK";
        public static final String LOGIN = "to Steam Public...";
        public static final String USER_INFO = "Waiting for user info";
        public static final String USER_INFO_SUCCESS = "Waiting for user info...OK";
        public static final String DOWNLOAD_START = "Downloading item";
        public static final String DOWNLOAD_SUCCESS = "Success. Downloaded item";

    }

}

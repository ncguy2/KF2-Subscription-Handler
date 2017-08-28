package handler.ui;

import java.io.File;

public abstract class Strings {
    
    public static final String NULL = "";
    public static final String CURRENT_DIRECTORY = System.getProperty("user.dir");
    
    public static final String WINDOW_TITLE = "KF2 Server Workshop Tool";
    public static final String WINDOW_BUTTON_CYCLE = "Refresh Map Cycle";
    public static final String WINDOW_BUTTON_SAVE = "Save Map Cycle";
    public static final String WINDOW_BUTTON_SUBSCRIPTION = "Refresh Subscriptions";
    public static final String WINDOW_BUTTON_SORT = "Sort Cycle alphabetically";
    public static final String WINDOW_BUTTON_ADD = "Add selected";
    public static final String WINDOW_BUTTON_REMOVE = "Remove selected";
    public static final String WINDOW_BUTTON_SUBSCRIBE = "Subscribe to Map";
    
    public static final String WINDOW_BUTTON_RENAME = "Rename file [Coming soon!]";
    public static final String WINDOW_BUTTON_DELETE = "Delete file [Coming soon!]";
    
    public static final String BUTTON_CYCLE = "#buttonMapCycle";
    public static final String BUTTON_SAVE = "#buttonSaveMapCycle";
    public static final String BUTTON_SUBSCRIPTION = "#buttonReadSubs";
    public static final String BUTTON_SORT = "#buttonSortMapCycle";
    public static final String BUTTON_ADD = "#buttonAddMap";
    public static final String BUTTON_REMOVE = "#buttonRemoveMap";
    public static final String BUTTON_SUBSCRIBE = "#buttonSubscribeMap";
    
    public static final String LIST_CYCLE = "#listMapCycle";
    public static final String LIST_SUBSCRIPTION = "#listSubscriptions";
    
    public static final String TEXTFIELD_SUBSCRIPTION = "#fieldSubscription";
    
    public static final String ERROR_NO_CYCLE = "No Map Cycle found.";
    public static final String ERROR_NOT_WORKSHOP = "#downloadNotFromWorkshop";
    public static final String ERROR_NO_FILE(String fileName) {
        return "(" + fileName + ") File or privileges missing.";
    }

    public static final String CACHE_ROOT_DIRECTORY = File.separator + "KF2SubscriptionHandler" + File.separator;

    public static final String HTTP_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:57.0) Gecko/20100101 Firefox/57.0";
    public static final String CACHE_IMAGE_FORMAT = "png";

    public static final long KF2AppId = 232090L;

    public static class Mutable {

        public static String WORKING_DIRECTORY = CURRENT_DIRECTORY;

    }
    
}

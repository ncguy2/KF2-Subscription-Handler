package handler.ui;

import handler.extensions.IExtension;
import handler.observable.ObservableValue;

import java.io.File;

public abstract class Strings {

    public static final long KF2AppId = 232090L;

    public static final String DIRECTORY_NAME = "KF2SubscriptionHandler";

    public static final String NULL = "";
    public static final String CURRENT_DIRECTORY = System.getProperty("user.dir");
    public static final String APPDATA_DIRECTORY = System.getenv("APPDATA") + File.separator + DIRECTORY_NAME + File.separator;
    public static final String BINARY_DIRECTORY = File.separator + "Binaries" + File.separator + "Win64" + File.separator;
    public static final String WORKSHOP_DIRECTORY = BINARY_DIRECTORY + "steamapps" + File.separator + "workshop" + File.separator + "content" + File.separator + String.valueOf(KF2AppId) + File.separator;
    public static final String CACHE_DIRECTORY = File.separator + "KFGame" + File.separator + "Cache" + File.separator;

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

    public static final String CACHE_ROOT_DIRECTORY = File.separator + DIRECTORY_NAME + File.separator;
    public static final String CACHE_EXTENSION_DIRECTORY = CACHE_ROOT_DIRECTORY + "Extensions" + File.separator;

    // TODO change user agent to be more accurate
    public static final String HTTP_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:57.0) Gecko/20100101 Firefox/57.0";
    public static final String CACHE_IMAGE_FORMAT = "png";

    public static final String APP_SETTINGS_FILE = APPDATA_DIRECTORY + "settings.json";

    public static class Mutable {

        public static ObservableValue<String> WORKING_DIRECTORY = new ObservableValue<>(CURRENT_DIRECTORY);
        public static ObservableValue<String> STEAMCMD_PATH = new ObservableValue<>(NULL);

    }

    public static class Accessors {

        public static String CacheDirectory() {
            return Mutable.WORKING_DIRECTORY.GetValue() + CACHE_ROOT_DIRECTORY;
        }

        public static String GlobalExtensionDirectory() {
            return CURRENT_DIRECTORY + File.separator + "Extensions";
        }

        public static String InstanceExtensionDirectory() {
            return Mutable.WORKING_DIRECTORY.GetValue() + CACHE_EXTENSION_DIRECTORY;
        }

        public static String InstanceExtensionDirectory(IExtension extension) {
            return InstanceExtensionDirectory() + extension.Id() + File.separator;
        }

        public static File ExtensionDirFile(IExtension extension) {
            return new File(InstanceExtensionDirectory(extension));
        }

        public static String ExtensionStorageDirectory(IExtension extension) {
            StringBuilder sb = new StringBuilder();
            sb.append(CacheDirectory())
                    .append("cache")
                    .append(File.separator)
                    .append("Extensions")
                    .append(File.separator)
                    .append(extension.Id())
                    .append(File.separator);
            return sb.toString();
        }

        public static File ExtensionStorageDirFile(IExtension extension) {
            return new File(ExtensionStorageDirectory(extension));
        }

    }

    public static class Commands {

        public static final String WORKSHOP_ITEM = "+workshop_download_item " + String.valueOf(KF2AppId);

    }

}

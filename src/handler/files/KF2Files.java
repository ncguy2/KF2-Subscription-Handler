package handler.files;

import handler.ui.Strings;
import handler.domain.Subscription;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class KF2Files {
    
    private static final String TCP_HEADER = "[IpDrv.TcpNetDriver]";
    private static final String TCP_MANAGER = "DownloadManagers=OnlineSubsystemSteamworks.SteamWorkshopDownload";
    private static final String WORKSHOP_HEADER = "[OnlineSubsystemSteamworks.KFWorkshopSteamworks]";
    private static final String WORKSHOP_SUBSCRIPTION = "ServerSubscribedWorkshopItems=";
    private static final String SERVER_MAPS = "GameMapCycles=(Maps=(";
    
    private static final String KFEngine = "\\KFGame\\Config\\PCServer-KFEngine.ini";
    private static final String KFGame = "\\KFGame\\Config\\PCServer-KFGame.ini";
    private static final String Cache = "\\KFGame\\Cache";
    
    private static final Map<String, Subscription> SUBSCRIPTIONS = new HashMap<>();
    
    
    public static List<String> getMapCycle() throws Exception {
        String maps = readMapCycle(new File(Strings.CURRENT_DIRECTORY + KFGame)).split("\\(")[2].replaceAll("\"", "");
        return Arrays.asList(maps.replaceAll("\\)", "").split(","));
    }
    
    private static String readMapCycle(File file) throws Exception {
        if (!file.canRead()) throw new Exception(Strings.ERROR_NO_FILE("KFGame"));
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String readLine = "...";
        while (!(readLine == null)) {
            if (readLine.contains(SERVER_MAPS)) {
                reader.close();
                return readLine;
            }
            readLine = reader.readLine();
        }
        throw new Exception(Strings.ERROR_NO_CYCLE);
    }
    
    
    public static Map<String, Subscription> getSubscriptions() throws Exception {
        readSubscriptions(new File(Strings.CURRENT_DIRECTORY + KFEngine));
        return readSubscriptionNames(Paths.get(Strings.CURRENT_DIRECTORY + Cache).toFile());
    }
    
    private static Map<String, Subscription> readSubscriptions(File file) throws Exception {
        if (!file.canRead()) throw new Exception(Strings.ERROR_NO_FILE("KFEngine"));
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String readLine = "...";
        while (!(readLine == null)) {
            if (readLine.contains(WORKSHOP_HEADER)) {
                // Workshop files found, read contents
                while (true) {
                    readLine = reader.readLine();
                    if (!readLine.contains(WORKSHOP_SUBSCRIPTION))
                        break;
                    SUBSCRIPTIONS.put(readLine.split("=")[1], new Subscription(readLine.split("=")[1]));
                }
            }
            readLine = reader.readLine();
        }
        return SUBSCRIPTIONS;
    }
    
    private static Map<String, Subscription> readSubscriptionNames(File file) throws Exception {
        if (!file.canRead()) throw new Exception(Strings.ERROR_NO_FILE("Cache"));
        for (File folder : file.listFiles()) {
            String subscriptionName = readFileName(folder);
            if (SUBSCRIPTIONS.containsKey(folder.getName()) && !subscriptionName.equals(Strings.ERROR_NOT_WORKSHOP))
                SUBSCRIPTIONS.get(folder.getName()).setName(
                    subscriptionName.replaceAll(".kfm", ""));
        }
        return SUBSCRIPTIONS;
    }
    
    private static String readFileName(File file) throws Exception {
        if (!(file.listFiles() == null)) {
            for (File fileEntry : file.listFiles()) {
                if (fileEntry.getName().contains(".kfm"))
                    return fileEntry.getName();
                return readFileName(fileEntry);
            }
        }
        return Strings.ERROR_NOT_WORKSHOP;
    }
    
    
    public static void setMapCycle(String input) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(Strings.CURRENT_DIRECTORY + KFGame));
        String fileContents = "";
        String readLine;
        while (!((readLine = reader.readLine()) == null)) {
            if (readLine.contains(SERVER_MAPS)) fileContents += input + System.lineSeparator();
            else fileContents += readLine + System.lineSeparator();
        }
        reader.close();
        FileOutputStream file = new FileOutputStream(Strings.CURRENT_DIRECTORY + KFGame);
        file.write(fileContents.getBytes());
        file.close();
    }
    
}

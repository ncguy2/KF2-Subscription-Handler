package handler.files;

import handler.ui.Strings;
import handler.domain.Subscription;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.*;

public abstract class KF2Files {
    
    private static final String TCP_HEADER = "[IpDrv.TcpNetDriver]";
    private static final String TCP_MANAGER = "DownloadManagers=OnlineSubsystemSteamworks.SteamWorkshopDownload";
    private static final String WORKSHOP_HEADER = "[OnlineSubsystemSteamworks.KFWorkshopSteamworks]";
    private static final String WORKSHOP_SUBSCRIPTION = "ServerSubscribedWorkshopItems=";
    private static final String SERVER_MAPS = "GameMapCycles=(Maps=(";
    
    private static final String KFEngine = "\\KFGame\\Config\\PCServer-KFEngine.ini";
    private static final String KFGame = "\\KFGame\\Config\\PCServer-KFGame.ini";
    private static final String Cache = "\\KFGame\\Cache";

    private static final String DefaultGame = "\\KFGame\\Config\\DefaultGame.ini";
    
    private static final Map<String, Subscription> SUBSCRIPTIONS = new HashMap<>();
    
    
    public static List<String> getMapCycle() throws Exception {
        String maps = readMapCycle(new File(Strings.Mutable.WORKING_DIRECTORY + KFGame)).split("\\(")[2].replaceAll("\"", "");
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
        readSubscriptions(new File(Strings.Mutable.WORKING_DIRECTORY + KFEngine));
        return readSubscriptionNames(Paths.get(Strings.Mutable.WORKING_DIRECTORY + Cache).toFile());
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
            if (SUBSCRIPTIONS.containsKey(folder.getName()) && !subscriptionName.equals(Strings.ERROR_NOT_WORKSHOP)) {
                Subscription sub = SUBSCRIPTIONS.get(folder.getName());
                sub.setName(subscriptionName.replaceAll(".kfm", ""));
                sub.setOnDisk(true);
            }
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
        BufferedReader reader = new BufferedReader(new FileReader(Strings.Mutable.WORKING_DIRECTORY + KFGame));
        String fileContents = "";
        String readLine;
        while (!((readLine = reader.readLine()) == null)) {
            if (readLine.contains(SERVER_MAPS)) fileContents += input + System.lineSeparator();
            else fileContents += readLine + System.lineSeparator();
        }
        reader.close();
        FileOutputStream file = new FileOutputStream(Strings.Mutable.WORKING_DIRECTORY + KFGame);
        file.write(fileContents.getBytes());
        file.close();
    }

    public static void setMapCycle(List<?> maps) throws Exception {
        setMapCycle(formatCycleString(maps));
    }
    
    
    public static void addSubscription(int input) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(Strings.Mutable.WORKING_DIRECTORY + KFEngine));
        String fileContents = "";
        String readLine;
        while (!((readLine = reader.readLine()) == null)) {
            fileContents += readLine + System.lineSeparator();
            if (readLine.contains(WORKSHOP_HEADER)) {
                    fileContents += WORKSHOP_SUBSCRIPTION + input + System.lineSeparator();
            }
        }
        reader.close();
        FileOutputStream file = new FileOutputStream(Strings.Mutable.WORKING_DIRECTORY + KFEngine);
        file.write(fileContents.getBytes());
        file.close();
    }

    public static void AddSubscription(int input) {
        try {
            addSubscription(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String formatCycleString(List<?> maps) {
        StringBuilder builder = new StringBuilder();
        builder.append("GameMapCycles=(Maps=(");
        for (Object map : maps) {
            builder.append("\"").append(map.toString()).append("\"").append(",");
        }
        builder.deleteCharAt(builder.length() - 1);
        builder.append("))");
        return builder.toString();
    }

    private static List<String> nativeMaps = null;

    private static void LoadNativeMaps() throws Exception {
        String mapString = readMapCycle(new File(Strings.Mutable.WORKING_DIRECTORY + DefaultGame));
        mapString = mapString.split("\\(")[2].replaceAll("\"", "");
        List<String> maps = Arrays.asList(mapString.replaceAll("\\)", "").split(","));
        nativeMaps.clear();
        nativeMaps.addAll(maps);
    }

    public static List<String> GetNativeMaps() {
        if(nativeMaps == null) {
            nativeMaps = new ArrayList<>();
            try {
                LoadNativeMaps();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return nativeMaps;
    }

    public static boolean IsNativeMap(String name) {
        return GetNativeMaps().contains(name);
    }
    public static int NativeIndex(String name) {
        return GetNativeMaps().indexOf(name);
    }

}

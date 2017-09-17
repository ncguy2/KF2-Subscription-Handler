package handler.files;

import handler.domain.Subscription;
import handler.ui.Strings;
import handler.utils.StringUtils;
import org.ini4j.Profile;

import java.io.*;
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
    private static final String Localization = "\\KFGame\\Localization";
    private static final String LocalizationPattern = Localization + "\\%s\\%s.%s";

    private static final String DefaultGame = "\\KFGame\\Config\\DefaultGame.ini";
    
    private static final Map<String, Subscription> SUBSCRIPTIONS = new HashMap<>();

    private static final Map<String, String> nativeMaps = new HashMap<>();

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

    public static Optional<Map<String, String>> GetINIBlock(String blockHeader, String iniFile) throws IOException {
//        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(Strings.Mutable.WORKING_DIRECTORY + iniFile), "UTF-8"));

        IniFile file = new IniFile(Strings.Mutable.WORKING_DIRECTORY + iniFile);
        Optional<Profile.Section> section = file.GetSection(blockHeader);

        // Unbox-box used to avoid "Incompatible types" error caused by generic arguments
        if(section.isPresent())
            return Optional.of(section.get());

        return Optional.empty();

//        File file = new File(Strings.Mutable.WORKING_DIRECTORY + iniFile);
//        Path path = file.toPath();
//        List<String> strings = Files.readAllLines(path, StandardCharsets.UTF_16LE);
//
//        Map<String, String> iniBlock = new HashMap<>();
//        boolean foundBlock = false;
//        for (String readLine : strings) {
//            if(readLine.isEmpty()) continue;
//            if(readLine.contains(blockHeader)) {
//                foundBlock = true;
//                continue;
//            }
//            if(!foundBlock) continue;
//
//            if(readLine.startsWith("["))
//                break;
//
//            int i = readLine.indexOf('=');
//            if(i <= -1) continue;
//            String key = readLine.substring(0, i);
//            String value = readLine.substring(i+1);
//            iniBlock.put(key, value);
//        }
//        return iniBlock;
    }

    public static Optional<Map<String, String>> GetI18NINIBlock(String mapName, I18N i18n) {
        try {
            return GetINIBlock("["+mapName+" KFMapSummary]", String.format(LocalizationPattern, i18n.Name(), "KFGame", i18n.Ext()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public static I18N activeLoc = I18N.INT;

    private static void LoadNativeMaps() throws Exception {
        String mapString = readMapCycle(new File(Strings.Mutable.WORKING_DIRECTORY + DefaultGame));
        mapString = mapString.split("\\(")[2].replaceAll("\"", "");
        List<String> maps = Arrays.asList(mapString.replaceAll("\\)", "").split(","));
        nativeMaps.clear();

        for (String map : maps) {
            Optional<Map<String, String>> i18nMap = GetI18NINIBlock(map, activeLoc);
            String name = map;
            if(i18nMap.isPresent()) {
                Map<String, String> loc = i18nMap.get();
                String s = loc.get("DisplayName_" + activeLoc.Name());
                if(s != null) name = StringUtils.Trim(s, '"');
            }
            nativeMaps.put(map, name);
        }
    }

    public static Map<String, String> NativeMaps() {
        if(nativeMaps.isEmpty()) {
            try {
                LoadNativeMaps();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(nativeMaps.size());
        }
        return nativeMaps;
    }

    public static Collection<String> GetNativeMaps() {
        return NativeMaps().keySet();
    }

    public static Collection<String> GetNativeMapNames() {
        return NativeMaps().values();
    }

    public static boolean IsNativeMap(String name) {
        Collection<String> strings = GetNativeMaps();
        return strings.contains(name);
    }
    public static int NativeIndex(String name) {
        return StringUtils.IndexOf(GetNativeMaps(), name);
    }

    public static enum I18N {
        CHN("Chinese"),
        CHT("Chinese-Traditional"),
        CZE("Czech"),
        DAN("Danish"),
        DEU("German"),
        DUT("Dutch"),
        ESL("Spanish"),
        ESN("Spanish-European"),
        FRA("French"),
        FRC("French-Canadian"),
        HUN("Hungarian"),
        INT("English"),
        ITA("Italian"),
        JPN("Japanese"),
        KOR("Korean"),
        POL("Polish"),
        POR("Portuguese"),
        PTB("Portuguese-Brazilian"),
        RUS("Russian"),
        TUR("Turkish"),
        UKR("Ukranian"),
        ;

        public final String displayName;

        I18N() {
            this.displayName = StringUtils.ToDisplayCase(name());
        }

        I18N(String name) {
            this.displayName = StringUtils.ToDisplayCase(name);
        }

        public String Name() {
            return name();
        }

        public String Ext() {
            return name().toLowerCase();
        }

    }

}

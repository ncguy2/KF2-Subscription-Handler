package handler.steam;

import handler.ui.Strings;
import handler.utils.FileUtils;

import java.io.*;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.function.Consumer;

public class SteamCmdHandler {

    public static void ExecuteCommand(SteamCommand command, long itemId, SteamCmdProcessCallback callback) {
        ExecuteCommand(command, itemId, callback::CoreFunction, callback::OnFinish);
    }

    public static void ExecuteCommand(SteamCommand command, long itemId) {
        ExecuteCommand(command, itemId, null, null);
    }

    public static void ExecuteCommand(SteamCommand command, long itemId, Consumer<String> lineReader) {
        ExecuteCommand(command, itemId, lineReader, null);
    }

    public static void ExecuteCommand(SteamCommand command, long itemId, Runnable onComplete) {
        ExecuteCommand(command, itemId, null, onComplete);
    }

    public static void ExecuteCommand(SteamCommand command, long itemId, Consumer<String> lineReader, Runnable onComplete) {
        String s = command.BuildCommandString(itemId);
        InvokeProcess(s, lineReader != null ? lineReader : SteamCmdHandler::DefaultLineReader, onComplete != null ? onComplete : SteamCmdHandler::DefaultCmdComplete, itemId);
    }

    private static void DefaultLineReader(String line) {
        System.out.print(line);
    }

    private static void DefaultCmdComplete() {
        System.out.println("Command completed");
    }

    private static void InvokeProcess(String cmdString, Consumer<String> lineReader, Runnable onComplete, long itemId) {
        new Thread(() -> {
            Process process = null;
            try {
                process = new ProcessBuilder(cmdString.split(" ")).start();
            } catch (IOException e) {
                e.printStackTrace();
            }

            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);

            String line;
            System.out.printf("Output of invoking %s is: \n", cmdString);

            try {
                while(!Objects.equals(line = String.valueOf((char) br.read()), String.valueOf((char) -1)))
                    lineReader.accept(line);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(process.isAlive()) {
                process.destroy();
            }

            String itemDir = Strings.Mutable.WORKING_DIRECTORY.GetValue() + Strings.WORKSHOP_DIRECTORY + itemId;
            String targetDir = Strings.Mutable.WORKING_DIRECTORY.GetValue() + Strings.CACHE_DIRECTORY + itemId + File.separator + "0" + File.separator;
            File itemFile = new File(itemDir);
            File targetFile = new File(targetDir);

            try {
                FileUtils.CopyFileOrFolder(itemFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }

            onComplete.run();
        }).start();
    }

}

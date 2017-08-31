package handler.steam;

import handler.ui.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SteamCommand {

    public static Supplier<String> steamCmdPathSupplier = Strings.Mutable.STEAMCMD_PATH::GetValue;
    public static String login = "+login";
    public static String prefix = "+force_install_dir";
    public static Supplier<String> installDirSupplier = () -> Strings.Mutable.WORKING_DIRECTORY.GetValue() + Strings.BINARY_DIRECTORY;
    public static String suffix = "+quit";

    public String credentials = "";
    public String command = "";

    public SteamCommand(String credentials, String command) {
        this.credentials = credentials;
        this.command = command;
    }

    public SteamCommand(String command) {
        this("anonymous", command);
    }

    public String BuildCommandString(long itemId) {
        List<String> cmd = new ArrayList<>();

        cmd.add(steamCmdPathSupplier.get());
        cmd.add(login);
        cmd.add(credentials);
        cmd.add(prefix);
        cmd.add(installDirSupplier.get());
        cmd.add(command);
        cmd.add(String.valueOf(itemId));
        cmd.add(suffix);

        return cmd.stream().collect(Collectors.joining(" "));
    }

    public static SteamCommand WorkshopItemCommand() {
        return new SteamCommand(Strings.Commands.WORKSHOP_ITEM);
    }

}

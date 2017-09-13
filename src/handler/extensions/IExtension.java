package handler.extensions;

import handler.fx.uifx.FXWindow;
import handler.fx.uifx.WindowController;
import handler.threading.Task;
import handler.ui.Strings;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class IExtension {

    public abstract String DisplayName();
    public String GetStylesheet() { return null; }
    public ExtensionStyle GetTabStyle() { return null; }
    public void GetTabs(List<WindowController.TabInfo> tabs) {}
    public void OnUnload() {}

    public String DesiredId() {
        return getClass().getCanonicalName();
    }

    public FXMLLoader GetFXMLLoader() {
        FXMLLoader loader = new FXMLLoader();
        loader.setClassLoader(getClass().getClassLoader());
        return loader;
    }

    public Optional<InputStream> GetResourceAsStream(String path) {
        return Optional.ofNullable(getClass().getResourceAsStream(path));
    }

    public Optional<URL> GetResource(String path) {
        return Optional.ofNullable(getClass().getResource(path));
    }

    private String id = null;
    public final String Id() {
        if(id == null)
            id = DesiredId();
        return id;
    }

    public final Optional<String> GetStylesheet_Impl() {
        return Optional.ofNullable(GetStylesheet());
    }

    public final Optional<ExtensionStyle> GetTabStyle_Impl() {
        return Optional.ofNullable(GetTabStyle());
    }

    public final File File(String filePath) {
        String s = Strings.Accessors.ExtensionStorageDirectory(this);
        return new File(s + filePath);
    }

    public final List<WindowController.TabInfo> GetTabs() {
        List<WindowController.TabInfo> tabs = new ArrayList<>();
        GetTabs(tabs);
        return tabs;
    }

    public final FXWindow GetContext() {
        return FXWindow.GetMainContext();
    }

    public final void PostTask(Task task) {
        task.SetName("Task@"+Id());
        GetContext().GetTaskHandler().Post(task);
    }

    public final void PostFX(Runnable runnable) {
        GetContext().Post(runnable);
    }

    public final Stage GetStage() {
        return GetContext().GetStage();
    }

}

package handler.extensions;

import com.sun.javafx.collections.ObservableMapWrapper;
import handler.fx.uifx.WindowController;
import handler.ui.Strings;
import handler.utils.FileUtils;
import handler.utils.JsonSerializer;
import handler.utils.ReflectionUtils;
import handler.utils.StringUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class ExtensionHandler {

    public static final String PLUGIN_META_EXTENSION = ".plugin";

    private static ExtensionHandler instance;
    public static ExtensionHandler instance() {
        if (instance == null)
            instance = new ExtensionHandler();
        return instance;
    }

    protected final ObservableMapWrapper<String, IExtension> extensionMap;
    protected final List<InstanceExtensionData> instanceExtensions;
    public Runnable afterLoad = null;

    private ExtensionHandler() {
        extensionMap = new ObservableMapWrapper<>(new HashMap<>());
        instanceExtensions = new ArrayList<>();
        LoadAvailableInstanceExtensions();
        Strings.Mutable.WORKING_DIRECTORY.AddListener((observable, oldValue, newValue) -> LoadExtensions());
        LoadExtensions();
    }

    public List<InstanceExtensionData> GetInstanceExtensions() {
        return instanceExtensions;
    }

    public ObservableMapWrapper<String, IExtension> GetExtensionMap() {
        return extensionMap;
    }

    public void LoadAvailableInstanceExtensions() {
        String path = Strings.Accessors.GlobalExtensionDirectory() + File.separator + "Instance";
        File dir = new File(path);
        if(!dir.exists()) {
            dir.mkdirs();
            return;
        }

        File[] files = dir.listFiles((parent, name) -> name.endsWith(PLUGIN_META_EXTENSION));
        assert files != null;
        for (File file : files)
            LoadInstanceExtensionData(file);
    }

    protected void LoadInstanceExtensionData(File file) {
        JsonSerializer.instance()
                .FromJsonFile(file, InstanceExtensionData.class)
                .ifPresent(instanceExtensions::add);
    }

    public void LoadExtensions() {
        UnloadExtensions();
        try {
            LoadExtensions(Strings.Accessors.GlobalExtensionDirectory());
            LoadExtensions(Strings.Accessors.InstanceExtensionDirectory());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if(afterLoad != null)
            afterLoad.run();
    }

    public void UnloadExtensions() {
        extensionMap.values().forEach(this::UnloadExtension);
        extensionMap.clear();
    }

    protected void UnloadExtension(IExtension extension) {
        System.out.printf("Unloading extension: %s [%s]\n", extension.DisplayName(), extension.Id());
        extension.OnUnload();
    }

    protected void LoadExtensions(String rootPath) throws MalformedURLException {
        File dir = new File(rootPath);
        if(!dir.exists()) {
            dir.mkdirs();
            return;
        }

        File[] files = Objects.requireNonNull(dir.listFiles(FileUtils::IsJar));

        URL[] urls = new URL[files.length];
        for (int i = 0; i < files.length; i++)
            urls[i] = files[i].toURI().toURL();

        URLClassLoader ucl = new URLClassLoader(urls, getClass().getClassLoader());
        ServiceLoader<IExtension> sl = ServiceLoader.load(IExtension.class, ucl);
        Iterator<IExtension> iterator = sl.iterator();
        iterator.forEachRemaining(this::LoadExtension);
    }

    protected void LoadExtension(IExtension extension) {
        String id = extension.Id();
        if(extensionMap.containsKey(id)) HandleDuplicateExtension(extension);
        else HandleNewExtension(extension);
    }

    private Field extensionIdField = null;
    private Field GetExtensionIdField() {
        if (extensionIdField == null) {
            Optional<Field> id = ReflectionUtils.GetDeclaredField(IExtension.class, "id");
            id.ifPresent(field -> extensionIdField = field);
        }
        return extensionIdField;
    }

    protected void HandleDuplicateExtension(IExtension extension) {
        ReflectionUtils.Set(extension, GetExtensionIdField(), extension.DesiredId()+"_"+ StringUtils.NewUUIDString());
    }

    protected void HandleNewExtension(IExtension extension) {
        extensionMap.put(extension.Id(), extension);
    }

    public List<WindowController.TabInfo> GetTabs() {
        List<WindowController.TabInfo> tabs = new ArrayList<>();
        extensionMap.values().forEach(e -> e.GetTabs(tabs));
        return tabs;
    }

    public Optional<IExtension> GetExtensionFromClasspath(String cp) {
        for (IExtension extension : extensionMap.values()) {
            if(extension.getClass().getCanonicalName().equalsIgnoreCase(cp))
                return Optional.of(extension);
        }
        return Optional.empty();
    }

    public Optional<IExtension> GetExtensionFromClass(Class<?> cls) {
        for (IExtension extension : extensionMap.values()) {
            if(extension.getClass().equals(cls))
                return Optional.of(extension);
        }
        return Optional.empty();
    }

    public Optional<IExtension> GetExtensionFromId(String id) {
        return Optional.ofNullable(extensionMap.get(id));
    }

    public void LoadExtension(File targetFile) throws MalformedURLException {
        URLClassLoader ucl = new URLClassLoader(new URL[]{targetFile.toURI().toURL()}, getClass().getClassLoader());
        ServiceLoader<IExtension> sl = ServiceLoader.load(IExtension.class, ucl);
        Iterator<IExtension> iterator = sl.iterator();
        iterator.forEachRemaining(this::LoadExtension);
    }
}

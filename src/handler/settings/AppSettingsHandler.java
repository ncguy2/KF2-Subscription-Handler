package handler.settings;

import handler.observable.ObservableValue;
import handler.ui.Strings;
import handler.utils.JsonSerializer;
import handler.utils.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AppSettingsHandler implements Serializable {

    protected transient final String filePath;
    protected transient final AppSettings settings;

    public AppSettingsHandler() {
        this(Strings.APP_SETTINGS_FILE);
    }

    public AppSettingsHandler(String filePath) {
        this.filePath = filePath;
        settings = new AppSettings();
        settingContainers = new ArrayList<>();
        BindSettings();
        Pop();
    }

    public boolean Load() {
        Optional<AppSettings> other = JsonSerializer.instance().FromJsonFile(filePath, AppSettings.class);
        other.ifPresent(source -> {
            try {
                ReflectionUtils.CopyDeclaredMembers(source, this.settings, AppSettings.class);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        return other.isPresent();
    }

    public void Save() {
        JsonSerializer.instance().ToJsonFile(settings, filePath);
    }

    /**
     * Pushes any changes to the active variables
     */
    public AppSettingsHandler Push() {
        settingContainers.forEach(container -> {
            Optional<Object> o = ReflectionUtils.Get(settings, container.localField);
            o.ifPresent(obj -> {
                if(container.isObservable) {
                    Optional<Object> o1 = ReflectionUtils.GetStatic(container.targetField);
                    if(o1.isPresent() && o1.get() instanceof ObservableValue)
                        ((ObservableValue) o1.get()).SetValue(obj);
                }else{
                    ReflectionUtils.SetStatic(container.targetField, obj);
                }
            });
        });
        return this;
    }

    /**
     * Sets all variables to the state of their attached variable
     */
    public AppSettingsHandler Pop() {
        settingContainers.forEach(container -> {
            Optional<Object> o = ReflectionUtils.GetStatic(container.targetField);
            o.ifPresent(obj -> {
                ReflectionUtils.Set(settings, container.localField, obj);
            });
        });
        return this;
    }

    public void PopAndSave() {
        Pop().Save();
    }

    protected void BindSettings() {
        List<Field> fields = ReflectionUtils.GetDeclaredFieldsAnnotatedWith(settings.getClass(), AppSetting.class);
        if(fields.isEmpty()) return;
        fields.forEach(field -> {
            Optional<AppSetting> appSetting = ReflectionUtils.GetAnnotation(field, AppSetting.class);
            appSetting.ifPresent(appSetting1 -> settingContainers.add(new SettingContainer(field, appSetting1)));
        });
    }

    protected transient List<SettingContainer> settingContainers;

    public static class SettingContainer {
        public final Field localField;
        public final Field targetField;
        public final Class<?> targetCls;
        public final boolean valid;
        public final boolean isObservable;

        public SettingContainer(Field localField, AppSetting settingDef) {
            this(localField, settingDef.Class(), settingDef.Field(), settingDef.Observable());
        }

        public SettingContainer(Field localField, Class<?> targetCls, String targetField, boolean isObservable) {
            this.localField = localField;
            Optional<Field> field = ReflectionUtils.GetDeclaredField(targetCls, targetField);

            this.targetField = field.orElse(null);
            this.targetCls = targetCls;

            final boolean[] tempValid = {false};
            this.isObservable = isObservable;

            // Ensure target field is static
            field.ifPresent(f -> tempValid[0] = Modifier.isStatic(f.getModifiers()));

            valid = tempValid[0];

        }
    }

}

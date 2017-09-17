package handler.files;

import org.ini4j.Ini;
import org.ini4j.Profile;
import org.ini4j.Wini;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class IniFile {

    protected Wini ini;

    public IniFile(String fileName) throws IOException {
        this(new File(fileName));
    }

    public IniFile(File file) throws IOException {
        ini = new Wini(file);
    }

    @Override
    protected void finalize() throws Throwable {
        ini.store();
        super.finalize();
    }

    public Optional<String> Get(String section, String key) {
        return Optional.ofNullable(ini.get(section, key));
    }

    public <T> Optional<T> Get(String section, String key, Class<T> type) {
        return Optional.ofNullable(ini.get(section, key, type));
    }

    public Optional<Ini.Section> GetSection(String section) {
        return Optional.ofNullable(ini.get(section));
    }

    public <T> Optional<T> GetAs(String section, Class<T> type) {
        Optional<Profile.Section> sec = GetSection(section);
        if(sec.isPresent())
            return Optional.ofNullable(sec.get().as(type));
        return Optional.empty();
    }

    public <T> void Set(String section, String key, T value) {
        ini.put(section, key, value);
    }

    public <T> void SetAs(String section, T value) {
        Optional<Profile.Section> secOpt = GetSection(section);
        secOpt.ifPresent(sec -> sec.to(value));
    }


    public void Store() throws IOException {
        ini.store();
    }

    public Wini Ini() {
        return ini;
    }

}

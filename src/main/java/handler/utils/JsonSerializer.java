package handler.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JsonSerializer {

    private static JsonSerializer instance;
    public static JsonSerializer instance() {
        if (instance == null)
            instance = new JsonSerializer();
        return instance;
    }

    private Gson gson;

    private JsonSerializer() {
        GsonBuilder gb = new GsonBuilder();
        gb.setPrettyPrinting();
        gson = gb.create();
    }

    public String ToJson(Object obj) {
        return gson.toJson(obj);
    }

    public void ToJsonFile(Object obj, String file) {
        ToJsonFile(obj, new File(file));
    }

    public void ToJsonFile(Object obj, File file) {
        String s = ToJson(obj);
        Path path = file.toPath();
        try {
            Files.deleteIfExists(path);
            Files.createDirectories(path.getParent());
            Files.write(path, s.getBytes(), StandardOpenOption.CREATE_NEW, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public <T> T FromJson(String json, Class<T> cls) {
        return gson.fromJson(json, cls);
    }

    public <T> Optional<T> FromJsonFile(String file, Class<T> cls) {
        return FromJsonFile(new File(file), cls);
    }

    public <T> Optional<T> FromJsonFile(File file, Class<T> cls) {
        Path path = file.toPath();
        if(!Files.exists(path))
            return Optional.empty();
        try {
            List<String> lines = Files.readAllLines(path);
            String json = lines.stream().collect(Collectors.joining("\n"));
            return Optional.ofNullable(FromJson(json, cls));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

}
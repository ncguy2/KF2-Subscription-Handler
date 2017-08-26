package handler.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

    public <T> T FromJson(String json, Class<T> cls) {
        return gson.fromJson(json, cls);
    }

}
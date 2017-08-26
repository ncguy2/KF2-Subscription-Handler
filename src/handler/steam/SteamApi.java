package handler.steam;

//
// Author: Nick Guy
// Created at: 25/08/2017
//

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import handler.domain.Subscription;
import handler.http.HttpMethod;
import handler.http.HttpRequest;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class SteamApi {

    public static class Bindings {

        static final String API_ROOT = "https://api.steampowered.com";
        static final String REMOTE_STORAGE_API = API_ROOT+"/ISteamRemoteStorage";

        public static final String GET_PUBLISHED_FILE_DETAILS = REMOTE_STORAGE_API+"/GetPublishedFileDetails/v1/";

    }

    public static class Functions {

        public static HttpRequest<Map> GetSingleItem(Subscription sub) {
            HttpRequest<Map> request = new HttpRequest<>();
            request.setHref(SteamApi.Bindings.GET_PUBLISHED_FILE_DETAILS);
            request.SetMethod(HttpMethod.POST);
            request.SetParam("key", SteamApi.ApiKey());
            request.SetParam("itemcount", 1);
            request.SetParam("format", "json");
            request.SetParam("publishedfileids[0]", sub.getId());
            GsonBuilder gb = new GsonBuilder();
            gb.setPrettyPrinting();
            Gson gson = gb.create();
            request.SetResponseMapper(str -> gson.fromJson(str, Map.class));
            return request;
        }

        public static HttpRequest<Map> GetSingleItemAsync(Subscription sub) {
            HttpRequest<Map> request = GetSingleItem(sub);
            request.SetAsync(true);
            return request;
        }

    }

    static Properties properties;

    static {
        properties = new Properties();
        try{
            properties.load(new FileInputStream("steam.props"));
        }catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static final String API_KEY_PROP_NAME = "ApiKey";
    public static final String STEAM_ID_PROP_NAME = "SteamId";

    public static String ApiKey() {
        final String defaultValue = "No Key";
        String key = properties.getProperty(API_KEY_PROP_NAME, defaultValue);
        if(key.equalsIgnoreCase(defaultValue))
            throw new RuntimeException(String.format("No %s property found", API_KEY_PROP_NAME));
        return key;
    }

    public static String SteamID() {
        final String defaultValue = "No Key";
        String key = properties.getProperty(STEAM_ID_PROP_NAME, defaultValue);
        if(key.equalsIgnoreCase(defaultValue))
            throw new RuntimeException(String.format("No %s property found", STEAM_ID_PROP_NAME));
        return key;
    }

}

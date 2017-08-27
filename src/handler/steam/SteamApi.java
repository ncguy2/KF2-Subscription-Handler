package handler.steam;

//
// Author: Nick Guy
// Created at: 25/08/2017
//

import handler.domain.Subscription;
import handler.http.HttpMethod;
import handler.http.HttpRequest;
import handler.utils.JsonSerializer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

public class SteamApi {

    public static class Bindings {

        static final String API_ROOT = "https://api.steampowered.com";
        static final String REMOTE_STORAGE_API = API_ROOT+"/ISteamRemoteStorage";

        static final String API_VERSION = "/v1/";

        public static final String GET_PUBLISHED_FILE_DETAILS = REMOTE_STORAGE_API+"/GetPublishedFileDetails" + API_VERSION;
        public static final String GET_COLLECTION_DETAILS = REMOTE_STORAGE_API+"/GetCollectionDetails" + API_VERSION;

    }

    public static class Functions {


        public static HttpRequest<SubscriptionDetails.SubscriptionDetailSet> GetSubscriptionSet(long... itemIds) {
            Optional<String> key = ApiKey();
            if(!key.isPresent()) {
                System.out.println("No API key provided");
                return null;
            }
            HttpRequest<SubscriptionDetails.SubscriptionDetailSet> request = new HttpRequest<>();
            request.setHref(Bindings.GET_PUBLISHED_FILE_DETAILS);
            request.SetMethod(HttpMethod.POST);
            request.SetParam("key", key.get());
            request.SetParam("itemcount", itemIds.length);
            request.SetParam("format", "json");
            for (int i = 0; i < itemIds.length; i++)
                request.SetParam(String.format("publishedfileids[%s]", String.valueOf(i)), itemIds[i]);
            request.SetResponseMapper(str -> {
                SubscriptionDetails.SubscriptionDetailSet.Wrapper wrapper = JsonSerializer.instance().FromJson(str, SubscriptionDetails.SubscriptionDetailSet.Wrapper.class);
                return wrapper.set;
            });
            return request;
        }

        public static HttpRequest<CollectionDetails.CollectionDetailSet> GetCollectionDetails(long collectionId) {
            Optional<String> key = ApiKey();
            if(!key.isPresent()) {
                System.out.println("No API key provided");
                return null;
            }
            HttpRequest<CollectionDetails.CollectionDetailSet> request = new HttpRequest<>();
            request.setHref(Bindings.GET_COLLECTION_DETAILS);
            request.SetMethod(HttpMethod.POST);
            request.SetParam("key", key.get());
            request.SetParam("collectioncount", 1);
            request.SetParam("format", "json");
            request.SetParam("publishedfileids[0]", String.valueOf(collectionId));
            request.SetResponseMapper(str -> {
                CollectionDetails.CollectionDetailSet.Wrapper wrapper = JsonSerializer.instance().FromJson(str, CollectionDetails.CollectionDetailSet.Wrapper.class);
                return wrapper.set;
            });
            return request;
        }

        public static HttpRequest<Map> GetSingleItem(Subscription sub) {
            Optional<String> key = ApiKey();
            if(!key.isPresent()) {
                System.out.println("No API key provided");
                return null;
            }
            HttpRequest<Map> request = new HttpRequest<>();
            request.setHref(SteamApi.Bindings.GET_PUBLISHED_FILE_DETAILS);
            request.SetMethod(HttpMethod.POST);
            request.SetParam("key", key.get());
            request.SetParam("itemcount", 1);
            request.SetParam("format", "json");
            request.SetParam("publishedfileids[0]", sub.getId());
            request.SetResponseMapper(str -> JsonSerializer.instance().FromJson(str, Map.class));
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
        try{
            LoadProperties();
        }catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void LoadProperties() throws IOException {
        GetProperties().load(new FileInputStream("steam.props"));
    }
    public static void SetProperty(String key, String value) {
        SetProperty(key, value, true);
    }
    public static void SetProperty(String key, String value, boolean save) {
        GetProperties().setProperty(key, value);
        if(!save) return;
        try {
            GetProperties().store(new FileOutputStream("steam.props"), "Steam API authentication");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static Properties GetProperties() {
        if (properties == null)
            properties = new Properties();
        return properties;
    }

    public static final String API_KEY_PROP_NAME = "ApiKey";
    public static final String STEAM_ID_PROP_NAME = "SteamId";

    public static Optional<String> ApiKey() {
        final String defaultValue = "No Key";
        String key = GetProperties().getProperty(API_KEY_PROP_NAME, defaultValue);
        if(key.equalsIgnoreCase(defaultValue))
            return Optional.empty();
        return Optional.of(key);
    }

    public static Optional<String> SteamID() {
        final String defaultValue = "No Key";
        String key = GetProperties().getProperty(STEAM_ID_PROP_NAME, defaultValue);
        if(key.equalsIgnoreCase(defaultValue))
            return Optional.empty();
        return Optional.of(key);
    }

}

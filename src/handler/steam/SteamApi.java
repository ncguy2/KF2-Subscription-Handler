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
import java.io.IOException;
import java.util.Map;
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
            HttpRequest<SubscriptionDetails.SubscriptionDetailSet> request = new HttpRequest<>();
            request.setHref(Bindings.GET_PUBLISHED_FILE_DETAILS);
            request.SetMethod(HttpMethod.POST);
            request.SetParam("key", ApiKey());
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
            HttpRequest<CollectionDetails.CollectionDetailSet> request = new HttpRequest<>();
            request.setHref(Bindings.GET_COLLECTION_DETAILS);
            request.SetMethod(HttpMethod.POST);
            request.SetParam("key", SteamApi.ApiKey());
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
            HttpRequest<Map> request = new HttpRequest<>();
            request.setHref(SteamApi.Bindings.GET_PUBLISHED_FILE_DETAILS);
            request.SetMethod(HttpMethod.POST);
            request.SetParam("key", SteamApi.ApiKey());
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

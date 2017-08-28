package handler.steam;

import com.sun.javafx.collections.ObservableMapWrapper;
import handler.fx.uifx.FXWindow;
import handler.http.HttpRequest;
import handler.ui.Strings;
import handler.utils.JsonSerializer;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;


public class SteamCache {

    public static String CachePath() {
        return Strings.Mutable.WORKING_DIRECTORY + Strings.CACHE_ROOT_DIRECTORY + File.separator + "cache" + File.separator;
    }

    public static String ImagePath() {
        return CachePath() + File.separator + "images" + File.separator;
    }

    public static String ItemPath() {
        return CachePath() + File.separator + "subscriptions" + File.separator;
    }

    public static void PopulateIndex() {
        Index.remoteImageCache.clear();

        boolean loadImages = true;
        boolean loadItems = true;

        File cacheRoot = new File(CachePath());
        if(!cacheRoot.exists()) {
            cacheRoot.mkdirs();
        }

        File imageCache = new File(ImagePath());
        if(!imageCache.exists()) {
            imageCache.mkdirs();
            loadImages = false;
        }

        File itemCache = new File(ItemPath());
        if(!itemCache.exists()) {
            itemCache.mkdirs();
            loadItems = false;
        }

        if(loadImages) {
            File[] files = imageCache.listFiles();
            assert files != null;
            for (File file : files)
                RegisterImage(file);
        }

        if(loadItems) {
            File[] files = itemCache.listFiles();
            assert files != null;
            for (File file : files)
                RegisterItem(file);
        }

    }

    // Registry
    protected static void RegisterImage(File file) {
        Index.remoteImageCache.put(file.getName(), file.getAbsolutePath());
    }
    protected static void RegisterItem(File file) {
        Index.workshopItemCache.put(Long.valueOf(file.getName()), file.getAbsolutePath());
    }

    // Image

    public static void DeleteImage(final String urlPath) {
        String s = Index.remoteImageCache.remove(urlPath);
        if(s == null) {
            System.out.printf("Attempting to delete image from cache, but cannot find image [%s]\n", urlPath);
            return;
        }
        Path path = new File(s).toPath();
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void AddRemoteImage(String urlPath) throws IOException {
        AddRemoteImage_Fallback(urlPath);
    }

    private static void AddRemoteImage_Fallback(String urlPath) throws IOException {
        System.out.println("Using fallback");
        BufferedImage image = ImageIO.read(new URL(urlPath));
        ByteArrayOutputStream s = new ByteArrayOutputStream();
        ImageIO.write(image, Strings.CACHE_IMAGE_FORMAT, s);
        byte[] bytes = s.toByteArray();
        s.close();
        SaveByteArray(urlPath, bytes);
    }

    private static void SaveByteArray(String urlPath, byte[] bytes) throws IOException {
        final String path = FilterImagePath(urlPath);
        System.out.println("Path: "+path);
        File file = new File(ImagePath() + path);
        System.out.println("File path: " + file.getAbsolutePath());
        if(!file.exists()) {
            File parentFile = file.getParentFile();
            if(!parentFile.exists()) parentFile.mkdirs();
            file.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(bytes);
        fos.close();
        RegisterImage(file);
    }

    public static Image GetRemoteImage(String url) {
        final String path = FilterImagePath(url);
        if(Index.remoteImageCache.containsKey(path))
            url = "file:///"+Index.remoteImageCache.get(path);
        else {
            try {
                AddRemoteImage(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new Image(url);
    }

    private static String FilterImagePath(String url) {
        return url.replaceAll("/", ".").replaceAll(":", "_") + "." + Strings.CACHE_IMAGE_FORMAT;
    }

    // Workshop Item

    public static void DeleteItem(long itemId) {
        String s = Index.workshopItemCache.remove(itemId);
        if(s == null) {
            System.out.printf("Attempting to delete item from cache, but cannot find item [%s]\n", String.valueOf(itemId));
            return;
        }
        Path path = new File(s).toPath();
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void GetSubscriptionDetails(long itemId, Consumer<SubscriptionDetails> func) {
        Thread t = new Thread(() -> {
            Optional<SubscriptionDetails> details = GetDetailsFromDisk(itemId);
            if(details.isPresent()) {
                FXWindow.PostStatic(() -> func.accept(details.get()));
                return;
            }
            HttpRequest<SubscriptionDetails.SubscriptionDetailSet> request = SteamApi.Functions.GetSubscriptionSet(itemId);
            request.SetAsync(false);
            Optional<SubscriptionDetails.SubscriptionDetailSet> set = request.Request();
            if(set.isPresent()) {
                set.get().details.stream().findFirst().ifPresent(d -> {
                    try{
                        SaveDetailsToDisk(d);
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                    FXWindow.PostStatic(() -> func.accept(d));
                });
            }else{
                System.out.println("Failed to get subscription details");
            }
        });
        t.setDaemon(true);
        t.start();
    }

    public static void GetSubscriptionSet(Consumer<SubscriptionDetails.SubscriptionDetailSet> func, Runnable onFail, long... itemIds) {
        ItemIdFilter<SubscriptionDetails.SubscriptionDetailSet> filter = GetDetailSetFromDisk(itemIds);
        final SubscriptionDetails.SubscriptionDetailSet set = filter.data;
        ArrayList<Long> remainingIds = filter.remainingIds;
        if(remainingIds.size() <= 0) {
            func.accept(set);
            return;
        }
        long[] longs = new long[remainingIds.size()];
        for (int i = 0; i < remainingIds.size(); i++)
            longs[i] = remainingIds.get(i);
        HttpRequest<SubscriptionDetails.SubscriptionDetailSet> request = SteamApi.Functions.GetSubscriptionSet(longs);
        request.SetAsync(true);
        request.SetOnSuccess(s -> {
            set.Merge(s);
            func.accept(set);
        });
        request.SetOnFail(() -> {
            System.out.println("Unable to get all subscription items,");
            remainingIds.forEach(id -> System.out.printf("\t%s\n", String.valueOf(id)));
            onFail.run();
            func.accept(set);
        });
        request.Request();
    }

    public static void GetSubscriptionSet(Consumer<SubscriptionDetails.SubscriptionDetailSet> func, long... itemIds) {
        GetSubscriptionSet(func, null, itemIds);
    }

    protected static void SaveDetailsToDisk(SubscriptionDetails details) throws IOException {
        long itemId = Long.parseLong(details.publishedfileid);
        String filePath = ItemPath() + details.publishedfileid;
        String json = JsonSerializer.instance().ToJson(details);
        Path path = new File(filePath).toPath();
        if(Files.exists(path))
            Files.delete(path);
        Files.write(path, json.getBytes(), StandardOpenOption.CREATE_NEW, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        Index.workshopItemCache.put(itemId, filePath);
    }

    protected static Optional<SubscriptionDetails> GetDetailsFromDisk(long itemId) {
        if (Index.workshopItemCache.containsKey(itemId)) {
            String filePath = Index.workshopItemCache.get(itemId);
            Path path = new File(filePath).toPath();
            if(!Files.exists(path)) {
                Index.workshopItemCache.remove(itemId);
                return Optional.empty();
            }
            try {
                List<String> strings = Files.readAllLines(path);
                String file = strings.stream().collect(Collectors.joining("\n"));
                SubscriptionDetails subscriptionDetails = JsonSerializer.instance().FromJson(file, SubscriptionDetails.class);
                return Optional.ofNullable(subscriptionDetails);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }

    protected static ItemIdFilter<SubscriptionDetails.SubscriptionDetailSet> GetDetailSetFromDisk(long... itemIds) {
        final SubscriptionDetails.SubscriptionDetailSet set = new SubscriptionDetails.SubscriptionDetailSet();
        ArrayList<Long> remaining = new ArrayList<>();
        for (long itemId : itemIds) {
            Optional<SubscriptionDetails> details = GetDetailsFromDisk(itemId);
            if(details.isPresent())
                set.details.add(details.get());
            else remaining.add(itemId);
        }

        set.result = 1;
        set.resultCount = set.details.size();

        ItemIdFilter<SubscriptionDetails.SubscriptionDetailSet> filter = new ItemIdFilter<>();
        filter.data = set;
        filter.remainingIds = remaining;
        return filter;
    }

    public static SubscriptionDetails.SubscriptionDetailSet GetItemCacheAsSet() {
        Set<Long> keys = Index.workshopItemCache.keySet();
        long[] itemIds = new long[keys.size()];
        int index = 0;
        for (Long key : keys)
            itemIds[index++] = key;

        return GetDetailSetFromDisk(itemIds).data;
    }

    public static ObservableMap<String, String> GetImageCache() {
        return Index.remoteImageCache;
    }

    public static void AddImageListener(MapChangeListener<String, String> listener) {
        Index.remoteImageCache.addListener(listener);
    }
    public static void RemoveImageListener(MapChangeListener<String, String> listener) {
        Index.remoteImageCache.removeListener(listener);
    }

    public static void AddItemListener(MapChangeListener<Long, String> listener) {
        Index.workshopItemCache.addListener(listener);
    }
    public static void RemoveItemListener(MapChangeListener<Long, String> listener) {
        Index.workshopItemCache.removeListener(listener);
    }

    public static class Index {

        public static ObservableMap<String, String> remoteImageCache = new ObservableMapWrapper<>(new HashMap<>());
        public static ObservableMap<Long, String> workshopItemCache = new ObservableMapWrapper<>(new HashMap<>());

    }

    public static class ItemIdFilter<T> {
        public T data;
        public ArrayList<Long> remainingIds;
    }

}

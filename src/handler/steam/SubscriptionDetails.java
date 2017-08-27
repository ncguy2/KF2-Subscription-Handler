package handler.steam;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SubscriptionDetails {

    @SerializedName("publishedfileid")
    public String publishedfileid;

    @SerializedName("result")
    public long result;

    @SerializedName("creator")
    public String creator;

    @SerializedName("creator_app_id")
    public long creatorAppId;

    @SerializedName("consumer_app_id")
    public long consumerAppId;

    @SerializedName("filename")
    public String filename;

    @SerializedName("file_size")
    public long fileSize;

    @SerializedName("file_url")
    public String fileUrl;

    @SerializedName("hcontent_file")
    public String hcontentFile;

    @SerializedName("preview_url")
    public String previewUrl;

    @SerializedName("hcontent_preview")
    public String hcontentPreview;

    @SerializedName("title")
    public String title;

    @SerializedName("description")
    public String description;

    @SerializedName("time_created")
    public long timeCreated;

    @SerializedName("time_updated")
    public long timeUpdated;

    @SerializedName("visibility")
    public long visibility;

    @SerializedName("banned")
    public long banned;

    @SerializedName("ban_reason")
    public String banReason;

    @SerializedName("subscriptions")
    public long subscriptions;

    @SerializedName("favorited")
    public long favorited;

    @SerializedName("lifetime_subscriptions")
    public long lifetimeSubscriptions;

    @SerializedName("lifetime_favorited")
    public long lifetimeFavorited;

    @SerializedName("views")
    public long views;

    @SerializedName("tags")
    public List<Tag> tags = null;

    public String[] TagArray() {
        if(tags.size() == 0) return null;
        String[] arr = new String[tags.size()];
        for (int i = 0; i < tags.size(); i++)
            arr[i] = tags.get(i).tag;
        return arr;
    }

    @Override
    public String toString() {
        return title;
    }

    public static class SubscriptionDetailSet {
        @SerializedName("result")
        public int result;

        @SerializedName("resultcount")
        public int resultCount;

        @SerializedName("publishedfiledetails")
        public List<SubscriptionDetails> details = new ArrayList<>();

        public static class Wrapper {
            @SerializedName("response")
            public SubscriptionDetailSet set;
        }

        public void Merge(SubscriptionDetailSet other) {
            details.addAll(other.details.stream()
                    .filter(d -> this.details.contains(d))
                    .collect(Collectors.toList()));
        }

    }


    public static class Tag {
        @SerializedName("tag")
        public String tag;
    }

}

package handler.steam;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CollectionDetails {

    @SerializedName("publishedfileid")
    protected String fileId;

    @SerializedName("result")
    public int result;

    @SerializedName("children")
    public List<CollectionItem> children;

    public long FileId() {
        return Long.parseLong(fileId);
    }

    public static class CollectionItem {
        @SerializedName("publishedfileid")
        protected String fileId;

        @SerializedName("sortorder")
        public int sortOrder;

        @SerializedName("filetype")
        public int fileType;

        public long FileId() {
            return Long.parseLong(fileId);
        }
    }

    public static class CollectionDetailSet {
        @SerializedName("result")
        public int result;

        @SerializedName("resultcount")
        public int resultCount;

        @SerializedName("collectiondetails")
        public List<CollectionDetails> details;

        public static class Wrapper {
            @SerializedName("response")
            public CollectionDetailSet set;
        }

    }

}

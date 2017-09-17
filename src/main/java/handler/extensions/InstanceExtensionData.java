package handler.extensions;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class InstanceExtensionData {

    @SerializedName("name")         public String name;
    @SerializedName("jar")          public String jarFile;
    @SerializedName("description")  public String desc;
    @SerializedName("author")       public String author;
    @SerializedName("version")      public String version;
    @SerializedName("links")        public List<Link> links;

    @Override
    public String toString() {
        return name;
    }

    public static class Link {
        @SerializedName("title")    public String title;
        @SerializedName("url")      public String url;
    }


}

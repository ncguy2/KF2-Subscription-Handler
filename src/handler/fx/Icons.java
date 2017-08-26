package handler.fx;

// TODO give IN_CLOUD its own icon
public enum Icons {
    DOWNLOADING("downloading.png"),
    DOWNLOAD_AVAILABLE("download_available.png"),
    DOWNLOAD_FINISHED("download_finished.png"),
    IN_CLOUD("download_available.png"),
    ;

    Icons(String path) {
        this.path = path;
    }
    String path;
}

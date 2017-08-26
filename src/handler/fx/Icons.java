package handler.fx;

public enum Icons {
    DOWNLOADING("downloading.png"),
    DOWNLOAD_AVAILABLE("download_available.png"),
    DOWNLOAD_FINISHED("download_finished.png"),
    ;

    Icons(String path) {
        this.path = path;
    }
    String path;
}

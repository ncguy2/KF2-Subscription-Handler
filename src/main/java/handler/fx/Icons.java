package handler.fx;

// TODO give IN_CLOUD its own icon
public enum Icons {
    DOWNLOADING("downloading.png"),
    DOWNLOAD_AVAILABLE("download_available.png"),
    DOWNLOAD_FINISHED("download_finished.png"),
    IN_CLOUD("in_cloud.png"),
    IS_NATIVE("is_native.png"),
    KF2_LOGO("kf2_logo.jpg"),
    ;

    Icons(String path) {
        this.path = path;
    }
    String path;
}

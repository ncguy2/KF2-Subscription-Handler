package handler.core.controllers;

import handler.fx.uifx.components.Toast;
import handler.steam.SteamCache;
import handler.steam.SubscriptionDetails;
import handler.ui.Strings;
import handler.utils.JsonSerializer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class CacheController implements Initializable {

    protected ContextMenu cacheItemContextMenu;
    protected ContextMenu cacheImageContextMenu;

    public CacheController() {}

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        cachelistItems.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> CacheItemSelected());
        cachelistImages.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> CacheImageSelected());

        cacheimgImage.fitWidthProperty().bind(cacheimgAnchorParent.widthProperty());
        cacheimgImage.setManaged(false);

// TODO add cache deletion confirmation dialog (low priority)
        cacheItemContextMenu = new ContextMenu();
        MenuItem itemDelete = new MenuItem("Delete");
        itemDelete.setOnAction(e -> {
            Object selectedItem = cachelistItems.getSelectionModel().getSelectedItem();
            if(selectedItem instanceof SubscriptionDetails) {
                SubscriptionDetails sub = (SubscriptionDetails) selectedItem;
                SteamCache.DeleteItem(Long.parseLong(sub.publishedfileid));
                cachelistItems.getSelectionModel().clearSelection();
                cachelistItems.getItems().remove(sub);
            }
        });
        cacheItemContextMenu.getItems().add(itemDelete);

        cacheImageContextMenu = new ContextMenu();
        MenuItem imageDelete = new MenuItem("Delete");
        imageDelete.setOnAction(e -> {
            Object selectedItem = cachelistImages.getSelectionModel().getSelectedItem();
            if(selectedItem instanceof ImageString) {
                ImageString is = (ImageString) selectedItem;
                SteamCache.DeleteImage(is.string);
                cachelistImages.getSelectionModel().clearSelection();
                cachelistImages.getItems().remove(is);
            }
        });
        cacheImageContextMenu.getItems().add(imageDelete);
        cachelistItems.setContextMenu(cacheItemContextMenu);
        cachelistImages.setContextMenu(cacheImageContextMenu);

        InitializeCacheListeners();

        Strings.Mutable.WORKING_DIRECTORY.AddListener((observable, oldValue, newValue) -> SteamCache.PopulateIndex());
        SteamCache.PopulateIndex();
    }

    public void InitializeCacheListeners() {
        // Item cache listener
        SteamCache.AddItemListener(change -> {
            long changedId = change.getKey();
            System.out.printf("Item change detected for item %s\n", changedId);
            if(change.wasAdded()) {
                SteamCache.GetSubscriptionDetails(changedId, details -> {
                    System.out.println("Adding details for item with ID "+details.publishedfileid);
                    cachelistItems.getItems().add(details);
                });
            }else if(change.wasRemoved()) {
                for (Object o : cachelistItems.getItems()) {
                    if(o instanceof SubscriptionDetails) {
                        SubscriptionDetails o1 = (SubscriptionDetails) o;
                        if(Long.parseLong(o1.publishedfileid) == changedId) {
                            cachelistItems.getItems().remove(o1);
                            return;
                        }
                    }
                }
            }else{
                // TODO check if this is required, and implement if necessary
                System.out.println("Unsupported item cache change detected");
            }
        });

        // Image cache listener
        SteamCache.AddImageListener(change -> {
            String changedId = change.getKey();
            if(change.wasAdded()) {
                cachelistImages.getItems().add(new ImageString(changedId, change.getValueAdded()));
            }else if(change.wasRemoved()) {
                ImageString imageString = new ImageString(changedId, change.getValueRemoved());
                cachelistImages.getItems().remove(imageString);
            }else{
                // TODO check if this is required, and implement if necessary
                System.out.println("Unsupported image cache change detected");
            }
        });
    }

    @FXML
    public ListView cachelistItems;
    @FXML
    public TextArea cachetxtItemJson;

    @FXML
    public ListView cachelistImages;
    @FXML
    public ImageView cacheimgImage;
    @FXML
    public TabPane cacheTabPane;

    private ImageString cacheImgActiveImage = null;

    @FXML
    public AnchorPane cacheimgAnchorParent;

    public static class ImageString {
        public String string;
        public String filePath;
        protected String display;

        public ImageString(String string, String filePath) {
            this.string = string;
            this.filePath = "file:///"+filePath;

            List<String> split = new ArrayList<>();
            Collections.addAll(split, string.split("\\."));
            split = split.stream()
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
            display = split.get(split.size()-2);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ImageString that = (ImageString) o;

            if (string != null ? !string.equals(that.string) : that.string != null) return false;
            return display != null ? display.equals(that.display) : that.display == null;
        }

        @Override
        public int hashCode() {
            int result = string != null ? string.hashCode() : 0;
            result = 31 * result + (display != null ? display.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return display;
        }
    }

    public void CacheItemSelected() {
        Object selected = cachelistItems.getSelectionModel().getSelectedItem();
        if(selected == null) return;
        if(selected instanceof SubscriptionDetails) {
            SubscriptionDetails details = (SubscriptionDetails) selected;
            String s = JsonSerializer.instance().ToJson(details);
            cachetxtItemJson.setText(s);
        }
    }

    public void CacheImageSelected() {
        Object selected = cachelistImages.getSelectionModel().getSelectedItem();
        if(selected == null) return;
        if(selected instanceof ImageString) {
            ImageString s = (ImageString) selected;
            Image image = new Image(s.filePath);
            cacheimgImage.setImage(image);
            cacheimgImage.setEffect(null);
            cacheImgActiveImage = s;
        }
    }

    @FXML
    public void OpenCacheInExplorer(ActionEvent event) {
        String dir = "";
        SingleSelectionModel<Tab> selectionModel = cacheTabPane.getSelectionModel();
        int selectedIndex = selectionModel.getSelectedIndex();
        switch(selectedIndex) {
            case 0: dir = "subscriptions"; break;
            case 1: dir = "images"; break;
        }
        String path = Strings.Accessors.CacheDirectory() + "cache" + File.separator + dir + File.separator;
        File file = new File(path);
        if(file.exists()) {
            try {
                Desktop.getDesktop().open(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            new Toast("Open in explorer", "Cannot find directory", Toast.NotificationType.ERROR).ShowAndDismiss(Duration.seconds(5));
            System.out.println("Directory: "+path);
        }
    }

}

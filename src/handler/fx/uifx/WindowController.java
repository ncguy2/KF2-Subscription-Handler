package handler.fx.uifx;

//
// Author: Nick Guy
// Created at: 25/08/2017
//

import handler.domain.Subscription;
import handler.fx.ThemeManager;
import handler.fx.task.*;
import handler.fx.uifx.components.CycleCell;
import handler.fx.uifx.components.Toast;
import handler.steam.SteamApi;
import handler.steam.SteamCache;
import handler.steam.SubscriptionDetails;
import handler.ui.Strings;
import handler.utils.JsonSerializer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.util.Duration;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class WindowController implements Initializable {

    protected List<Subscription> subscriptions;
    protected List<Subscription> mapCycle;

    public Map<Subscription, TitledPane> removedPanes;
    public final double hiddenOpacity = 0.2;
    public final int hiddenDuration = 150;
    public static final String MenuSeparator = "------------";

    protected ContextMenu filterContextMenu;
    protected ContextMenu cacheItemContextMenu;
    protected ContextMenu cacheImageContextMenu;

    public WindowController() {
        subscriptions = new ArrayList<>();
        mapCycle = new ArrayList<>();
        removedPanes = new HashMap<>();
    }

    public FXWindow context;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Window controller initialized");
        listCycle.setCellFactory(param -> new CycleCell());
        cachelistItems.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> CacheItemSelected());
        cachelistImages.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> CacheImageSelected());

        cacheimgImage.fitWidthProperty().bind(cacheimgAnchorParent.widthProperty());
        cacheimgImage.setManaged(false);

        fieldApiKeyPlain.setManaged(false);
        fieldApiKeyPlain.setVisible(false);
        fieldApiKeyPlain.managedProperty().bind(tglApiKeyVisibility.selectedProperty());
        fieldApiKeyPlain.visibleProperty().bind(tglApiKeyVisibility.selectedProperty());
        fieldApiKey.managedProperty().bind(tglApiKeyVisibility.selectedProperty().not());
        fieldApiKey.visibleProperty().bind(tglApiKeyVisibility.selectedProperty().not());

        fieldApiKeyPlain.textProperty().bindBidirectional(fieldApiKey.textProperty());

        ChangeListener<Boolean> changeListener = (observable, oldValue, newValue) -> {
            if (newValue) return;
            SteamApi.SetProperty("ApiKey", fieldApiKeyPlain.getText());
        };

        fieldApiKeyPlain.focusedProperty().addListener(changeListener);
        fieldApiKey.focusedProperty().addListener(changeListener);

        SteamApi.ApiKey().ifPresent(fieldApiKey::setText);

        fieldSubFilter.textProperty().addListener((observable, oldValue, newValue) -> {
            FilterList(newValue);
            accSubscriptions.layout();
            accSubscriptions.applyCss();
        });

        Map.Entry<String, String>[] premadeFilters = new Map.Entry[]{
                new AbstractMap.SimpleEntry<>("Vanilla", ":native"),
                new AbstractMap.SimpleEntry<>("Not downloaded", ":cloud"),
                new AbstractMap.SimpleEntry<>("Local files", ":local"),
                new AbstractMap.SimpleEntry<>("Subscribed", ":subscribed"),
                new AbstractMap.SimpleEntry<>("Requires update", ":update"),
                new AbstractMap.SimpleEntry<>(MenuSeparator , ""),
                new AbstractMap.SimpleEntry<>("Clear filter", ""),
        };
        filterContextMenu = new ContextMenu();
        for (Map.Entry<String, String> filter : premadeFilters) {

            if(filter.getKey().equalsIgnoreCase(MenuSeparator)) {
                filterContextMenu.getItems().add(new SeparatorMenuItem());
                continue;
            }

            MenuItem item = new MenuItem(filter.getKey());
            item.setOnAction(event -> fieldSubFilter.setText(filter.getValue()));
            filterContextMenu.getItems().add(item);
        }

        fieldSubFilter.setContextMenu(filterContextMenu);

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

        InvalidateThemeList();
        InitializeCacheListeners();
        InitializeNumericFilters();

        ColorAdjust monochrome = new ColorAdjust();
        monochrome.setSaturation(-1);

        Strings.Mutable.WORKING_DIRECTORY.AddListener((observable, oldValue, newValue) -> {
            DisplayNotification("New working directory", newValue, Color.AQUAMARINE);
            SteamCache.PopulateIndex();
            InvalidateThemeList();
            RefreshSubscriptions(null);
            RefreshCycle(null);
        });

        SteamCache.PopulateIndex();
        Platform.runLater(() -> {
            RefreshSubscriptions(null);
            RefreshCycle(null);
        });

    }

    public void InvalidateThemeList() {
        menuThemes.getItems().clear();
        for (ThemeManager.Themes theme : ThemeManager.Themes.values()) {
            MenuItem menuItem = new MenuItem(theme.Title());
            menuItem.setOnAction(e -> ThemeManager.ApplyTheme(context.scene, theme));
            menuThemes.getItems().add(menuItem);
        }

        Optional<Map<String, String>> externalThemes = ThemeManager.FindExternalThemes();

        if(!externalThemes.isPresent()) return;
        if(externalThemes.get().isEmpty()) return;

        menuThemes.getItems().add(new SeparatorMenuItem());

        externalThemes.get().forEach((k, v) -> {
            MenuItem menuItem = new MenuItem(k);
            menuItem.setOnAction(e -> ThemeManager.ApplyTheme(context.scene, v));
            menuThemes.getItems().add(menuItem);
        });
    }

    public void InitializeCacheListeners() {
        // Item cache listener
        SteamCache.AddItemListener(change -> {
            long changedId = change.getKey();
            if(change.wasAdded()) {
                SteamCache.GetSubscriptionDetails(changedId, cachelistItems.getItems()::add);
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
                if(imageString.equals(cacheImgActiveImage))
                    cacheimgImage.setEffect(monochrome);
            }else{
                // TODO check if this is required, and implement if necessary
                System.out.println("Unsupported image cache change detected");
            }
        });
    }

    public void InitializeNumericFilters() {
        FXUtils.AddNumericTextFilter(fieldSubscription);
        FXUtils.AddNumericTextFilter(fieldCollection);
    }

    public void AddSubToCycle(Subscription sub) {
        ObservableList items = listCycle.getItems();
        items.add(sub);
        listCycle.setItems(items);
    }

    public void AddSubscriptionPane(Subscription sub, TitledPane pane) {
        accSubscriptions.getPanes().add(pane);
    }

    public void DisplayNotification(String title, String body, Color colour) {
        new Toast(title, body, null, colour).ShowAndDismiss(Duration.seconds(5));
    }

    public void SetRowVisibility(TitledPane pane, boolean visible) {
        new Timeline(new KeyFrame(Duration.millis(hiddenDuration),
                new KeyValue(pane.opacityProperty(), visible ? 1.0 : hiddenOpacity)
        )).play();
    }

    public void FilterList(String filter) {
        filter = filter.toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
        if(filter.startsWith(":")) {
            if(InternalFilter(filter)) {
                return;
            }
        }

        final String f = filter;

        if(f.isEmpty() || f.startsWith(":"))
            accSubscriptions.getPanes().forEach(pane -> SetRowVisibility(pane, true));
        else accSubscriptions.getPanes().forEach(pane -> {
            boolean s = pane.getText().toLowerCase().replaceAll("[^a-zA-Z0-9]", "").contains(f);
            SetRowVisibility(pane, s);
        });
    }

    protected Optional<Subscription> GetSubFromPane(TitledPane pane) {
        Node content = pane.getContent();
        if(content instanceof DetailController) {
            DetailController controller = (DetailController) content;
            Subscription sub = controller.GetSubscription();
            return Optional.ofNullable(sub);
        }
        return Optional.empty();
    }

    private boolean InternalFilter(final String filter) {
        switch(filter) {
            case ":native": case ":vanilla": case ":default":
                accSubscriptions.getPanes().stream().filter(pane -> GetSubFromPane(pane).isPresent()).forEach(pane -> {
                    Subscription sub = GetSubFromPane(pane).get();
                    SetRowVisibility(pane, sub.isNative());
                });
                return true;
            case ":cloud":
                accSubscriptions.getPanes().stream().filter(pane -> GetSubFromPane(pane).isPresent()).forEach(pane -> {
                    Subscription sub = GetSubFromPane(pane).get();
                    SetRowVisibility(pane, !sub.isOnDisk());
                });
                return true;
            case ":disk": case ":local":
                accSubscriptions.getPanes().stream().filter(pane -> GetSubFromPane(pane).isPresent()).forEach(pane -> {
                    Subscription sub = GetSubFromPane(pane).get();
                    SetRowVisibility(pane, sub.isOnDisk());
                });
                return true;
            case ":subscribed":
                accSubscriptions.getPanes().stream().filter(pane -> GetSubFromPane(pane).isPresent()).forEach(pane -> {
                    Subscription sub = GetSubFromPane(pane).get();
                    SetRowVisibility(pane, !sub.isNative());
                });
                return true;
            case ":update":
                accSubscriptions.getPanes().stream().filter(pane -> GetSubFromPane(pane).isPresent()).forEach(pane -> {
                    Subscription sub = GetSubFromPane(pane).get();
                    SetRowVisibility(pane, sub.NeedsUpdate());
                });
                return true;
        }
        return false;
    }

    @FXML
    public void menuItemResetSplitAction(ActionEvent event) {
        splitLists.setDividerPosition(0, 0.5);
    }

    @FXML
    public void AddSubscription(ActionEvent event) {
        new FXAddSubscription(context).start();
    }

    @FXML
    public void btnAddSelectedClick(ActionEvent event) {
        new FXAddMapToCycle(context).start();
    }

    @FXML
    public void SetDirectory(ActionEvent event) {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle("Select working directory");
        dc.setInitialDirectory(new File(Strings.Mutable.WORKING_DIRECTORY.toString()));
        File directory = dc.showDialog(context.GetStage());
        if(directory != null) {
            Strings.Mutable.WORKING_DIRECTORY.SetValue(directory.getAbsolutePath());
        }else{
            DisplayNotification("No working directory, reverting...", Strings.Mutable.WORKING_DIRECTORY.toString(), Color.INDIANRED);
        }
    }

    @FXML
    public void RefreshSubscriptions(ActionEvent event) {
        new FXFetchSubscription(context).start();
    }

    @FXML public void RefreshCycle(ActionEvent event) {
        new FXFetchMapCycle(context).start();
    }

    @FXML public void SaveCycle(ActionEvent event) {
        new FXSaveMapCycle(context).start();
    }

    @FXML public void SortCycle(ActionEvent event) {
        new FXSortMapCycle(context).start();
    }

    @FXML
    public void RemoveSelectedCycle(ActionEvent event) {
        new FXRemoveMapFromCycle(context).start();
    }

    @FXML
    public void LoadCollection(ActionEvent event) {
        new FXLoadCollection(context).start();
    }

    @FXML
    public void RevertCycleToVanilla(ActionEvent event) {
        new FXRevertMapCycle(context).start();
    }

    @FXML
    public SplitPane splitLists;
    @FXML
    public Accordion accSubscriptions;
    @FXML
    public ListView listCycle;
    @FXML
    public Button btnRefreshSubscriptions;
    @FXML
    public Button btnAddSelected;
    @FXML
    public Button btnRefreshCycle;
    @FXML
    public Button btnRemoveSelected;
    @FXML
    public Button btnSubscribe;
    @FXML
    public TextField fieldSubscription;
    @FXML
    public Button btnSortCycleAlpha;
    @FXML
    public Button btnSaveCycle;
    @FXML
    public TextField fieldCollection;
    @FXML
    public MenuItem menuItemResetSplit;
    @FXML
    public MenuItem menuItemSetDirectory;
    @FXML
    public TextField fieldSubFilter;

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
    public GridPane rootPane;

    @FXML
    public ListView cachelistItems;
    @FXML
    public TextArea cachetxtItemJson;

    @FXML
    public ListView cachelistImages;
    @FXML
    public ImageView cacheimgImage;

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

    @FXML
    public PasswordField fieldApiKey;
    @FXML
    public ToggleButton tglApiKeyVisibility;
    @FXML
    public TextField fieldApiKeyPlain;

    @FXML
    public Menu menuThemes;
    @FXML
    public TabPane cacheTabPane;

    private ColorAdjust monochrome;

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

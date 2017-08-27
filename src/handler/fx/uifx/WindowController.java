package handler.fx.uifx;

//
// Author: Nick Guy
// Created at: 25/08/2017
//

import handler.domain.Subscription;
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
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class WindowController implements Initializable {

    protected List<Subscription> subscriptions;
    protected List<Subscription> mapCycle;

    public Map<Subscription, TitledPane> removedPanes;
    public final double hiddenOpacity = 0.2;
    public final int hiddenDuration = 150;
    public static final String MenuSeparator = "------------";

    protected ContextMenu filterContextMenu;

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

//        fieldSubFilter.setOnContextMenuRequested(event -> {
//            filterContextMenu.show(fieldSubFilter, event.getScreenX(), event.getScreenY());
//            event.consume();
//        });
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
        dc.setInitialDirectory(new File(Strings.Mutable.WORKING_DIRECTORY));
        File directory = dc.showDialog(context.GetStage());
        // TODO add notification to show to user that the working directory has changed
        if(directory != null) {
            Strings.Mutable.WORKING_DIRECTORY = directory.getAbsolutePath();
            SteamCache.PopulateIndex();
            DisplayNotification("New working directory", Strings.Mutable.WORKING_DIRECTORY, Color.AQUAMARINE);
            cachelistItems.getItems().setAll(SteamCache.GetItemCacheAsSet().details);

            HashMap<String, String> imageCache = SteamCache.GetImageCache();
            cachelistImages.getItems().setAll(imageCache.entrySet()
                    .stream()
                    .map(entry -> new ImageString(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList()));
        }else{
            DisplayNotification("No working directory, reverting...", Strings.Mutable.WORKING_DIRECTORY, Color.INDIANRED);
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
        }
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

}

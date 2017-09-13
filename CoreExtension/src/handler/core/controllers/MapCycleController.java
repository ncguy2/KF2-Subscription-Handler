package handler.core.controllers;

import handler.domain.Subscription;
import handler.fx.task.*;
import handler.fx.uifx.DetailController;
import handler.fx.uifx.FXUtils;
import handler.fx.uifx.FXWindow;
import handler.fx.uifx.components.CycleCell;
import handler.ui.Strings;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;

import static handler.fx.uifx.WindowController.MenuSeparator;

public class MapCycleController implements Initializable {

    protected ContextMenu filterContextMenu;
    public final double hiddenOpacity = 0.2;
    public final int hiddenDuration = 150;
    public FXWindow context;

    public MapCycleController() {}

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        context = FXWindow.GetMainContext();

        listCycle.setCellFactory(param -> new CycleCell());

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

        InitializeNumericFilters();


        Strings.Mutable.WORKING_DIRECTORY.AddListener((observable, oldValue, newValue) -> {
//            DisplayNotification("New working directory", newValue, Color.AQUAMARINE);
//            SteamCache.PopulateIndex();
//            InvalidateThemeList();
            RefreshSubscriptions(null);
            RefreshCycle(null);
//            fieldSteamServerDir.setText(newValue);
        });

        Platform.runLater(() -> {
            RefreshSubscriptions(null);
            RefreshCycle(null);
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

    public void SetRowVisibility(TitledPane pane, boolean visible) {
        new Timeline(new KeyFrame(Duration.millis(hiddenDuration),
                new KeyValue(pane.opacityProperty(), visible ? 1.0 : hiddenOpacity)
        )).play();
    }

    public void FilterList(String filter) {
        if(filter.startsWith(":")) {
            if(InternalFilter(filter)) {
                return;
            }
        }

        final String f = filter.toLowerCase().replaceAll("[^a-zA-Z0-9]", "");

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
    public void AddSubscription(ActionEvent event) {
        FXAddSubscription fxAddSubscription = new FXAddSubscription(context, fieldSubscription.getText(), this::AddSubscriptionPane);
        fxAddSubscription.onStart = () -> btnAddSelected.setDisable(true);
        fxAddSubscription.onEnd = () -> {
            btnAddSelected.setDisable(false);
            fieldSubscription.setText("");
        };
        fxAddSubscription.start();
    }

    @FXML
    public void btnAddSelectedClick(ActionEvent event) {
        new FXAddMapToCycle(context, accSubscriptions, this::AddSubToCycle).start();
    }

    @FXML
    public void RefreshSubscriptions(ActionEvent event) {
        FXFetchSubscription fxFetchSubscription = new FXFetchSubscription(context, this::AddSubscriptionPane);
        fxFetchSubscription.onStart = () -> {
            btnSubscribe.setDisable(true);
            btnRefreshSubscriptions.setDisable(true);
            context.Post(() -> accSubscriptions.getPanes().clear());
        };
        fxFetchSubscription.onEnd = () -> {
            btnSubscribe.setDisable(false);
            btnRefreshSubscriptions.setDisable(false);
        };
        fxFetchSubscription.start();
    }

    @FXML public void RefreshCycle(ActionEvent event) {
        FXFetchMapCycle fxFetchMapCycle = new FXFetchMapCycle(context, listCycle.getItems()::add);
        fxFetchMapCycle.onStart = () -> {
            btnRefreshCycle.setDisable(true);
            context.Post(listCycle.getItems()::clear);
        };
        fxFetchMapCycle.onEnd = () -> btnRefreshCycle.setDisable(false);
        fxFetchMapCycle.start();
    }

    @FXML public void SaveCycle(ActionEvent event) {
        FXSaveMapCycle fxSaveMapCycle = new FXSaveMapCycle(context, listCycle::getItems);
        fxSaveMapCycle.onStart = () -> btnSaveCycle.setDisable(true);
        fxSaveMapCycle.onEnd = () -> btnSaveCycle.setDisable(false);
        fxSaveMapCycle.start();
    }

    @FXML public void SortCycle(ActionEvent event) {
        new FXSortMapCycle(context, listCycle.getItems()).start();
    }

    @FXML
    public void RemoveSelectedCycle(ActionEvent event) {
        new FXRemoveMapFromCycle(context, listCycle).start();
    }

    @FXML
    public void LoadCollection(ActionEvent event) {
        FXLoadCollection fxLoadCollection = new FXLoadCollection(context, fieldCollection.getText(), this::AddSubscriptionPane);
        fxLoadCollection.onStart = () -> btnLoadCollection.setDisable(true);
        fxLoadCollection.onEnd = () -> {
            btnLoadCollection.setDisable(false);
            fieldCollection.setText("");
        };
        fxLoadCollection.start();
    }

    @FXML
    public void RevertCycleToVanilla(ActionEvent event) {
        new FXRevertMapCycle(context, listCycle.getItems()::setAll).start();
    }

    @FXML private SplitPane splitLists;
    @FXML private TextField fieldSubFilter;
    @FXML private Accordion accSubscriptions;
    @FXML private ListView listCycle;
    @FXML private Button btnRefreshSubscriptions;
    @FXML private Button btnAddSelected;
    @FXML private Button btnRefreshCycle;
    @FXML private Button btnLoadCollection;
    @FXML private Button btnRemoveSelected;
    @FXML private Button btnSubscribe;
    @FXML private TextField fieldSubscription;
    @FXML private Button btnSortCycleAlpha;
    @FXML private Button btnSaveCycle;
    @FXML private TextField fieldCollection;


}

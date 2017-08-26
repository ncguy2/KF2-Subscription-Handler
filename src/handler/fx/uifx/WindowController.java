package handler.fx.uifx;

//
// Author: Nick Guy
// Created at: 25/08/2017
//

import handler.domain.Subscription;
import handler.fx.task.*;
import handler.fx.uifx.components.CycleCell;
import handler.fx.uifx.components.Toast;
import handler.ui.Strings;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.*;

public class WindowController implements Initializable {

    protected List<Subscription> subscriptions;
    protected List<Subscription> mapCycle;

    public Map<Subscription, TitledPane> removedPanes;

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
            DisplayNotification("New working directory", Strings.Mutable.WORKING_DIRECTORY, Color.AQUAMARINE);
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






}

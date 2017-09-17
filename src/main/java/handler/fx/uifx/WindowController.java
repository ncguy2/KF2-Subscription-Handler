package handler.fx.uifx;

//
// Author: Nick Guy
// Created at: 25/08/2017
//

import handler.domain.Subscription;
import handler.extensions.ExtensionHandler;
import handler.extensions.IExtension;
import handler.fx.ThemeManager;
import handler.fx.uifx.components.Toast;
import handler.ui.Strings;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
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
    public static final String MenuSeparator = "------------";

    public WindowController() {
        subscriptions = new ArrayList<>();
        mapCycle = new ArrayList<>();
        removedPanes = new HashMap<>();
    }

    public FXWindow context;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        context = FXWindow.GetMainContext();

        LoadFragment("/handler/fxml/Extension.fxml", "Instance Extensions", ExtensionUIController.class);
        LoadFragment("/handler/fxml/ActiveExtension.fxml", "Active Extensions", ActiveExtensionUIController.class);

        ExtensionHandler.instance().GetTabs().forEach(this::LoadFragment);
        context.GetStage().sizeToScene();


        ExtensionHandler.instance().GetExtensionMap().addListener((MapChangeListener<? super String, ? super IExtension>) change -> {
            if(change.wasAdded()) {
                IExtension valueAdded = change.getValueAdded();
                if(valueAdded != null)
                    valueAdded.GetTabs().forEach(this::LoadFragment);
            }else if(change.wasRemoved()) {
                // TODO add support for removing extensions
            }
            context.GetStage().sizeToScene();
        });

        System.out.println("Window controller initialized");


        InvalidateThemeList();

        ColorAdjust monochrome = new ColorAdjust();
        monochrome.setSaturation(-1);

        Strings.Mutable.WORKING_DIRECTORY.AddListener((observable, oldValue, newValue) -> {
            DisplayNotification("New working directory", newValue, Color.AQUAMARINE);
            InvalidateThemeList();
        });


        FXWindow.useTabIcons.AddListener((observable, oldValue, newValue) -> UpdateTabIcons(newValue));
        UpdateTabIcons(FXWindow.useTabIcons.GetValue());
    }

    public void UpdateTabIcons(boolean useIcon) {
        tabbedContentPane.getTabs().forEach(tab -> UpdateTabIcon(tab, useIcon));
    }

    protected void UpdateTabIcon(final Tab tab) {
        UpdateTabIcon(tab, FXWindow.useTabIcons.GetValue());
    }

    protected void UpdateTabIcon(final Tab tab, boolean useIcon) {
        Object userData = tab.getUserData();
        if(userData == null) return;
        if(userData instanceof TabInfo) {
            TabInfo tabInfo = (TabInfo) userData;
            if(useIcon && !tabInfo.icon.isEmpty()) {
                Optional<URL> urlOpt = tabInfo.owningExtension.GetResource(tabInfo.icon);
                if(urlOpt.isPresent()) {
                    Image image = new Image(urlOpt.get().toExternalForm(), true);
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(24);
                    imageView.setFitHeight(24);
                    tab.setGraphic(imageView);
                    tab.setText("");
                }else tab.setText(tabInfo.name);
            }else {
                tab.setGraphic(null);
                tab.setText(tabInfo.name);
            }
        }
    }

    public void LoadFragment(final TabInfo info) {
        Map.Entry<Tab, Optional<FXUtils.FXMLFragment<Object>>> entry = LoadFragment(info.fxmlCp, info.name, info.resourceRoot);
        Tab tab = entry.getKey();
        tab.setUserData(info);
        UpdateTabIcon(tab);
        Optional<FXUtils.FXMLFragment<Object>> fragment = entry.getValue();

        fragment.ifPresent(frag -> {
            Node rootNode = frag.rootNode;
            if(rootNode instanceof Parent) {
                ObservableList<String> stylesheets = ((Parent)rootNode).getStylesheets();
                info.owningExtension.GetStylesheet_Impl().ifPresent(stylesheets::add);
            }
        });
        info.owningExtension.GetTabStyle_Impl().ifPresent(style -> {
            if(style.isExternal) {
                tab.getStyleClass().add(style.cls);
                tabbedContentPane.getStylesheets().add(style.style);
            }else{
                String s = tab.getStyle();
                if(!s.isEmpty())
                    s += ";";
                s += style.style;
                tab.setStyle(s);
            }
        });
    }

    public Map.Entry<Tab, Optional<FXUtils.FXMLFragment<Object>>> LoadFragment(final String fxmlCp, final String name, final Class<?> resourceRoot) {
        Tab tab = new Tab(name);
        tab.setTooltip(new Tooltip(name));
        tab.closableProperty().setValue(false);
        tabbedContentPane.getTabs().add(tab);
        Optional<FXUtils.FXMLFragment<Object>> fragment = LoadFragmentIntoTab(fxmlCp, tab, resourceRoot);
        return new AbstractMap.SimpleEntry<>(tab, fragment);
    }

    public Optional<FXUtils.FXMLFragment<Object>> LoadFragmentIntoTab(final String fxmlCp, final Tab tab, final Class<?> resourceRoot) {
        Optional<FXUtils.FXMLFragment<Object>> fragment = FXUtils.LoadFragmentSafe(fxmlCp, resourceRoot);
        fragment.ifPresent(f -> tab.setContent(f.rootNode));
        return fragment;
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

    public void DisplayNotification(String title, String body, Color colour) {
        new Toast(title, body, null, colour).ShowAndDismiss(Duration.seconds(5));
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
    public void ToggleIcons(ActionEvent event) {
        FXWindow.useTabIcons.SetValue(!FXWindow.useTabIcons.GetValue());
    }

    @FXML public TabPane tabbedContentPane;

    @FXML
    public MenuItem menuItemSetDirectory;

    @FXML
    public GridPane rootPane;

    @FXML
    public Menu menuThemes;

    public static class TabInfo {
        public IExtension owningExtension;
        public String name;
        public String fxmlCp;
        public String icon = "";
        public Class<?> resourceRoot;

        public TabInfo(String name, String fxmlCp, IExtension owningExtension) {
            this(name, fxmlCp, owningExtension.getClass(), owningExtension);
        }

        public TabInfo(String name, String fxmlCp, Class<?> resourceRoot, IExtension owningExtension) {
            this.name = name;
            this.fxmlCp = fxmlCp;
            this.resourceRoot = resourceRoot;
            this.owningExtension = owningExtension;
        }

        public TabInfo SetIconPath(String iconPath) {
            this.icon = iconPath;
            return this;
        }

    }

}

package handler.fx.uifx;

//
// Author: Nick Guy
// Created at: 25/08/2017
//

import handler.domain.Subscription;
import handler.fx.IconLoader;
import handler.fx.Icons;
import handler.fx.ThemeManager;
import handler.steam.SteamCache;
import handler.steam.SteamCmdHandler;
import handler.steam.SteamCmdProcessCallback;
import handler.steam.SteamCommand;
import javafx.animation.Animation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class DetailController extends GridPane {

    protected boolean loaded = false;
    protected Subscription sub = null;

    TitledPane titlePane = null;

    public DetailController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/handler/fxml/Detail.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try{
            loader.load();
        }catch (IOException e) {
            throw new RuntimeException(e);
        }

        ThemeManager.ApplyTheme(getScene());
    }

    public Subscription GetSubscription() {
        return sub;
    }

    public void SetTitlePane(TitledPane pane) {
        this.titlePane = pane;
//        ImageView imageView = IconLoader.GetIcon(Icons.DOWNLOAD_AVAILABLE);
//        pane.setGraphic(imageView);
//        pane.setContentDisplay(ContentDisplay.RIGHT);
//        imageView.setX(pane.getWidth() - imageView.getFitWidth());
    }

    public void SetSubscription(Subscription sub) {
        this.sub = sub;
        imageThumb.setImage(IconLoader.LoadIcon(Icons.KF2_LOGO));
        fieldSubId.setText(sub.getId());
        fieldSubName.setText(sub.getName());
//        SetRequiresDownload(sub.NeedsUpdate() || !sub.isOnDisk());
    }

    public void SetHasLoaded(boolean state) {
        loaded = state;
    }

    public void Load() {
        if(loaded) return;
        if(sub == null) return;
        loaded = true;
        SteamCache.GetSubscriptionDetails(Long.parseLong(sub.getId()), details -> {
            if(titlePane != null)
                FXUtils.TextTransition(titlePane, details.title, 2000).ifPresent(Animation::play);
            FXUtils.CreateTransition(imageThumb, SteamCache.GetRemoteImage(details.previewUrl)).play();
            fieldSubName.setText(details.title);
        });
    }

    public void SetRequiresDownload(boolean required) {
        btnDownloadItem.setDisable(!required);
    }

    public boolean RequiresDownload() {
        if(sub.isNative()) return false;
        return sub.NeedsUpdate() || !sub.isOnDisk();
    }

    @FXML
    public void AttemptDownload(ActionEvent event) {
        if(sub.isNative()) return;
        SetRequiresDownload(false);
        SteamCommand cmd = SteamCommand.WorkshopItemCommand();
        SteamCmdProcessCallback callback = new SteamCmdProcessCallback() {
            @Override
            public void OnSteamCMDDownloadSuccess() {
                sub.setNeedsUpdate(false);
                FXWindow.PostStatic(() -> titlePane.setGraphic(null));
            }

            @Override
            public void CoreFunction(String line) {
                super.CoreFunction(line);
                System.out.print(line);
            }

            @Override
            public void OnFinish() {
                SetRequiresDownload(true);
            }
        };
        SteamCmdHandler.ExecuteCommand(cmd, Long.parseLong(sub.getId()), callback);
    }

    @FXML
    private ImageView imageThumb;
    @FXML
    private TextField fieldSubId;
    @FXML
    private TextField fieldSubName;
    @FXML
    private Button btnDownloadItem;

}

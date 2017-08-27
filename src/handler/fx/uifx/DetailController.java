package handler.fx.uifx;

//
// Author: Nick Guy
// Created at: 25/08/2017
//

import handler.domain.Subscription;
import handler.fx.IconLoader;
import handler.fx.Icons;
import handler.steam.SteamCache;
import javafx.animation.Animation;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Detail.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try{
            loader.load();
        }catch (IOException e) {
            throw new RuntimeException(e);
        }

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
    }

    public void SetHasLoaded(boolean state) {
        loaded = state;
    }

    public void Load() {
        if(loaded) return;
        if(sub == null) return;
        loaded = true;
        SteamCache.GetSubscriptionDetails(Long.parseLong(sub.getId()), details -> {
            if(titlePane != null) {
                FXUtils.TextTransition(titlePane, details.title, 2000).ifPresent(Animation::play);
//                titlePane.setText(details.title);
            }
            FXUtils.CreateTransition(imageThumb, SteamCache.GetRemoteImage(details.previewUrl)).play();
            fieldSubName.setText(details.title);
        });
    }

    @FXML
    private ImageView imageThumb;
    @FXML
    private TextField fieldSubId;
    @FXML
    private TextField fieldSubName;

}

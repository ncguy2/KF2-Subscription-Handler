package handler.fx.uifx;

//
// Author: Nick Guy
// Created at: 25/08/2017
//

import handler.domain.Subscription;
import handler.fx.IconLoader;
import handler.fx.Icons;
import handler.http.HttpRequest;
import handler.steam.SteamApi;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
        ImageView imageView = IconLoader.GetIcon(Icons.DOWNLOAD_AVAILABLE);
        pane.setGraphic(imageView);
        pane.setContentDisplay(ContentDisplay.RIGHT);
        imageView.setX(pane.getWidth() - imageView.getFitWidth());
    }

    public void SetSubscription(Subscription sub) {
        this.sub = sub;
        imageThumb.setImage(new Image(getClass().getResourceAsStream("kf2_logo.jpg")));
        fieldSubId.setText(sub.getId());
        fieldSubName.setText(sub.getName());
    }

    public void Load() {
        if(loaded) return;
        if(sub == null) return;
        loaded = true;
        HttpRequest<Map> request = SteamApi.Functions.GetSingleItemAsync(sub);
        request.SetOnSuccess(map -> {
            try{
                Map resp = (Map) map.get("response");
                List pub = (List) resp.get("publishedfiledetails");
                Map o = (Map) pub.get(0);
                String url = o.get("preview_url").toString();
                CreateTransition(imageThumb, new Image(url)).play();
                String name = o.get("title").toString();
                fieldSubName.setText(name);
                Platform.runLater(() -> titlePane.setGraphic(IconLoader.GetIcon(Icons.DOWNLOAD_FINISHED)));
            }catch (NullPointerException npe) {
                request.OnFail();
            }
        });
        request.SetOnFail(() -> {
            Platform.runLater(() -> titlePane.setGraphic(null));
        });
        System.out.println(request.GetParameterString());
        titlePane.setGraphic(IconLoader.GetIcon(Icons.DOWNLOADING));
        request.Request();
    }

    // TODO improve transition animation
    SequentialTransition CreateTransition(final ImageView iv, final Image image) {
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.4), iv);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> iv.setImage(image));

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.4), iv);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        return new SequentialTransition(fadeOut, fadeIn);
    }

    @FXML
    private ImageView imageThumb;
    @FXML
    private TextField fieldSubId;
    @FXML
    private TextField fieldSubName;

}

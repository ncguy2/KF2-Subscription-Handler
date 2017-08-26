package handler.fx.task;

import handler.domain.Subscription;
import handler.files.KF2Files;
import handler.fx.uifx.FXUtils;
import handler.fx.uifx.FXWindow;
import handler.fx.uifx.WindowController;
import handler.fx.uifx.WorkshopDialog;
import handler.http.HttpRequest;
import handler.steam.SteamApi;
import javafx.scene.image.Image;

import java.util.List;
import java.util.Map;

public class FXAddSubscription extends FXBackgroundTask {

    public FXAddSubscription(FXWindow context) {
        super(context);
    }

    @Override
    public void run() {
        controller.btnSubscribe.setDisable(true);
        try {
            QueryWorkshopItem(controller.fieldSubscription.getText());

        }catch (Exception ex) {
            ex.printStackTrace();
        }
        controller.btnSubscribe.setDisable(false);
    }

    protected void QueryWorkshopItem(final String id) {
        HttpRequest<Map> request = SteamApi.Functions.GetSingleItemAsync(new Subscription(id));
        context.Post(() -> {
            WorkshopDialog dialog = new WorkshopDialog();
            dialog.setLabelTitle("Steam workshop item");
            dialog.SetImage(new Image(WindowController.class.getResourceAsStream("kf2_logo.jpg")));
            dialog.SetId(id);
            dialog.SetLoading(true);
            dialog.SetOnConfirm(d -> {
                try {
                    KF2Files.addSubscription(Integer.parseInt(id));
                    new FXFetchSubscription(context).start();
                    controller.fieldSubscription.setText("");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            request.SetOnSuccess(map -> {
                try{
                    Map resp = (Map) map.get("response");
                    List pub = (List) resp.get("publishedfiledetails");
                    Map o = (Map) pub.get(0);
                    String url = o.get("preview_url").toString();
                    FXUtils.CreateTransition(dialog.GetImage(), new Image(url)).play();
                    String name = o.get("title").toString();
                    dialog.SetName(name);
                    dialog.SetLoading(false);
                }catch (NullPointerException npe) {
                    request.OnFail();
                }
            });
            request.SetOnFail(dialog::FailedRequest);

            dialog.show();
        });

        request.Request();
    }
}

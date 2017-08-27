package handler.fx.task;

import handler.files.KF2Files;
import handler.fx.IconLoader;
import handler.fx.Icons;
import handler.fx.uifx.FXUtils;
import handler.fx.uifx.FXWindow;
import handler.fx.uifx.WorkshopDialog;
import handler.steam.SteamCache;
import handler.steam.SubscriptionDetails;

import java.util.function.Consumer;

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
        final ConsumerContainer onSuccessContainer = new ConsumerContainer();
        context.Post(() -> {
            WorkshopDialog dialog = new WorkshopDialog();
            dialog.setLabelTitle("Steam workshop item");
            dialog.SetImage(IconLoader.LoadIcon(Icons.KF2_LOGO));
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
            onSuccessContainer.consumer = details -> {
                FXUtils.CreateTransition(dialog.GetImage(), SteamCache.GetRemoteImage(details.previewUrl)).play();
                dialog.SetName(details.title);
                dialog.SetLoading(false);
            };
            dialog.show();
        });

        while(onSuccessContainer.consumer == null) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        SteamCache.GetSubscriptionDetails(Long.parseLong(id), onSuccessContainer.consumer);
//        HttpRequest<Map> request = SteamApi.Functions.GetSingleItemAsync(new Subscription(id));
//        context.Post(() -> {
//            WorkshopDialog dialog = new WorkshopDialog();


//
//            request.SetOnSuccess(map -> {
//                try{
//                    Map resp = (Map) map.get("response");
//                    List pub = (List) resp.get("publishedfiledetails");
//                    Map o = (Map) pub.get(0);
//                    String url = o.get("preview_url").toString();
//                    FXUtils.CreateTransition(dialog.GetImage(), new Image(url)).play();
//                    String name = o.get("title").toString();
//                    dialog.SetName(name);
//                    dialog.SetLoading(false);
//                }catch (NullPointerException npe) {
//                    request.OnFail();
//                }
//            });
//            request.SetOnFail(dialog::FailedRequest);
//
//            dialog.show();
//        });
//
//        request.Request();
    }

    public static class ConsumerContainer {
        Consumer<SubscriptionDetails> consumer = null;
    }

}

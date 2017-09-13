package handler.fx.task;

import handler.domain.Subscription;
import handler.files.KF2Files;
import handler.fx.IconLoader;
import handler.fx.Icons;
import handler.fx.uifx.FXUtils;
import handler.fx.uifx.FXWindow;
import handler.fx.uifx.WorkshopDialog;
import handler.steam.SteamCache;
import handler.steam.SubscriptionDetails;
import handler.ui.Strings;
import javafx.scene.control.TitledPane;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class FXAddSubscription extends FXBackgroundTask {

    private final String query;
    private final BiConsumer<Subscription, TitledPane> addSubPane;

    public FXAddSubscription(FXWindow context, String query, BiConsumer<Subscription, TitledPane> addSubPane) {
        super(context);
        this.query = query;
        this.addSubPane = addSubPane;
    }

    @Override
    public void run() {
        QueryWorkshopItem(query);
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
                    new FXFetchSubscription(context, addSubPane).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            onSuccessContainer.consumer = details -> {
                FXUtils.CreateTransition(dialog.GetImage(), SteamCache.GetRemoteImage(details.previewUrl)).play();
                dialog.SetName(details.title);
                dialog.SetLoading(false);

                long consumerAppId = details.consumerAppId;
                if(consumerAppId != Strings.KF2AppId)
                    dialog.SetWarning("Item not for KF2", true);

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

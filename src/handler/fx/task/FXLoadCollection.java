package handler.fx.task;

import handler.domain.Subscription;
import handler.files.KF2Files;
import handler.fx.uifx.CollectionDialog;
import handler.fx.uifx.FXWindow;
import handler.http.HttpRequest;
import handler.steam.CollectionDetails;
import handler.steam.SteamApi;
import handler.steam.SteamCache;
import handler.steam.SubscriptionDetails;
import javafx.scene.control.TitledPane;

import java.util.List;
import java.util.function.BiConsumer;

public class FXLoadCollection extends FXBackgroundTask {

    private final String query;
    private final BiConsumer<Subscription, TitledPane> addSubPane;

    public FXLoadCollection(FXWindow context, String query, BiConsumer<Subscription, TitledPane> addSubPane) {
        super(context);
        this.query = query;
        this.addSubPane = addSubPane;
    }

    @Override
    public void run() {
        final String text = query;

        long l = Long.parseLong(text);
        HttpRequest<CollectionDetails.CollectionDetailSet> request = SteamApi.Functions.GetCollectionDetails(l);
        context.Post(() -> {
            CollectionDialog dialog = new CollectionDialog(context);
            dialog.Clean();
            dialog.SetCollectionId(text);
            dialog.SetLoaderState(true);
            dialog.SetOnConfirm(d -> {
                try{
                    List<SubscriptionDetails> confirmedSubs = d.GetConfirmedSubs();
                    confirmedSubs.forEach(sub -> {
                        try {
                            KF2Files.addSubscription(Integer.parseInt(sub.publishedfileid));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    new FXFetchSubscription(context, addSubPane).start();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            });

            request.SetOnSuccess(set -> {
                // TODO handle error properly
                try{
                    CollectionDetails details = set.details.get(0);
                    long[] arr = new long[details.children.size()];
                    for (int i = 0; i < details.children.size(); i++)
                        arr[i] = details.children.get(i).FileId();

                    SteamCache.GetSubscriptionSet(s -> {
                        dialog.SetListCollectionItems(s);
                        dialog.SetLoaderState(false);
                    }, dialog::Failed, arr);
                }catch (NullPointerException npe) {
                    request.OnFail();
                }

//                HttpRequest<SubscriptionDetails.SubscriptionDetailSet> req2 = SteamApi.Functions.GetSubscriptionSet(arr);
//                req2.SetOnSuccess(subSet -> {
//                    dialog.SetListCollectionItems(subSet);
//                    dialog.SetLoaderState(false);
//                });
//                req2.SetOnFail(dialog::Failed);
//                req2.Request();
            });
            request.SetOnFail(dialog::Failed);

            dialog.show();
        });
        request.Request();
    }
}

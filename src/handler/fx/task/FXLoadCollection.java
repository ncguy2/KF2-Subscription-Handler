package handler.fx.task;

import handler.files.KF2Files;
import handler.fx.uifx.CollectionDialog;
import handler.fx.uifx.FXWindow;
import handler.http.HttpRequest;
import handler.steam.CollectionDetails;
import handler.steam.SteamApi;
import handler.steam.SteamCache;
import handler.steam.SubscriptionDetails;

import java.util.List;

public class FXLoadCollection extends FXBackgroundTask {

    public FXLoadCollection(FXWindow context) {
        super(context);
    }

    @Override
    public void run() {
        long l = Long.parseLong(controller.fieldCollection.getText());
        HttpRequest<CollectionDetails.CollectionDetailSet> request = SteamApi.Functions.GetCollectionDetails(l);
        context.Post(() -> {
            CollectionDialog dialog = new CollectionDialog(context);
            dialog.Clean();
            dialog.SetCollectionId(controller.fieldCollection.getText());
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
                    new FXFetchSubscription(context).start();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            });

            request.SetOnSuccess(set -> {
                CollectionDetails details = set.details.get(0);
                long[] arr = new long[details.children.size()];
                for (int i = 0; i < details.children.size(); i++)
                    arr[i] = details.children.get(i).FileId();

                SteamCache.GetSubscriptionSet(s -> {
                    dialog.SetListCollectionItems(s);
                    dialog.SetLoaderState(false);
                }, dialog::Failed, arr);

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

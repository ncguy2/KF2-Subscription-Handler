package handler.fx.task;

import handler.files.KF2Files;
import handler.fx.uifx.FXWindow;
import javafx.scene.control.TextField;

public class FXAddSubscription extends FXBackgroundTask {

    public FXAddSubscription(FXWindow context) {
        super(context);
    }

    @Override
    public void run() {
        controller.btnSubscribe.setDisable(true);
        TextField subscription = controller.fieldSubscription;
        try {
            KF2Files.addSubscription(Integer.parseInt(subscription.getText()));
            new FXFetchSubscription(context).start();
            subscription.setText("");
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        controller.btnSubscribe.setDisable(false);
    }
}

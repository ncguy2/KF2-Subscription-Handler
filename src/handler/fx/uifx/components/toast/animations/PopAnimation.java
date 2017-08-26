package handler.fx.uifx.components.toast.animations;

import handler.fx.uifx.components.toast.AnimationProvider;
import handler.fx.uifx.components.toast.JTrayAnimation;
import handler.fx.uifx.components.toast.ToastStage;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class PopAnimation implements JTrayAnimation {

    private final Timeline showAnimation, dismissAnimation;
    private final SequentialTransition sq;
    private final ToastStage stage;
    private boolean trayShowing;

    public PopAnimation(ToastStage stage) {
        this.stage = stage;
        showAnimation = SetupShowAnimation();
        dismissAnimation = SetupDismissAnimation();
        sq = new SequentialTransition(SetupShowAnimation(), SetupDismissAnimation());
    }

    private Timeline SetupShowAnimation() {
        Timeline t1 = new Timeline();

        KeyValue kv1 = new KeyValue(stage.yLocationProperty(), stage.bottomRight.getY() + stage.getWidth());
        KeyFrame kf1 = new KeyFrame(Duration.ZERO, kv1);

        KeyValue kv2 = new KeyValue(stage.yLocationProperty(), stage.bottomRight.getY());
        KeyFrame kf2 = new KeyFrame(Duration.millis(1000), kv2);

        KeyValue kv3 = new KeyValue(stage.opacityProperty(), 0.0);
        KeyFrame kf3 = new KeyFrame(Duration.ZERO, kv3);

        KeyValue kv4 = new KeyValue(stage.opacityProperty(), 1.0);
        KeyFrame kf4 = new KeyFrame(Duration.millis(2000), kv4);

        t1.getKeyFrames().addAll(kf1, kf2, kf3, kf4);
        t1.setOnFinished(e -> trayShowing = true);

        return t1;
    }

    private Timeline SetupDismissAnimation() {
        Timeline t1 = new Timeline();

        KeyValue kv1 = new KeyValue(stage.yLocationProperty(), stage.getY() + stage.getWidth());
        KeyFrame kf1 = new KeyFrame(Duration.millis(2000), kv1);

        KeyValue kv2 = new KeyValue(stage.opacityProperty(), 0.0);
        KeyFrame kf2 = new KeyFrame(Duration.millis(2000), kv2);

        t1.getKeyFrames().addAll(kf1, kf2);

        t1.setOnFinished(e -> {
            trayShowing = false;
            stage.close();
            stage.SetLocation(stage.bottomRight);
        });

        return t1;
    }

    @Override
    public AnimationProvider.AnimationType GetAnimationType() {
        return AnimationProvider.AnimationType.POPUP;
    }

    @Override
    public void PlaySequential(Duration dismissDelay) {
        sq.getChildren().get(1).setDelay(dismissDelay);
        sq.play();
    }

    @Override
    public void PlayShowAnimation() {
        showAnimation.play();
    }

    @Override
    public void PlayDismissAnimation() {
        dismissAnimation.play();
    }

    @Override
    public boolean IsShowing() {
        return trayShowing;
    }
}

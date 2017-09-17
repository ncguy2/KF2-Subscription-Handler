package handler.fx.uifx.components.toast.animations;

import handler.fx.uifx.components.toast.AnimationProvider;
import handler.fx.uifx.components.toast.JTrayAnimation;
import handler.fx.uifx.components.toast.ToastStage;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class FadeAnimation implements JTrayAnimation {

    private final Timeline showAnimation, dismissAnimation;
    private final SequentialTransition sq;
    private final ToastStage stage;
    private boolean trayShowing;

    public FadeAnimation(ToastStage stage) {
        this.stage = stage;
        showAnimation = SetupShowAnimation();
        dismissAnimation = SetupDismissAnimation();
        sq = new SequentialTransition(SetupShowAnimation(), SetupDismissAnimation());
    }

    private Timeline SetupShowAnimation() {
        Timeline t1 = new Timeline();

        KeyValue kvOpacity0 = new KeyValue(stage.opacityProperty(), 0.0);
        KeyFrame frame1 = new KeyFrame(Duration.ZERO, kvOpacity0);

        KeyValue kvOpacity1 = new KeyValue(stage.opacityProperty(), 1.0);
        KeyFrame frame2 = new KeyFrame(Duration.millis(400), kvOpacity1);

        t1.getKeyFrames().addAll(frame1, frame2);
        t1.setOnFinished(e -> trayShowing = true);

        return t1;
    }

    private Timeline SetupDismissAnimation() {
        Timeline t1 = new Timeline();

        KeyValue kv1 = new KeyValue(stage.opacityProperty(), 0.0);
        KeyFrame kf1 = new KeyFrame(Duration.millis(1000), kv1);

        t1.getKeyFrames().addAll(kf1);

        t1.setOnFinished(e -> {
            trayShowing = false;
            stage.close();
            stage.SetLocation(stage.bottomRight);
        });

        return t1;
    }

    @Override
    public AnimationProvider.AnimationType GetAnimationType() {
        return AnimationProvider.AnimationType.FADE;
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

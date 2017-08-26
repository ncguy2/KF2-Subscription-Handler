package handler.fx.uifx.components.toast.animations;

import handler.fx.uifx.components.toast.AnimationProvider;
import handler.fx.uifx.components.toast.JTrayAnimation;
import handler.fx.uifx.components.toast.ToastStage;
import javafx.animation.*;
import javafx.util.Duration;

public class SlideAnimation implements JTrayAnimation {

    private final Timeline showAnimation, dismissAnimation;
    private final SequentialTransition sq;
    private final ToastStage stage;
    private boolean trayShowing;

    public SlideAnimation(ToastStage stage) {
        this.stage = stage;
        showAnimation = SetupShowAnimation();
        dismissAnimation = SetupDismissAnimation();
        sq = new SequentialTransition(SetupShowAnimation(), SetupDismissAnimation());
    }

    private Timeline SetupShowAnimation() {
        Timeline t1 = new Timeline();

        double offscreenX = stage.GetOffScreenBounds().getX();

        KeyValue kvX = new KeyValue(stage.xLocationProperty(), offscreenX);
        KeyFrame kf1 = new KeyFrame(Duration.ZERO, kvX);

        Interpolator interpolator = Interpolator.TANGENT(Duration.millis(300), 50);
        KeyValue kvInter = new KeyValue(stage.xLocationProperty(), stage.bottomRight.getX(), interpolator);
        KeyFrame kf2 = new KeyFrame(Duration.millis(1300), kvInter);

        KeyValue kvO0 = new KeyValue(stage.opacityProperty(), 0.0);
        KeyFrame kf3 = new KeyFrame(Duration.ZERO, kvO0);

        KeyValue kvO1 = new KeyValue(stage.opacityProperty(), 1.0);
        KeyFrame kf4 = new KeyFrame(Duration.millis(1000), kvO1);

        t1.getKeyFrames().addAll(kf1, kf2, kf3, kf4);
        t1.setOnFinished(e -> trayShowing = true);

        return t1;
    }

    private Timeline SetupDismissAnimation() {
        Timeline t1 = new Timeline();


        double offScreenX = stage.GetOffScreenBounds().getX();
        Interpolator interpolator = Interpolator.TANGENT(Duration.millis(300), 50);
        double trayPadding = 3;

        KeyValue kvX = new KeyValue(stage.xLocationProperty(), offScreenX + trayPadding, interpolator);
        KeyFrame kf1 = new KeyFrame(Duration.millis(1400), kvX);

        KeyValue kvOpacity = new KeyValue(stage.opacityProperty(), 0.4);
        KeyFrame kf2 = new KeyFrame(Duration.millis(2000), kvOpacity);

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
        return AnimationProvider.AnimationType.SLIDE;
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

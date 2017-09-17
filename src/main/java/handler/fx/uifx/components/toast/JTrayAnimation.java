package handler.fx.uifx.components.toast;

import javafx.util.Duration;

public interface JTrayAnimation {

    AnimationProvider.AnimationType GetAnimationType();
    void PlaySequential(Duration dismissDelay);
    void PlayShowAnimation();
    void PlayDismissAnimation();
    boolean IsShowing();

}

package handler.fx.uifx.components.toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AnimationProvider {

    private List<JTrayAnimation> animations;

    public AnimationProvider(JTrayAnimation... animations) {
        this.animations = new ArrayList<>();
        Collections.addAll(this.animations, animations);
    }

    public void AddAll(JTrayAnimation... animations) {
        Collections.addAll(this.animations, animations);
    }

    public JTrayAnimation Get(int index) {
        return animations.get(index);
    }

    public JTrayAnimation FindFirstWhere(Predicate<? super JTrayAnimation> predicate) {
        return animations.stream().filter(predicate).findFirst().orElse(null);
    }

    public List<JTrayAnimation> Where(Predicate<? super JTrayAnimation> predicate) {
        return animations.stream().filter(predicate).collect(Collectors.toList());
    }

    public enum AnimationType {
        FADE,
        SLIDE,
        POPUP
    }

}

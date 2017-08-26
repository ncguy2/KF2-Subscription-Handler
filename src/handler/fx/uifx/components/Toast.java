package handler.fx.uifx.components;

import handler.fx.uifx.components.toast.AnimationProvider;
import handler.fx.uifx.components.toast.JTrayAnimation;
import handler.fx.uifx.components.toast.ToastStage;
import handler.fx.uifx.components.toast.animations.FadeAnimation;
import handler.fx.uifx.components.toast.animations.PopAnimation;
import handler.fx.uifx.components.toast.animations.SlideAnimation;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;

public class Toast {

    public Toast(String title, String body, Image image, Paint fill) {
        InitToast(title, body, NotificationType.CUSTOM);

        SetImage(image);
        SetRectangleFill(fill);
    }

    public Toast(String title, String body, NotificationType type) {
        InitToast(title, body, type);
    }

    public Toast() {
        InitToast("", "", NotificationType.CUSTOM);
    }

    private void InitToast(String title, String message, NotificationType type) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Toast.fxml"));
            loader.setController(this);
            loader.load();

            InitStage();
            InitAnimations();

            SetTray(title, message, type);

        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void InitAnimations() {
        animProvider = new AnimationProvider(
                new FadeAnimation(stage),
                new SlideAnimation(stage),
                new PopAnimation(stage));

        animator = animProvider.Get(0);
    }

    private void InitStage() {
        stage = new ToastStage(rootNode, StageStyle.UNDECORATED);
        stage.setScene(new Scene(rootNode));
        stage.setAlwaysOnTop(true);
        stage.SetLocation(stage.bottomRight);
        lblClose.setOnMouseClicked(e -> Dismiss());
    }

    public void SetNotificationType(NotificationType nType) {

        type = nType;

        URL imageLocation = null;
        String paintHex = null;

        switch (nType) {

            case INFORMATION:
                imageLocation = getClass().getResource("/tray/resources/info.png");
                paintHex = "#2C54AB";
                break;

            case NOTICE:
                imageLocation = getClass().getResource("/tray/resources/notice.png");
                paintHex = "#8D9695";
                break;

            case SUCCESS:
                imageLocation = getClass().getResource("/tray/resources/success.png");
                paintHex = "#009961";
                break;

            case WARNING:
                imageLocation = getClass().getResource("/tray/resources/warning.png");
                paintHex = "#E23E0A";
                break;

            case ERROR:
                imageLocation = getClass().getResource("/tray/resources/error.png");
                paintHex = "#CC0033";
                break;

            case CUSTOM:
                return;
        }

        SetRectangleFill(Paint.valueOf(paintHex));
        SetImage(new Image(imageLocation.toString()));
        SetTrayIcon(imageIcon.getImage());
    }

    public NotificationType GetNotificationType() {
        return type;
    }

    public void SetTray(String title, String message, NotificationType type) {
        SetTitle(title);
        SetMessage(message);
        SetNotificationType(type);
    }

    public void SetTray(String title, String message, Image img, Paint rectangleFill, AnimationProvider.AnimationType animType) {
        SetTitle(title);
        SetMessage(message);
        SetImage(img);
        SetRectangleFill(rectangleFill);
        SetAnimationType(animType);
    }

    public boolean IsTrayShowing() {
        return animator.IsShowing();
    }

    public void ShowAndDismiss(Duration dismissDelay) {

        if (IsTrayShowing()) {
            Dismiss();
        } else {
            stage.show();

            OnShown();
            animator.PlaySequential(dismissDelay);
        }

        OnDismissed();
    }

    public void ShowAndWait() {
        if(!IsTrayShowing()) {
            stage.show();
            animator.PlayShowAnimation();
            OnShown();
        }
    }

    public void Dismiss() {
        if(IsTrayShowing()) {
            animator.PlayDismissAnimation();
            OnDismissed();
        }
    }

    protected void OnShown() {}
    protected void OnDismissed() {}

    public void SetTrayIcon(Image img) {
        if(img == null) return;
        stage.getIcons().clear();
        stage.getIcons().add(img);
    }
    public Image GetTrayIcon() {
        return stage.getIcons().get(0);
    }

    public void SetTitle(String title) {
        lblTitle.setText(title);
    }
    public String GetTitle() {
        return lblTitle.getText();
    }

    public void SetMessage(String text) {
        lblMessage.setText(text);
    }
    public String GetMessage() {
        return lblMessage.getText();
    }

    public void SetImage(Image img) {
        imageIcon.setImage(img);
        SetTrayIcon(img);
    }
    public Image GetImage() {
        return imageIcon.getImage();
    }

    public void SetRectangleFill(Paint value) {
        rectangleColor.setFill(value);
    }
    public Paint GetRectangleFill() {
        return rectangleColor.getFill();
    }

    public void SetAnimationType(AnimationProvider.AnimationType type) {
        animator = animProvider.FindFirstWhere(a -> a.GetAnimationType() == type);
        this.animType = animType;
    }

    public AnimationProvider.AnimationType GetAnimationType() {
        return animType;
    }

    public enum NotificationType {
        INFORMATION,
        NOTICE,
        SUCCESS,
        WARNING,
        ERROR,
        CUSTOM
    }

    @FXML
    private Label lblTitle, lblMessage, lblClose;
    @FXML
    private ImageView imageIcon;
    @FXML
    private Rectangle rectangleColor;
    @FXML
    private AnchorPane rootNode;

    ToastStage stage;
    NotificationType type;
    AnimationProvider.AnimationType animType;
    AnimationProvider animProvider;
    JTrayAnimation animator;

}

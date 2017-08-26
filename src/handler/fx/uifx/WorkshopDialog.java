package handler.fx.uifx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Dialog;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.function.Consumer;

public class WorkshopDialog extends Dialog<Boolean> {

    public WorkshopDialog() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("WorkshopConfirm.fxml"));
        loader.setController(this);

        try{
            loader.load();
        }catch (IOException e) {
            throw new RuntimeException(e);
        }

        getDialogPane().setContent(rootPane);
        setOnCloseRequest(e -> CloseDialog());
    }

    public void setLabelTitle(String title) {
        labelTitle.setText(title);
    }
    public void SetId(String id) {
        fieldId.setText(id);
    }
    public void SetName(String name) {
        fieldName.setText(name);
    }
    public void SetLoading(boolean visible) {
        loaderWaiting.setVisible(visible);
    }
    public void SetImage(Image image) {
        imgThumb.setImage(image);
    }

    public void FailedRequest() {
        SetLoading(false);
        setLabelTitle("Failed to load item from steam workshop");
    }

    public Consumer<WorkshopDialog> GetOnConfirm() {
        return onConfirm;
    }

    public void SetOnConfirm(Consumer<WorkshopDialog> onConfirm) {
        this.onConfirm = onConfirm;
    }

    public ImageView GetImage() {
        return imgThumb;
    }

    @FXML
    public void Confirm(ActionEvent event) {
        if(onConfirm != null) onConfirm.accept(this);
        resultProperty().setValue(true);
        CloseDialog();
    }

    @FXML
    public void Cancel(ActionEvent event) {
        resultProperty().setValue(false);
        CloseDialog();
    }

    public void CloseDialog() {
        System.out.println("Cancelled");
        hide();
    }

    @FXML
    private Text labelTitle;
    @FXML
    private TextField fieldId;
    @FXML
    private TextField fieldName;
    @FXML
    private ProgressIndicator loaderWaiting;
    @FXML
    private ImageView imgThumb;
    @FXML
    private GridPane rootPane;

    protected Consumer<WorkshopDialog> onConfirm;
}

package handler.fx.uifx;

import handler.fx.IconLoader;
import handler.fx.Icons;
import handler.steam.SubscriptionDetails;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

;

public class CollectionDialog extends Dialog<Boolean> {

    private Consumer<CollectionDialog> onConfirm;

    public CollectionDialog() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("CollectionConfirm.fxml"));
        loader.setController(this);

        try{
            loader.load();
        }catch (IOException ioe) {
            ioe.printStackTrace();
        }

        getDialogPane().setContent(rootPane);
        setOnCloseRequest(e -> CloseDialog(false));

        MultipleSelectionModel selectionModel = listCollectionItems.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);
    }

    public void SetImage(Image image) {
        imgCollectionSelected.setImage(image);
    }
    public void SetTitle(String title) {
        titleCollectionSelected.setText(title);
    }
    public void SetDescription(String description) {
        descCollectionSelected.setText(description);
    }
    public void SetTimeCreated(long time) {
        fieldCreated.setText(FormatTime(time));
    }
    public void SetTimeUpdated(long time) {
        fieldUpdated.setText(FormatTime(time));
    }
    public void SetTags(String... tags) {
        listTags.getItems().clear();
        if(tags != null && tags.length > 0)
            listTags.getItems().addAll(tags);
    }

    public List<SubscriptionDetails> GetConfirmedSubs() {
        List<SubscriptionDetails> subs = new ArrayList<>();
        for (Object o : listCollectionItems.getItems()) {
            if(!(o instanceof SubscriptionDetails)) continue;
            SubscriptionDetails d = (SubscriptionDetails) o;
            if(IsConfirmed(d)) subs.add(d);
        }
        return subs;
    }

    public boolean IsConfirmed(SubscriptionDetails sub) {
        // TODO implement
        return sub != null;
    }

    public void SetCollectionId(String collectionId) {
        fieldCollectionId.setText(collectionId);
    }

    public void SetListCollectionItems(SubscriptionDetails.SubscriptionDetailSet set) {
        listCollectionItems.getItems().clear();
        listCollectionItems.getItems().addAll(set.details);
    }

    public void SetLoaderState(boolean visible) {
        loaderCollection.setVisible(visible);
    }

    public Consumer<CollectionDialog> GetOnConfirm() {
        return onConfirm;
    }

    public void SetOnConfirm(Consumer<CollectionDialog> onConfirm) {
        this.onConfirm = onConfirm;
    }

    protected String FormatTime(long time) {
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy, HH:mm");
        Date t = new Date(time);
        return sdfDate.format(t);
    }

    public void Failed() {
        Clean();
        SetTitle("Unable to get collection details");
        btnConfirm.setDisable(true);
    }

    public void CloseDialog(boolean res) {
        resultProperty().setValue(res);
        hide();
    }

    @FXML
    public void SelectElement(MouseEvent event) {
        Object o = listCollectionItems.getSelectionModel().getSelectedItem();
        if(o instanceof SubscriptionDetails) {
            SubscriptionDetails d = (SubscriptionDetails) o;
            SetImage(new Image(d.previewUrl));
            SetTitle(d.title);
            SetDescription(d.description);
            SetTimeCreated(d.timeCreated);
            SetTimeUpdated(d.timeUpdated);
            SetTags(d.TagArray());
        }
    }

    @FXML
    public void ConfirmCollection(ActionEvent event) {
        // TODO confirm collection
    }
    @FXML
    public void CancelCollection(ActionEvent event) {
        // TODO confirm collection
    }

    @FXML
    private GridPane rootPane;
    @FXML
    private Button btnConfirm;
    @FXML
    private Button btnCancel;
    @FXML
    private ListView listCollectionItems;
    @FXML
    private ImageView imgCollectionSelected;
    @FXML
    private Text titleCollectionSelected;
    @FXML
    private TextArea descCollectionSelected;
    @FXML
    private TextField fieldCreated;
    @FXML
    private TextField fieldUpdated;
    @FXML
    private ListView listTags;
    @FXML
    private TextField fieldCollectionId;
    @FXML
    private ProgressIndicator loaderCollection;

    public void Clean() {
        SetImage(IconLoader.LoadIcon(Icons.KF2_LOGO));
        SetTitle("");
        SetDescription("");
        fieldCreated.setText("");
        fieldUpdated.setText("");
        SetTags((String[]) null);
        SetLoaderState(false);
        btnConfirm.setDisable(false);
        btnCancel.setDisable(false);
    }
}

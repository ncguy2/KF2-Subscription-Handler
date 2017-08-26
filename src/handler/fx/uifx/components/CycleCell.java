package handler.fx.uifx.components;

import handler.domain.Subscription;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;

import java.util.ArrayList;

public class CycleCell extends ListCell<Subscription> {

    public static DataFormat subscriptionDataFormat = new DataFormat(Subscription.class.getCanonicalName());

    public CycleCell() {
        super();
        setOnDragDetected(this::OnDragDetected);
        setOnDragOver(this::OnDragOver);
        setOnDragEntered(this::OnDragEntered);
        setOnDragExited(this::OnDragExited);
        setOnDragDropped(this::OnDragDropped);
        setOnDragDone(DragEvent::consume);
    }

    protected void OnDragDetected(MouseEvent event) {
        Subscription item = getItem();
        if(item == null) return;
        ObservableList<Subscription> items = getListView().getItems();
        Dragboard dragboard = startDragAndDrop(TransferMode.MOVE);
        ClipboardContent content = new ClipboardContent();
        content.put(subscriptionDataFormat, item);

        WritableImage image = new WritableImage((int)this.getWidth(), (int)this.getHeight());
        this.snapshot(null, image);
        dragboard.setDragView(image);
        dragboard.setContent(content);
        event.consume();
    }

    protected void OnDragOver(DragEvent event) {
        if(event.getGestureSource() != this && event.getDragboard().hasContent(subscriptionDataFormat)) {
            event.acceptTransferModes(TransferMode.MOVE);
        }
        event.consume();
    }

    protected void OnDragEntered(DragEvent event) {
        if(event.getGestureSource() != this && event.getDragboard().hasContent(subscriptionDataFormat))
            setOpacity(0.3);
    }

    protected void OnDragExited(DragEvent event) {
        if(event.getGestureSource() != this && event.getDragboard().hasContent(subscriptionDataFormat))
            setOpacity(1);
    }

    protected void OnDragDropped(DragEvent event) {
        if(getItem() == null) return;
        Dragboard db = event.getDragboard();
        boolean success = false;
        if(db.hasContent(subscriptionDataFormat)) {

            ObservableList<Subscription> items = getListView().getItems();
            boolean shiftOthers = true;
            int thisIdx = items.indexOf(getItem());
            Subscription content = (Subscription) db.getContent(subscriptionDataFormat);

            if(shiftOthers) {
                items.remove(content);
                items.add(thisIdx, content);
            }else{
                int draggedIdx = items.indexOf(content);
                items.set(draggedIdx, getItem());
                items.set(thisIdx, content);
            }

            ArrayList<Subscription> itemsCopy = new ArrayList<>(getListView().getItems());
            getListView().getItems().setAll(itemsCopy);
            success = true;

        }
        event.setDropCompleted(success);
        event.consume();
    }

    @Override
    protected void updateItem(Subscription item, boolean empty) {
        super.updateItem(item, empty);
        if(empty || item == null) {
            setText("");
        }else{
            setText(item.toString());
        }
    }
}

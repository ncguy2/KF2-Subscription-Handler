package handler.fx.uifx;

import com.sun.javafx.collections.ObservableMapWrapper;
import handler.extensions.ExtensionHandler;
import handler.extensions.IExtension;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class ActiveExtensionUIController implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listExtensions.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> Select(newValue));
        ObservableMapWrapper<String, IExtension> map = ExtensionHandler.instance().GetExtensionMap();
        map.values().forEach(ext -> {
            listExtensions.getItems().add(ext);
        });
        map.addListener((MapChangeListener<? super String, ? super IExtension>) change -> {
            if(change.wasAdded()) {
                IExtension valueAdded = change.getValueAdded();
                if(valueAdded != null)
                    listExtensions.getItems().add(valueAdded);
            }else if(change.wasRemoved()) {
                // TODO support removing extensions while running
            }
        });
    }

    public void Select(IExtension data) {
        txtName.setText(data.DisplayName());
        txtId.setText(data.Id());
        txtStylesheet.setText(data.GetStylesheet());
        vboxTabs.getChildren().clear();
        vboxRemarks.getChildren().clear();
        data.GetTabs().forEach(this::AddTabInfo);
        data.Remarks().forEach(this::AddRemark);
    }

    protected void AddTabInfo(WindowController.TabInfo info) {
        vboxTabs.getChildren().add(new Text(info.name));
    }

    protected void AddRemark(String remark) {
        vboxRemarks.getChildren().add(new Text(remark));
    }

    @FXML private ListView<IExtension> listExtensions;
    @FXML private Text txtName;
    @FXML private Text txtId;
    @FXML private Text txtStylesheet;
    @FXML private VBox vboxTabs;
    @FXML private VBox vboxRemarks;


}

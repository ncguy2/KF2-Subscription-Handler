package handler.fx.uifx;

import handler.extensions.ExtensionHandler;
import handler.extensions.InstanceExtensionData;
import handler.ui.Strings;
import handler.utils.FileUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;

public class ExtensionUIController implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listExtensions.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> Select(newValue));
        ExtensionHandler.instance()
                .GetInstanceExtensions()
                .forEach(listExtensions.getItems()::add);
    }

    public void Select(InstanceExtensionData data) {
        txtName.setText(data.name);
        txtDesc.setText(data.desc);
        txtAuthor.setText(data.author);
        txtVersion.setText(data.version);
        vboxLinkContainer.getChildren().clear();
        data.links.forEach(this::AddLink);


        InstanceExtensionData selectedItem = listExtensions.getSelectionModel().getSelectedItem();
        if(selectedItem != null) {
            String srcPath = "Extensions/Instance/"+selectedItem.jarFile;
            String target = Strings.Accessors.InstanceExtensionDirectory() + selectedItem.jarFile;

            boolean jarExists = Files.exists(new File(srcPath).toPath());
            boolean jarActive = Files.exists(new File(target).toPath());
            boolean canAdd = jarExists && !jarActive;
            btnAdd.setDisable(!canAdd);
        }else btnAdd.setDisable(true);
    }

    protected void AddLink(InstanceExtensionData.Link link) {
        Hyperlink hl = new Hyperlink();
        hl.setText(link.title);
        hl.setTooltip(new Tooltip(link.url));
        hl.setOnAction(event -> FXWindow.GetMainContext().getHostServices().showDocument(link.url));
        vboxLinkContainer.getChildren().add(hl);
    }

    @FXML
    public void AddSelected(ActionEvent event) {
        InstanceExtensionData selectedItem = listExtensions.getSelectionModel().getSelectedItem();
        if(selectedItem == null) return;

        String srcPath = "Extensions/Instance/"+selectedItem.jarFile;

        File jar = new File(srcPath);
        if(!jar.exists()) {
            System.err.printf("No file found, %s\n", jar.getAbsolutePath());
            return;
        }

        String target = Strings.Accessors.InstanceExtensionDirectory() + selectedItem.jarFile;

        System.out.println(target);

        try {
            File targetFile = new File(target);
            FileUtils.CopyFileOrFolder(jar, targetFile);
            ExtensionHandler.instance().LoadExtension(targetFile);
            btnAdd.setDisable(true);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    private ListView<InstanceExtensionData> listExtensions;
    @FXML
    private Text txtName;
    @FXML
    private TextArea txtDesc;
    @FXML
    private Text txtAuthor;
    @FXML
    private Text txtVersion;
    @FXML
    private VBox vboxLinkContainer;
    @FXML
    private Button btnAdd;

}

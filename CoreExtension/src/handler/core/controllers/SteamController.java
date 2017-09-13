package handler.core.controllers;

import handler.fx.uifx.FXWindow;
import handler.steam.SteamApi;
import handler.ui.Strings;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class SteamController implements Initializable {

    private FXWindow context;

    public SteamController() {}

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        context = FXWindow.GetMainContext();

        fieldApiKeyPlain.setManaged(false);
        fieldApiKeyPlain.setVisible(false);
        fieldApiKeyPlain.managedProperty().bind(tglApiKeyVisibility.selectedProperty());
        fieldApiKeyPlain.visibleProperty().bind(tglApiKeyVisibility.selectedProperty());
        fieldApiKey.managedProperty().bind(tglApiKeyVisibility.selectedProperty().not());
        fieldApiKey.visibleProperty().bind(tglApiKeyVisibility.selectedProperty().not());

        fieldApiKeyPlain.textProperty().bindBidirectional(fieldApiKey.textProperty());

        ChangeListener<Boolean> changeListener = (observable, oldValue, newValue) -> {
            if (newValue) return;
            SteamApi.SetProperty("ApiKey", fieldApiKeyPlain.getText());
        };

        fieldApiKeyPlain.focusedProperty().addListener(changeListener);
        fieldApiKey.focusedProperty().addListener(changeListener);

        SteamApi.ApiKey().ifPresent(fieldApiKey::setText);

        Strings.Mutable.WORKING_DIRECTORY.AddListener((observable, oldValue, newValue) -> {
            fieldSteamServerDir.setText(newValue);
        });

        Strings.Mutable.STEAMCMD_PATH.AddListener((observable, oldValue, newValue) -> fieldSteamCMDExe.setText(newValue));

        fieldSteamServerDir.setText(Strings.Mutable.WORKING_DIRECTORY.GetValue());
        fieldSteamCMDExe.setText(Strings.Mutable.STEAMCMD_PATH.GetValue());
    }

    @FXML
    private TextField fieldSteamServerDir;
    @FXML
    private TextField fieldSteamCMDExe;
    @FXML
    private PasswordField fieldApiKey;
    @FXML
    private TextField fieldApiKeyPlain;
    @FXML
    private ToggleButton tglApiKeyVisibility;


    @FXML
    public void BrowserForSteamCMD(ActionEvent event) {
        FileChooser fc = new FileChooser();
        String initial = Strings.Mutable.STEAMCMD_PATH.GetValue();
        if(initial.isEmpty()) initial = Strings.Mutable.WORKING_DIRECTORY.GetValue();
        fc.setInitialDirectory(new File(initial));
        fc.setTitle("SteamCMD executable");
        fc.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Executables", "*.exe"));
        File file = fc.showOpenDialog(context.GetStage());
        if(file == null) return;
        Strings.Mutable.STEAMCMD_PATH.SetValue(file.getAbsolutePath());
    }

}

package handler.extension.cd;

import handler.extensions.IExtension;
import handler.fx.uifx.WindowController;

import java.util.List;

public class CDExtension extends IExtension {

    @Override
    public String DisplayName() {
        return "Controlled Difficulty";
    }

    @Override
    public void GetTabs(List<WindowController.TabInfo> tabs) {
        tabs.add(new WindowController.TabInfo("CD", "/handler/extension/cd/fxml/CD.fxml", this).SetIconPath("/handler/extension/cd/icons/CD.png"));
    }

}

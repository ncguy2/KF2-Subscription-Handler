package handler.core;

import handler.extensions.ExtensionStyle;
import handler.extensions.IExtension;
import handler.fx.uifx.WindowController;

import java.util.List;

public class CoreExtension extends IExtension {

    @Override
    public String DisplayName() {
        return "Core";
    }

    @Override
    public void GetTabs(List<WindowController.TabInfo> tabs) {
        tabs.add(new WindowController.TabInfo("Map Cycle",  "/handler/core/MapCycle.fxml", this));
        tabs.add(new WindowController.TabInfo("Local Cache","/handler/core/Cache.fxml",    this));
        tabs.add(new WindowController.TabInfo("Steam",      "/handler/core/Steam.fxml",    this));
    }

    @Override
    public ExtensionStyle GetTabStyle() {
        return new ExtensionStyle("/css/CoreExtensionTabStyle.css", "tab-coreExtension", this.getClass());
    }
}

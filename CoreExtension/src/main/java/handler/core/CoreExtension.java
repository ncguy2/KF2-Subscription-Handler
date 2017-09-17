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
        tabs.add(new WindowController.TabInfo("Map Cycle", "/handler/core/MapCycle.fxml", this).SetIconPath("/core/icons/mapCycle.png"));
        tabs.add(new WindowController.TabInfo("Local Cache", "/handler/core/Cache.fxml",    this).SetIconPath("/core/icons/cache.png"));
        tabs.add(new WindowController.TabInfo("Steam", "/handler/core/Steam.fxml",    this).SetIconPath("/core/icons/steam.png"));
    }

    @Override
    public ExtensionStyle GetTabStyle() {
        return new ExtensionStyle("/css/CoreExtensionTabStyle.css", "tab-coreExtension", this.getClass());
    }

    @Override
    public void Remarks(List<String> remarks) {
        remarks.add("Provides primary functionality for map cycle");
        remarks.add("Should not be removed");
        remarks.add("I like cake");
    }
}

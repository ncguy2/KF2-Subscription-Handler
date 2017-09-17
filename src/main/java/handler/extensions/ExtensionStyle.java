package handler.extensions;

public class ExtensionStyle {

    public String style;
    public String cls;
    public boolean isExternal;

    public ExtensionStyle(String style) {
        this(style, false);
    }

    public ExtensionStyle(String style, boolean isExternal) {
        this.style = style;
        this.isExternal = isExternal;
    }

    public ExtensionStyle(String style, String cls) {
        this(style, true);
        this.cls = cls;
    }

    public ExtensionStyle(String style, String cls, Class<?> resourceRoot) {
        this.isExternal = true;
        this.cls = cls;
        this.style = resourceRoot.getResource(style).toExternalForm();
    }

}

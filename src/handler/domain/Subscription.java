package handler.domain;

import java.io.Serializable;

public class Subscription implements Serializable {
    
    private final String workshopId;
    private String name;
    private boolean onDisk;
    private boolean needsUpdate;

    public Subscription(String workshopId) {
        this.workshopId = workshopId;
        name = "[Unknown Subscription]";
        onDisk = false;
        needsUpdate = false;
    }
    
    public String getId() {
        return workshopId;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    public boolean isOnDisk() { return onDisk; }

    public void setOnDisk(boolean onDisk) { this.onDisk = onDisk; }

    public boolean NeedsUpdate() { return needsUpdate; }

    public void setNeedsUpdate(boolean needsUpdate) { this.needsUpdate = needsUpdate; }

    @Override
    public String toString() {
        return name;
    }
    
    @Override
    public boolean equals(Object object) {
        if(object == null) return false;
        if(!(object instanceof Subscription)) return false;
        Subscription subscription = (Subscription)object;
        return subscription.getId().equals(workshopId);
    }

    
}

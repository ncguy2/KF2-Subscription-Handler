package handler.domain;

public class Subscription {
    
    private final String workshopId;
    private String name;
    
    public Subscription(String workshopId) {
        this.workshopId = workshopId;
        name = "[Unknown Subscription]";
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
    
    @Override
    public String toString() {
        return name;
    }
    
    @Override
    public boolean equals(Object object) {
        Subscription subscription = (Subscription)object;
        return subscription.getId().equals(workshopId);
    }
    
}

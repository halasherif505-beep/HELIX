package Tracking;

import java.time.LocalDateTime;

public class Tracking {

    private String entity_type;
    private Trackable entity;
    private String timestamp;

    // ================== Constructors ==================
    public Tracking(String entity_type, Trackable entity) {
        this.entity_type = entity_type;
        this.entity = entity;
        this.timestamp = LocalDateTime.now().toString();
    }

    // Constructor افتراضي (مهم للتحميل من CSV)
    public Tracking() {
        this.entity_type = "UNKNOWN";
        this.timestamp = LocalDateTime.now().toString();
    }

    // ================== Core Tracking ==================
    public void update_location(String location, String time) {
        if (entity != null && location != null && !location.isBlank()) {
            entity.updateLocation(location);
            this.timestamp = (time != null) ? time : LocalDateTime.now().toString();
        } else {
            System.out.println("[TRACKING ERROR] cannot update location.");
        }
    }

    public String get_status() {
        if (entity == null) return "NO_ENTITY";
        return entity.getStatus();
    }

    public void notify_change() {
        if (entity != null) {
            entity.updateStatus("UPDATED");
            this.timestamp = LocalDateTime.now().toString();
        }
    }

    // ================== Getters ==================
    public Trackable getEntity() {
        return entity;
    }

    public String getEntityType() {
        return entity_type;
    }

    public String getTimestamp() {
        return timestamp;
    }

}

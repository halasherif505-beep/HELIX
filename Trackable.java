package Tracking;
public interface Trackable {
    String getId();
    String getLocation();
    String getStatus();
    void updateLocation(String location);
    void updateStatus(String status);

    }

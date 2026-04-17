package Tracking;
public class ETA {
    private String ambulance_id;
    private int estimated_time;
    private String last_updated;

    public ETA(String ambulance_id) { this.ambulance_id = ambulance_id; }

    public void update_eta(int time, String timestamp) {
        if(time > 0 && timestamp != null) {
            this.estimated_time = time;
            this.last_updated = timestamp;
        }
    }

    public int get_eta() { return estimated_time; }
}


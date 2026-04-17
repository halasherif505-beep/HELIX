package Tracking;

public class Ambulance implements Trackable{
    private String id;
    private String location;
    private int x;
    private int y;
    private String status;

    public Ambulance(String id, String location) {
        this.id = id;
        this.location = location;
        this.x = 0;
        this.y = 0;
        this.status = "AVAILABLE";
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
}


    public void updateLocation(String location) {
        if (location != null && !location.isEmpty())
            this.location = location;
    }

    public void updateStatus(String status) {
        if (status != null && !status.isEmpty())
            this.status = status;
    }

    public void moveToward(int targetX, int targetY) {
        if (x < targetX) x++;
        else if (x > targetX) x--;

        if (y < targetY) y++;
        else if (y > targetY) y--;
    }


    public boolean arrived(int tx, int ty) {
        return x == tx && y == ty;
    }

    public static Ambulance loadFromCSV(String line) {
        String[] data = line.split(",");
        if (data.length >= 2) {
            Ambulance a = new Ambulance(data[0], data[1]);
            if (data.length >= 3) a.updateStatus(data[2]);
            return a;
        }
        return null;
    }

    {
    }
}

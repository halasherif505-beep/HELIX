package Tracking;
import java.io.*;
import java.util.ArrayList;

public class Route {
    private String start_location;
    private String end_location;
    private ArrayList<String> path_coordinates = new ArrayList<>();
    private String traffic_info;

    public Route(String start, String end) {
        this.start_location = start;
        this.end_location = end;
    }

    public void calculate_best_route() {
        path_coordinates.clear();
        path_coordinates.add(start_location);
        path_coordinates.add("Checkpoint");
        path_coordinates.add(end_location);
        traffic_info = "Normal";
    }

    public void update_route(String traffic) { if(traffic != null) this.traffic_info = traffic; }

}
package schedule;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class ShiftSchedule {
    private String StaffID ;
    private LocalDateTime shiftStart;
    private LocalDateTime shiftEnd;
    private ArrayList<String> dayAssigned = new ArrayList<>();

    public ShiftSchedule(String staffID, LocalDateTime shiftStart, LocalDateTime shiftEnd) {
        if(staffID == null || staffID.isBlank()) {
            System.out.println( "ID is requried") ;
            staffID = "00000";
        }
        else {
            this.StaffID = staffID;
        }
        if(shiftStart == null || shiftEnd==null ) {
            System.out.print("INVALID SHIFT TIME");
            return ;
        }
        else if(!shiftEnd.isAfter(shiftStart)) {
            System.out.print("SHIFT END MUST BE AFTER SHIFT START");
        }
        else {
            this.shiftStart = shiftStart;
            this.shiftEnd = shiftEnd;
        }
    }

    public void addDay(String d) {
        dayAssigned.add(d);
        System.out.print("Day '" + d + "' added to shift schedule. ");
    }

    public String getStaffID() {
        return StaffID;
    }


}
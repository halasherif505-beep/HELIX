package Hospital;
import Patient.Patient;
import schedule.ShiftSchedule;
import schedule.Schedule;
import java.util.ArrayList;

public class Staff implements Schedule {
    private String name;
    private String staffID;
    private StaffType stafftype;
    private ShiftSchedule shift;
    private ArrayList<Patient> assignedPatients = new ArrayList<>();


    public Staff(String name, String staffID, StaffType stafftype) {
        if (name == null || name.isBlank()) {
            System.out.println("Name is requried");
            this.name = "incorrect";
        } else {
            this.name = name;
        }

        if (staffID == null || staffID.isBlank()) {
            System.out.println("ID is requried");
            this.staffID = "0000";
        } else {
            this.staffID = staffID;
        }

        this.stafftype = stafftype;
    }


    @Override
    public ShiftSchedule getShift() {
        return this.shift;
    }

    public String getName() {
        return name;
    }

    public String getStaffID() {
        return staffID;
    }

    public StaffType getStafftype() {
        return stafftype;
    }

    public void assignedPatient(Patient p) {
        if (p != null) {
            assignedPatients.add(p);
            System.out.print("Patient '" + p.getName() + "' has been succefully assigned to '" + name + "'.");
        } else {
            System.out.print("CANNOT ASSIGN PATIENT: invalid or missing data. ");
        }
    }


    public void updateSchedule(ShiftSchedule s) {
        if (s != null) {
            this.shift = s;
            System.out.print("Shift Schedule updated successfully for staff ID :" + s.getStaffID() + "\n");
        } else {
            System.out.print("NO SCHEDULE PROVIDED TO UPDATE");
        }
        {
        }
        {
            {
            }
        }
    }
}
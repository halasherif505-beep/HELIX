package Hospital;
import schedule.OperationSchedule;

import java.util.ArrayList;

public class Doctor extends Staff {
    private String specialization ;
    private ArrayList<OperationSchedule> operations = new ArrayList<>();

    public Doctor(String name, String staffID, String specialization) {
        super(name, staffID, StaffType.doctor);
        if(specialization == null || specialization.isBlank()) {
            System.out.print("specialization IS MISSING\n");
            this.specialization = "general" ;
        }
        else {
            this.specialization = specialization;
            System.out.print("Doctor specialization is set to: " + specialization + "\n");
        }
    }

    // FIX: Renamed performSugery to performSurgery
    public void performSurgery(OperationSchedule op) {
        if(op != null) {
            operations.add(op);
            System.out.print("Surgery '" + op.getOperationID() + "' scheduled successfully.\n");
        }
        else
            System.out.print("NO OPERATION PROVIDED. \n");
    }


    public String getSpecialization() {
        return specialization;
    }
}
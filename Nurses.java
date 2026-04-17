package Hospital;
import java.util.ArrayList;
import Patient.Patient;
    public class Nurses extends Staff {
        private ArrayList<String> assignedRooms = new ArrayList<>();

        public Nurses(String name, String staffID) {
            super(name, staffID, StaffType.nurses);
        }
        public void addRoom(String room) {
            if(room == null || room.isBlank()) {
                System.out.print("Invalid room\n");
                return ;
            }
            assignedRooms.add(room);
            // Note: Saving a room string like this is not standard. Assumes SmartHealthCareSystem handles Room objects properly.

            System.out.print("Room '" + room + "' assigned to nurse.\n");
        }
        public void setAssignedRooms(ArrayList<String> assignedRooms) {
            this.assignedRooms = assignedRooms;
        }
        public ArrayList<String> getAssignedRooms() {
            return assignedRooms;
        }
        public void monitorPatient(Patient p) {
            if(p != null) {

                System.out.print("Patient '" + p.getName() + "' is being monitored.\n");
            }
            else {
                System.out.print("NO PATIENT TO MONITOR \n");
            }
        }

        public void updatePatientStatus(String status , Patient p ) {
            // FIX: Changed OR (||) to AND (&&) for correct data validation
            if(p != null && status != null && !status.isBlank()) {
                System.out.print("Patient '" + p.getName() + "' status updated to: " + status + "\n");
            }
            else {
                System.out.print("CAN'T UPDATE PATIENT STATUS: Invalid patient or status data.\n");
            }
        }
    }

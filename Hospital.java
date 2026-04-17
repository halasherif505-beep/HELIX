package Hospital;
import Patient.Patient;
import java.util.ArrayList;
    public class Hospital {

        private String name;
        private String location;
        private ArrayList<Room> rooms;
        private ArrayList<Staff> staff;
        private ArrayList<String> feedbackList;
        private ArrayList<Patient> patients;

        public Hospital(String name, String location) {
            this.name = name;
            this.location = location;
            this.rooms = new ArrayList<>();
            this.staff = new ArrayList<>();
            this.feedbackList = new ArrayList<>();
            this.patients = new ArrayList<>();
        }



        public String getLocation() {
            return location;
        }



        public boolean checkBedAvailability() {
            for (Room room : rooms) {
                if (room.getAvailableBeds() > 0) {
                    return true;
                }
            }
            return false;
        }

        public void assignPatientToRoom(Patient patient) {
            for (Room room : rooms) {
                if (room.getAvailableBeds() > 0) {
                    room.addPatient(patient);
                    assignPatientToRoom(patient);
                    break;
                }
            }

        }

        public void addRoom(Room room) {
            rooms.add(room);
        }

        public void addStaff(Staff member) {
            staff.add(member);
        }

        public void addFeedback(String feedback) {
            feedbackList.add(feedback);
        }

        public ArrayList<Room> getRooms() {
            return rooms;
        }

        public ArrayList<Staff> getStaff() {
            return staff;
        }

        public ArrayList<String> getFeedbackList() {
            return feedbackList;
        }

        public void addPatient(Patient patient) {
            if (patient == null) {
                System.out.println("Cannot add null patient to hospital.");
                return;
            }

            patients.add(patient);

            // Try to assign patient to a room with available beds
            boolean assigned = false;
            for (Room room : rooms) {
                if (room.getAvailableBeds() > 0) {
                    room.addPatient(patient);
                    assigned = true;
                    break;
                }
            }

            if (!assigned) {
                System.out.println("Warning: No available beds for patient " + patient.getName());
            }

            // Record the assignment
            System.out.println("Patient '" + patient.getName() + "' added to hospital: " + this.name);
        }

        public ArrayList<Patient> getPatients() {
            return patients;
        }



        public String getName() {
            return name;
        }
    }


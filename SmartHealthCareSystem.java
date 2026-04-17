package system;
import Hospital.Bed;
import Hospital.Hospital;
import Hospital.Room;
import Hospital.Staff;
import Patient.*;
import schedule.MedicationSchedule;
import schedule.OperationSchedule;
import Tracking.Ambulance;
import Tracking.ETA;
import Tracking.Tracking;
import java.util.ArrayList;

public class SmartHealthCareSystem {

    private ArrayList<Patient> patients_list = new ArrayList<>();
    private ArrayList<Hospital> hospitals_list = new ArrayList<>();
    private ArrayList<Staff> staff_list = new ArrayList<>();
    private ArrayList<Ambulance> ambulances_list = new ArrayList<>();
    private ArrayList<Tracking> trackingList = new ArrayList<>();
    private ArrayList<Bed> beds = new ArrayList<>();
    private ArrayList<MedicationSchedule> medication_List = new ArrayList<>();
    private ArrayList<Appointment> appointment = new ArrayList<>();
    private ArrayList<OperationSchedule> operations = new ArrayList<>();
    private ArrayList<TestResult> results = new ArrayList<>();
    private ArrayList<Notification> notfi = new ArrayList<>();
    private ArrayList<MedicalHistory> history = new ArrayList<>();
    private ArrayList<Room> rooms = new ArrayList<>();
    private ArrayList<ETA> etaList = new ArrayList<>();
    private ArrayList<DailyHabits> dailyHabitsList = new ArrayList<>();


    // ======================================================
    // ADD FUNCTIONS
    // ======================================================

    public void addRoom(Room r) {
        if (r != null) rooms.add(r);
    }

    public void addHospital(Hospital h) {
        if (h != null) hospitals_list.add(h);
    }

    public void addPatient(Patient p) {
        if (p != null && !patientExists(p.getNationalId())) patients_list.add(p);
    }

    private boolean patientExists(String nid) {
        for (Patient p : patients_list)
            if (p.getNationalId().equals(nid)) return true;
        return false;
    }

    public void addStaff(Staff s) {
        if (s != null && !staffExists(s.getStaffID())) staff_list.add(s);
    }

    private boolean staffExists(String id) {
        for (Staff s : staff_list)
            if (s.getStaffID().equals(id)) return true;
        return false;
    }

    public void addAmbulance(Ambulance a) {
        if (a != null && !ambulanceExists(a.getId())) {
            ambulances_list.add(a);
            trackingList.add(new Tracking("Ambulance", a));
        }
    }

    private boolean ambulanceExists(String id) {
        for (Ambulance a : ambulances_list)
            if (a.getId().equals(id)) return true;
        return false;
    }

    public void addBed(Bed b) {
        if (b != null) beds.add(b);
    }

    public void addETA(ETA e) {
        if (e != null) etaList.add(e);
    }

    public void addAppointment(Appointment ap) {
        if (ap != null) appointment.add(ap);
    }

    public void addOperation(OperationSchedule op) {
        if (op != null) operations.add(op);
    }

    public void addMedicationSchedule(MedicationSchedule ms) {
        if (ms != null) medication_List.add(ms);
    }

    public void addTestResult(TestResult tr) {
        if (tr != null) results.add(tr);
    }

    public void addNotification(Notification n) {
        if (n != null) notfi.add(n);
    }

    public Patient findPatientById(String id) {
        for (Patient p : patients_list) {
            if (p.getNationalId().equals(id)) {
                return p;
            }
        }
        return null;
    }

    // ======================================================
    // GETTERS
    // ======================================================
    public ArrayList<Room> getRooms() {
        return rooms;
    }

    public ArrayList<Bed> getBeds() {
        return beds;
    }

    public ArrayList<Ambulance> getAmbulances() {
        return ambulances_list;
    }

    public ArrayList<Patient> getPatients() {
        return patients_list;
    }

    public ArrayList<Staff> getStaff() {
        return staff_list;
    }

    public ArrayList<Hospital> getHospitals() {
        return hospitals_list;
    }

    public ArrayList<Patient> getPatients_list() {
        return patients_list;
    }

    public ArrayList<Hospital> getHospitals_list() {
        return hospitals_list;
    }

    public ArrayList<Staff> getStaff_list() {
        return staff_list;
    }

    public ArrayList<Ambulance> getAmbulances_list() {
        return ambulances_list;
    }

    public ArrayList<Tracking> getTrackingList() {
        return trackingList;
    }

    public ArrayList<MedicationSchedule> getMedication_List() {
        return medication_List;
    }

    public ArrayList<Appointment> getAppointment() {
        return appointment;
    }

    public ArrayList<OperationSchedule> getOperations() {
        return operations;
    }

    public ArrayList<TestResult> getResults() {
        return results;
    }

    public ArrayList<Notification> getNotfi() {
        return notfi;
    }

    public ArrayList<MedicalHistory> getHistory() {
        return history;
    }

    public ArrayList<DailyHabits> getDailyHabitsList() {
        return dailyHabitsList;
    }

    public ArrayList<ETA> getEtaList() {
        return etaList;
    }
}
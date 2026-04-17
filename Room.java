package Hospital;
import Patient.Patient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Room {
    private int roomNumber;
    private int totalBeds;
    private int availableBeds;
    private ArrayList<Patient> patients;

    public Room(int roomNumber, int totalBeds) {
        if (totalBeds <= 0) throw new IllegalArgumentException("totalBeds must be > 0");
        this.roomNumber = roomNumber;
        this.totalBeds = totalBeds;
        this.availableBeds = totalBeds;
        this.patients = new ArrayList<>();
    }

    // add a patient if there is an available bed
    public boolean addPatient(Patient patient) {
        if (patient == null) return false;
        if (availableBeds <= 0) return false;
        patients.add(patient);
        availableBeds--;
        return true;
    }

    // remove a patient
    public boolean removePatient(Patient patient) {
        if (patient == null) return false;
        boolean removed = patients.remove(patient);
        if (removed) availableBeds++;
        return removed;
    }

    public int getRoomNumber() { return roomNumber; }
    public int getTotalBeds() { return totalBeds; }
    public int getAvailableBeds() { return availableBeds; }
    public ArrayList<Patient> getPatientList() { return new ArrayList<>(patients); }




}
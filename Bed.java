package Hospital;
import Patient.Patient;

public class Bed {
    private int bedNumber;
    private boolean occupied;
    private Patient patient;

    public Bed(int bedNumber) {
        this.bedNumber = bedNumber;
        this.occupied = false;
        this.patient = null;
    }


    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }


    public void setPatient(Patient patient) {
        this.patient = patient;
    }


    public void assignPatient(Patient patient) {
        if (!occupied && patient != null) {
            this.patient = patient;
            this.occupied = true;
        }
    }

    public void releaseBed() {
        if (occupied) {
            this.occupied = true ;
        }
        this.patient = null;
        this.occupied = false;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public Patient getPatient() {
        return patient;
    }

    public int getBedNumber() {
        return bedNumber;
    }

}
package schedule;
import Hospital.Doctor;
import Hospital.Nurses ;
import Patient.Patient;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class OperationSchedule {
    private String operationID ;
    private Patient patient ;
    private Doctor doctor ;
    private String room ;
    private LocalDateTime operationTime ;
    private ArrayList<Nurses>nurses = new ArrayList<>() ;


    public OperationSchedule( Patient patient, Doctor doctor, LocalDateTime operationTime, ArrayList<Nurses> nurses
            , String operationID , String room) {
        if(patient == null || doctor == null || operationTime == null) {
            System.out.print("ERROR!! MISSING OPERATION DATE");
        }
        this.operationID = operationID ;
        this.patient = patient;
        this.doctor = doctor;
        this.nurses = nurses;
        this.operationTime = operationTime ;
        this.room = room ;
        System.out.print("Operation created for patient: " + patient.getName() + " at " + operationTime+ "\n" );
    }


    public void addNurse(Nurses n) {
        System.out.print("Nurse added to operation schedule. ");
    }

    public void scheduleOperation() {
        System.out.print("Operation scheduled succefully for patient: " +patient.getName()+"\n");
    }

    public void updateOperation(LocalDateTime newTime) {
        if(newTime != null) {
            this.operationTime = newTime ;
        }
        else {
            System.out.print("TIME INVALID");
        }
    }

    public void cancelOperation() {
        System.out.print("Operation: " + operationID + "has been cancelled");
    }

    public String getOperationID() {
        return operationID;
    }

}
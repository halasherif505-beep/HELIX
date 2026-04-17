package schedule;

import java.util.ArrayList;
import Patient.Patient;
import Patient.DailyHabits;
import Patient.MedicalHistory;

import java.util.ArrayList;

public class MedicationSchedule {

    private ArrayList<String> medicationList;
    private ArrayList<String> dosageTime;
    private ArrayList<Integer> quantityLeft;
    private ArrayList<String> reminders;
    private static int medicationAdherenceScore = 0;

    private Patient patient;

    public MedicationSchedule(Patient patient) {
        if (patient == null) {
            System.out.println(" No patient provided. A placeholder patient record has been created.");
            this.patient = new Patient("Unknown", 0, "Unknown", new MedicalHistory(), new DailyHabits());
        } else {
            this.patient = patient;
            System.out.println(" Patient '" + patient.getName() + "' registered for medication tracking.");
        }
        this.medicationList = new ArrayList<>();
        this.dosageTime = new ArrayList<>();
        this.quantityLeft = new ArrayList<>();
        this.reminders = new ArrayList<>();
        this.medicationAdherenceScore++;
    }

    public ArrayList<String> getMedicationList() {
        return medicationList;
    }

    public void addMedication(String name, String time, int quantity, String reminder) {
        if (name == null || name.isEmpty()) return;
        if (time == null || time.isEmpty()) return;
        if (quantity < 0) return;

        medicationList.add(name);
        dosageTime.add(time);
        quantityLeft.add(quantity);
        reminders.add(reminder);
    }

    public void updateDosage(String medicationName, String newTime) {
        int index = medicationList.indexOf(medicationName);
        if (index != -1) {
            dosageTime.set(index, newTime);
        }
    }

    public void notifyMedication(String medicationName) {
        int index = medicationList.indexOf(medicationName);
        if (index != -1) {
            medicationAdherenceScore++;
            int q = quantityLeft.get(index);
            if (q > 0) {
                quantityLeft.set(index, q - 1);
            }
        }
    }

    public int getMedicationAdherenceScore() {
        return medicationAdherenceScore;
    }

    public void setMedicationAdherenceScore(int score) {
        this.medicationAdherenceScore = score;
    }
}
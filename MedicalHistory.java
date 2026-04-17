package Patient;
import java.util.ArrayList;
import java.util.List;
public class MedicalHistory implements Printable {
    private List<String> chronicDiseases;
    private List<String> allergies;
    private List<String> pastSurgeries;
    private List<String> familyHistory;

    public MedicalHistory() {
        chronicDiseases = new ArrayList<>();
        allergies = new ArrayList<>();
        pastSurgeries = new ArrayList<>();
        familyHistory = new ArrayList<>();
    }

    public void addDisease(String disease) {
        if (disease != null && !disease.isEmpty()) chronicDiseases.add(disease);
    }

    public void addAllergy(String allergy) {
        if (allergy != null && !allergy.isEmpty()) allergies.add(allergy);
    }

    public void addSurgery(String surgery) {
        if (surgery != null && !surgery.isEmpty()) pastSurgeries.add(surgery);
    }

    public void addFamilyHistory(String fh) {
        if (fh != null && !fh.isEmpty()) familyHistory.add(fh);
    }

    @Override
    public void printInfo() {
        System.out.println("Medical History → Chronic Diseases: " +
                (chronicDiseases.isEmpty() ? "None" : chronicDiseases));
        System.out.println("Allergies: " + (allergies.isEmpty() ? "None" : allergies));
        System.out.println("Past Surgeries: " + (pastSurgeries.isEmpty() ? "None" : pastSurgeries));
        System.out.println("Family History: " + (familyHistory.isEmpty() ? "None" : familyHistory));
    }
}

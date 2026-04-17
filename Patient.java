package Patient;
import Hospital.Billing;

import java.util.ArrayList;
import java.util.List;
public class Patient implements Notifiable, Printable {

    private String name;
    private int age;
    private String nationalId;
    private MedicalHistory medicalHistory;
    private DailyHabits dailyHabits;
    private int Id;
    private List<TestResult> testResults;
    private List<Notification> notifications;
    private Billing billing;

    // Constructor
    public Patient(String name, int age, String nationalId,
                   MedicalHistory medicalHistory, DailyHabits dailyHabits) {
        this.name = (name != null && !name.isEmpty()) ? name : "Unknown";
        this.age = (age >= 0) ? age : 0;
        this.nationalId = (nationalId != null && !nationalId.isEmpty()) ? nationalId : "Unknown";
        this.medicalHistory = medicalHistory;
        this.dailyHabits = dailyHabits;
        this.testResults = new ArrayList<>();
        this.notifications = new ArrayList<>();
        this.billing = new Billing(this);

    }

    // Update personal information
    public void updatePersonalInfo(String name, int age, String nationalId) {
        this.name = (name != null && !name.isEmpty()) ? name : this.name;
        this.age = (age >= 0) ? age : this.age;
        this.nationalId = (nationalId != null && !nationalId.isEmpty()) ? nationalId : this.nationalId;
    }

    // View medical history
    public MedicalHistory viewMedicalHistory() {
        return medicalHistory;
    }

    // Update daily habits
    public void updateDailyHabits(DailyHabits habits) {
        if (habits != null) this.dailyHabits = habits;
    }

    // Manage TestResults
    public void addTestResult(TestResult result) {
        if (result != null) testResults.add(result);
    }

    public List<TestResult> getTestResults() {
        return testResults;
    }

    // Print all test results for the patient
    public void printAllResults() {
        if (testResults.isEmpty()) {
            System.out.println("❌ No test results available for this patient.");
        } else {
            for (TestResult t : testResults) {
                t.printInfo();
            }
        }
    }

    // Manage notifications
    public void addNotification(Notification notification) {
        if (notification != null) notifications.add(notification);
    }

    public void sendNotification(String message, String type) {
        if (message != null && !message.isEmpty()) {
            Notification notification = new Notification(this, message, type, java.time.LocalDateTime.now().toString());
            notification.sendNotification();
        }
    }


    @Override
    public void receiveNotification(Notification notification) {
        if (notification != null) notifications.add(notification);
    }

    // Printable interface implementation
    @Override
    public void printInfo() {
        System.out.println("Patient ➤ Name: " + name + ", Age: " + age + ", National ID: " + nationalId);
        if (medicalHistory != null) medicalHistory.printInfo();
        if (dailyHabits != null) dailyHabits.printInfo();
        for (TestResult t : testResults) t.printInfo();
        for (Notification n : notifications) n.printInfo();
    }

    // Getters & Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public Billing getBilling() {
        return billing;
    }
}
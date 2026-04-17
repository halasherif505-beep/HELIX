package Patient;
public class TestResult implements Printable {
    private String testName;
    private String date;
    private String result;
    private String patientId;

    public TestResult(String testName, String date, String result, String patientId) {
        this.testName = (testName != null && !testName.isEmpty()) ? testName : "Unknown";
        this.date = (date != null && !date.isEmpty()) ? date : "Unknown";
        this.result = (result != null && !result.isEmpty()) ? result : "Pending";
        this.patientId = (patientId != null && !patientId.isEmpty()) ? patientId : "Unknown";
    }

    public void addTest(String testName, String date, String result) {
        this.testName = (testName != null && !testName.isEmpty()) ? testName : this.testName;
        this.date = (date != null && !date.isEmpty()) ? date : this.date;
        this.result = (result != null && !result.isEmpty()) ? result : this.result;
    }

    public void updateTestResult(String newResult) {
        if (newResult != null && !newResult.isEmpty()) this.result = newResult;
    }

    public String getTestName() {
        return testName;
    }

    public String getDate() {
        return date;
    }

    public String getResult() {
        return result;
    }

    public String getPatientId() {
        return patientId;
    }

    @Override
    public void printInfo() {
        System.out.println("Test Result → Name: " + testName +
                ", Date: " + date +
                ", Result: " + result +
                ", Patient ID: " + patientId);
    }
}

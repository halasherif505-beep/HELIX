package system;
import Database.*;
import Tracking.Ambulance;
import org.sqlite.core.DB;
import Tracking.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter DTF =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private static Connection connection;

    private static HospitalModuleDAO hospitalDAO;
    private static PatientModuleDAO patientDAO;
    private static StaffModuleDAO staffDAO;
    private static MedicalModuleDAO medicalDAO;
    private static AppointmentModuleDAO appointmentDAO;
    private static BillingModuleDAO billingDAO;
    private static TrackingModuleDAO trackingDAO;

    // =========================
    // Nurse Actions
    // =========================
    private static void nurseActions() throws SQLException {

        System.out.println("\n--- Nurse Actions (Database Mode) ---");

        System.out.print("Enter Nurse ID: ");
        String nurseId = scanner.nextLine();

        System.out.print("Enter Patient ID: ");
        String patientId = scanner.nextLine();

        System.out.print("Enter new patient status: ");
        String status = scanner.nextLine();

        patientDAO.loadPatientMonitoring(patientId);

        System.out.println("Patient status updated: " + status);
    }

    // =========================
    // MAIN
    // =========================
    public static void main(String[] args) {
        HelixGUI.main(args);

        try {
            connection = DBConnection.getConnection();
            String DB_PATH = "C:/Users/1/IdeaProjects/Helix/helix.db";

            if (connection == null) {
                System.out.println("❌ Database connection failed");
                return;
            }

            System.out.println("✅ Connected to Database");

            hospitalDAO = new HospitalModuleDAO(connection);
            patientDAO = new PatientModuleDAO(connection);
            staffDAO = new StaffModuleDAO(connection);
            medicalDAO = new MedicalModuleDAO(connection);
            appointmentDAO = new AppointmentModuleDAO(connection);
            billingDAO = new BillingModuleDAO(connection);
            trackingDAO = new TrackingModuleDAO(connection);

            patientDAO.createTables();
            staffDAO.createTables();
            medicalDAO.createTables();
            appointmentDAO.createTables();
            billingDAO.createTables();
            trackingDAO.createTables();
            hospitalDAO.createTables();
            hospitalDAO.seedRoomsAndBedsIfEmpty();
            patientDAO.savePatient("P1", "Ahmed", 22);
            billingDAO.createBill("P1", 500.0, "UNPAID");
            billingDAO.createBill("P1", 200.0, "PAID");
            hospitalDAO.insertHospital("Main Hospital", "Cairo");

            hospitalDAO.checkAvailableBeds();


            hospitalDAO.checkAvailableBeds();

            hospitalDAO.scheduleOperation(
                    "P1",
                    "Heart Surgery",
                    "2025-01-20"
            );

            hospitalDAO.loadRooms();
            hospitalDAO.loadBeds();
            hospitalDAO.loadOperations();
            hospitalDAO.loadRoomAssignments();

            patientDAO.loadPatient("P1");
            patientDAO.loadNotifications("P1");

            medicalDAO.loadTestResults("P1");
            medicalDAO.loadMedicationSchedule("P1");
            medicalDAO.loadDailyHabits("P1");
            medicalDAO.loadMedicalHistory("P1");

            staffDAO.loadAllStaff();
            staffDAO.loadDoctors();
            staffDAO.loadNurses();
            staffDAO.loadStaffShifts();

            appointmentDAO.loadAppointments();
            billingDAO.loadBills();

            trackingDAO.loadAmbulances();
            trackingDAO.loadAmbulanceETA();
            trackingDAO.loadTrackingLog();

            boolean running = true;

            while (running) {

                printMenu();
                String choice = scanner.nextLine().trim();

                try {
                    switch (choice) {

                        case "1":
                            registerHospital();
                            break;
                        case "2":
                            registerDoctor();
                            break;
                        case "3":
                            registerNurse();
                            break;
                        case "4":
                            registerPatient();
                            break;
                        case "5":
                            scheduleStaffShift();
                            break;
                        case "6":
                            scheduleOperation();
                            break;
                        case "7":
                            medicationSchedule();
                            break;
                        case "8":
                            ambulanceRegisterOrTrack();
                            break;
                        case "9":
                            systemReports(
                                    hospitalDAO,
                                    patientDAO,
                                    appointmentDAO,
                                    billingDAO,
                                    trackingDAO
                            );
                            break;
                        case "10":
                            recordBedAssignment();
                            break;
                        case "11":
                            recordRoomManagement();
                            break;
                        case "12":
                            addNotification();
                            break;
                        case "13":
                            addTestResult();
                            break;
                        case "14":
                            scheduleAppointment();
                            break;
                        case "15":
                            updateAmbulanceETA();
                            break;
                        case "16":
                            createPatientBilling();
                            break;

                        case "17":
                            saveAndExit();
                            running = false;
                            break;

                        default:
                            System.out.println("❌ Invalid choice");
                    }

                } catch (SQLException e) {
                    System.out.println("❌ Database error: " + e.getMessage());
                }
            }

            scanner.close();
            connection.close();

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private static void printHeader() {
        System.out.println("╔════════════════════════════════════════════════════╗");
        System.out.println("║        HELIX SMART HEALTHCARE SYSTEM               ║");
        System.out.println("║        Your Bridge To Every Medical Need           ║");
        System.out.println("╚════════════════════════════════════════════════════╝");
    }

    private static void printMenu() {
        System.out.println("\nChoose an action:");
        System.out.println("1  - Register Hospital");
        System.out.println("2  - Register Doctor");
        System.out.println("3  - Register Nurse");
        System.out.println("4  - Register Patient");
        System.out.println("5  - Schedule Staff Shift");
        System.out.println("6  - Schedule Operation");
        System.out.println("7  - Medication Schedule");
        System.out.println("8  - Ambulance Register / Track");
        System.out.println("9  - Full System Report");
        System.out.println("10 - Bed Record");
        System.out.println("11 - Room Management");
        System.out.println("12 - Notification");
        System.out.println("13 - Test Result");
        System.out.println("14 - Appointment Scheduling");
        System.out.println("15 - Ambulance ETA");
        System.out.println("16 - Create Patient Billing");
        System.out.println("17 - Save & Exit");
        System.out.print(">> ");
    }

    // =====================================================================================
    // REGISTER HOSPITAL
    // =====================================================================================
    private static void registerHospital() throws SQLException {
        System.out.println("\n--- Register Hospital ---");
        System.out.print("Hospital name: ");
        String name = scanner.nextLine();

        System.out.print("Location: ");
        String loc = scanner.nextLine();

        hospitalDAO.insertHospital(name, loc);
        System.out.println("✅ Hospital registered to database.");

        System.out.print("Enter hospital feedback: ");
        String feedback = scanner.nextLine();

    }

    // =====================================================================================
    // REGISTER DOCTOR
    // =====================================================================================
    private static void registerDoctor() throws SQLException {
        System.out.println("\n--- Register Doctor ---");

        System.out.print("Doctor name: ");
        String name = scanner.nextLine();

        System.out.print("Staff ID: ");
        String id = scanner.nextLine();

        System.out.print("Specialization: ");
        String spec = scanner.nextLine();

        staffDAO.saveDoctor(id, name, spec);
        System.out.println("✅ Doctor added to database.");
    }

    // =====================================================================================
    // REGISTER NURSE
    // =====================================================================================
    private static void registerNurse() throws SQLException {
        System.out.println("\n--- Register Nurse ---");

        System.out.print("Nurse name: ");
        String name = scanner.nextLine();

        System.out.print("Staff ID: ");
        String id = scanner.nextLine();

        System.out.print("Department: ");
        String department = scanner.nextLine();

        staffDAO.saveNurse(id, name, department);
        System.out.println("✅ Nurse added to database.");
    }

    // =====================================================================================
    // REGISTER PATIENT
    // =====================================================================================
    private static void registerPatient() throws SQLException {
        System.out.println("\n--- Register Patient ---");

        System.out.print("Name: ");
        String name = scanner.nextLine();

        System.out.print("Age: ");
        int age = readIntOrDefault(0);

        System.out.print("National ID: ");
        String nid = scanner.nextLine();

        System.out.print("Number of chronic diseases: ");
        int dCount = readIntOrDefault(0);

        for (int i = 0; i < dCount; i++) {
            System.out.print("Disease #" + (i + 1) + ": ");
            String disease = scanner.nextLine();
            medicalDAO.saveMedicalHistory(nid, "CHRONIC", disease);
        }

        System.out.print("Number of allergies: ");
        int aCount = readIntOrDefault(0);

        for (int i = 0; i < aCount; i++) {
            System.out.print("Allergy #" + (i + 1) + ": ");
            String allergy = scanner.nextLine();
            medicalDAO.saveMedicalHistory(nid, "ALLERGY", allergy);
        }

        System.out.print("Number of family history: ");
        int fCount = readIntOrDefault(0);

        for (int i = 0; i < fCount; i++) {
            System.out.print("family history #" + (i + 1) + ": ");
            String familyHistory = scanner.nextLine();
            medicalDAO.saveMedicalHistory(nid, "FAMILY", familyHistory);
        }

        System.out.print("Number of past surgeries: ");
        int sCount = readIntOrDefault(0);

        for (int i = 0; i < sCount; i++) {
            System.out.print("past surgeries #" + (i + 1) + ": ");
            String surgery = scanner.nextLine();
            medicalDAO.saveMedicalHistory(nid, "SURGERY", surgery);
        }

        System.out.print("Smoker? ");
        boolean smoker = parseBoolean(scanner.nextLine());

        System.out.print("Sleep hours per day: ");
        int sleep = readIntOrDefault(0);

        System.out.print("Exercises? ");
        boolean exercise = parseBoolean(scanner.nextLine());
        int lastStreak = medicalDAO.getLastStreak(nid);
        int newStreak;

        if (!smoker && exercise && sleep >= 7 && sleep <= 9) {
            newStreak = lastStreak + 1;
        } else {
            newStreak = 0;
        }

// ====== PUT MESSAGE HERE ======
        String message;

        if (newStreak == 0) {
            message = "🌱 Fresh start! Today is a new chance to build better habits.";
        } else {
            message = "❤️ Great job! You're staying consistent—keep going!";
        }

        System.out.println(message);
        medicalDAO.saveDailyHabits(nid, smoker, sleep, exercise, newStreak);

        System.out.println("✅ Patient registered to database.");

        nurseActions();
    }

    // ===============================
    // CREATE PATIENT BILLING
    // ===============================
    private static void createPatientBilling() throws SQLException {

        System.out.println("\n--- Create Patient Billing ---");

        System.out.print("Patient National ID: ");
        String patientId = scanner.nextLine();

        boolean addMore = true;

        while (addMore) {

            System.out.print("Service description: ");
            String desc = scanner.nextLine();

            System.out.print("Service cost: ");
            double cost = Double.parseDouble(scanner.nextLine());

            billingDAO.createBill(patientId, cost, "UNPAID");

            double total = billingDAO.getTotalByPatientId(patientId);
            System.out.println("Total Bill = " + total + " EGP");

            System.out.print("Add another service? (yes/no): ");
            String choice = scanner.nextLine();

            if (!choice.equalsIgnoreCase("yes")) {
                addMore = false;
            }
        }

        System.out.println("✅ Billing saved to database.");
    }
    // =====================================================================================
    // SHIFT SCHEDULING
    // =====================================================================================
    private static void scheduleStaffShift() throws SQLException {
        System.out.println("\n--- Schedule Staff Shift ---");

        System.out.print("Staff ID: ");
        String sid = scanner.nextLine();

        System.out.print("Shift Time (yyyy-MM-dd HH:mm): ");
        String shift = scanner.nextLine();

        staffDAO.assignShift(sid, shift);
        System.out.println("✅ Shift scheduled in database.");
    }

    // =====================================================================================
    // OPERATION SCHEDULING
    // =====================================================================================
    private static void scheduleOperation() throws SQLException {
        System.out.println("\n--- Operation Scheduling ---");

        System.out.print("Patient ID: ");
        String pid = scanner.nextLine();

        System.out.print("Operation Name: ");
        String opName = scanner.nextLine();

        System.out.print("Operation date (yyyy-MM-dd HH:mm): ");
        LocalDateTime dt = readDateTimeOrLater(LocalDateTime.now().plusDays(1));
        String opDate = dt.format(DTF);

        medicalDAO.saveMedicalHistory(pid, "OPERATION", opName + " @ " + opDate);
        System.out.println("✅ Operation scheduled in database.");
    }

    // =====================================================================================
    // MEDICATIONS
    // =====================================================================================
    private static void medicationSchedule() throws SQLException {
        System.out.println("\n--- Medication Schedule ---");

        System.out.print("Patient ID: ");
        String pid = scanner.nextLine();

        System.out.print("Medication name: ");
        String med = scanner.nextLine();

        System.out.print("Dosage time: ");
        String time = scanner.nextLine();

        System.out.print("Quantity: ");
        int q = readIntOrDefault(1);

        System.out.print("Reminder: ");
        String rem = scanner.nextLine();

        medicalDAO.saveMedicationSchedule(pid, med, time, q, "ON");
        System.out.println("✅ Medication saved to database.");
    }

    // =====================================================================================
    // AMBULANCE
    // =====================================================================================

    private static void ambulanceRegisterOrTrack() throws SQLException {

        System.out.println("\n--- Ambulance Menu ---");
        System.out.println("1) Register Ambulance");
        System.out.println("2) Track Ambulance");
        System.out.print(">> ");

        String ch = scanner.nextLine();

        if (ch.equals("1")) {

            System.out.print("Ambulance ID: ");
            String id = scanner.nextLine();

            System.out.print("Location: ");
            String loc = scanner.nextLine();
            trackingDAO = new TrackingModuleDAO(connection);
            Ambulance a = new Ambulance(id, loc);
            trackingDAO.saveAmbulance(a);

            System.out.println("Ambulance added to database");

        } else if (ch.equals("2")) {

            System.out.print("Enter Ambulance ID: ");
            String id = scanner.nextLine();

            Ambulance amb = trackingDAO.getAmbulanceById(id);

            if (amb == null) {
                System.out.println("Ambulance not found");
                return;
            }

            int targetX = 9;
            int targetY = 9;

            moveAmbulance(amb, targetX, targetY);
        }
    }

    // ==============================
    // MOVE AMBULANCE
    // ==============================
    private static void moveAmbulance(Ambulance amb, int targetX, int targetY) {

        int size = 15;

        while (amb.getX() != targetX || amb.getY() != targetY) {

            if (amb.getX() < targetX) amb.setX(amb.getX() + 1);
            else if (amb.getX() > targetX) amb.setX(amb.getX() - 1);

            if (amb.getY() < targetY) amb.setY(amb.getY() + 1);
            else if (amb.getY() > targetY) amb.setY(amb.getY() - 1);

            drawMap(amb, targetX, targetY);

            try {
                Thread.sleep(400);
            } catch (InterruptedException ignored) {
            }
        }

        System.out.println("Ambulance arrived at hospital");
    }

    // =====================================================================================
    // ETA UPDATE
    // =====================================================================================
    private static void updateAmbulanceETA() throws SQLException {
        System.out.println("\n--- Update ETA ---");

        System.out.print("Ambulance ID: ");
        String ambId = scanner.nextLine();

        System.out.print("ETA minutes: ");
        int minutes = readIntOrDefault(1);

        trackingDAO.saveETA(
                ambId,
                minutes,
                LocalDateTime.now().toString()
        );

        System.out.println("✅ ETA updated in database.");
    }

    // =====================================================================================
    // SYSTEM REPORTS
    // =====================================================================================
    private static void systemReports(
            HospitalModuleDAO hospitalDAO,
            PatientModuleDAO patientDAO,
            AppointmentModuleDAO appointmentDAO,
            BillingModuleDAO billingDAO,
            TrackingModuleDAO trackingDAO
    ) throws SQLException {

        System.out.println("===== SYSTEM REPORT =====");

        hospitalDAO.checkAvailableBeds();
        patientDAO.countPatients();
        appointmentDAO.countAppointment();
        billingDAO.calculateTotalRevenue();
        trackingDAO.getActiveAmbulances();
    }

    // =====================================================================================
// BEDS
// =====================================================================================
    private static void recordBedAssignment() throws SQLException {

        System.out.println("\n--- Bed Assignment ---");

        System.out.print("Patient ID: ");
        String pid = scanner.nextLine();

        System.out.print("Bed Number: ");
        String bed = scanner.nextLine();

        hospitalDAO.assignBed(pid, bed);
        System.out.println("Bed assigned successfully");
    }

    // =====================================================================================
// ROOM MANAGEMENT
// =====================================================================================
    private static void recordRoomManagement() throws SQLException {

        System.out.println("\n--- Room Management ---");

        hospitalDAO.showAvailableRooms();

        System.out.print("Assign patient to room? (yes/no): ");
        String choice = scanner.nextLine();

        if (choice.equalsIgnoreCase("yes")) {

            System.out.print("Patient ID: ");
            String pid = scanner.nextLine();

            System.out.print("Room Number: ");
            String room = scanner.nextLine();

            boolean assigned = hospitalDAO.assignRoom(pid, room);

            if (assigned) {
                System.out.println("Room assigned successfully");
            } else {
                System.out.println("Room not available");
            }
        }
    }

    // =====================================================================================
    // NOTIFICATION
    // =====================================================================================
    private static void addNotification() throws SQLException {
        System.out.println("\n--- Notification ---");

        System.out.print("Patient ID: ");
        String pid = scanner.nextLine();

        System.out.print("Message: ");
        String msg = scanner.nextLine();

        System.out.print("Type (medication/appointment): ");
        String type = scanner.nextLine();

        patientDAO.saveNotification(pid, msg, type, LocalDateTime.now().toString());
        System.out.println("✅ Notification saved to database.");
    }

    // =====================================================================================
    // TEST RESULTS
    // =====================================================================================
    private static void addTestResult() throws SQLException {
        System.out.println("\n--- Test Result ---");

        System.out.print("Patient ID: ");
        String pid = scanner.nextLine();

        System.out.print("Test name: ");
        String name = scanner.nextLine();

        System.out.print("Result: ");
        String res = scanner.nextLine();

        String testDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        medicalDAO.saveTestResult(pid, name, testDate, res);
        System.out.println("✅ Result saved to database.");
    }

    // =====================================================================================
    // APPOINTMENT SCHEDULING
    // =====================================================================================
    private static void scheduleAppointment() throws SQLException {
        System.out.println("\n--- Appointment Scheduling ---");

        System.out.print("Patient ID: ");
        String pid = scanner.nextLine();

        System.out.print("Doctor ID: ");
        String did = scanner.nextLine();

        System.out.print("Appointment date (yyyy-MM-dd HH:mm): ");
        LocalDateTime dt = readDateTimeOrLater(LocalDateTime.now().plusHours(1));
        String date = dt.format(DTF);

        appointmentDAO.saveAppointment(pid, did, date.split(" ")[0], date.split(" ")[1], "SCHEDULED");
        System.out.println("✅ Appointment saved to database.");
    }

    // =====================================================================================
    // SAVE & EXIT
    // =====================================================================================
    private static void saveAndExit() {
        System.out.println("\nSaving all system data to database...");
        System.out.println("✅ All Data Saved Successfully to Database");
        System.out.println("\n🚀 HELIX Smart Healthcare System");
        System.out.println("   Redefining healthcare through intelligent systems.");
    }

    // =====================================================================================
    // HELPERS
    // =====================================================================================
    private static int readIntOrDefault(int def) {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (Exception e) {
            return def;
        }
    }

    private static boolean parseBoolean(String s) {
        s = s.toLowerCase();
        return s.equals("yes") || s.equals("y") || s.equals("true") || s.equals("1");
    }

    private static LocalDateTime readDateTimeOrNow() {
        try {
            return LocalDateTime.parse(scanner.nextLine(), DTF);
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }

    private static LocalDateTime readDateTimeOrLater(LocalDateTime fallback) {
        try {
            return LocalDateTime.parse(scanner.nextLine(), DTF);
        } catch (Exception e) {
            return fallback;
        }
    }

    private static boolean readYesNo(String message) {
        while (true) {
            System.out.print(message + " (yes/no): ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("yes")) return true;
            if (input.equals("no")) return false;
            System.out.println("Please enter yes or no only.");
        }
    }

    public static void drawMap(Ambulance amb, int tx, int ty) {
        int size = 15;

        System.out.println("\nAMBULANCE TRACKING");

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {

                if (i == amb.getY() && j == amb.getX()) {
                    System.out.print("🚑 ");
                } else if (i == ty && j == tx) {
                    System.out.print("🏥 ");
                } else {
                    System.out.print("⬜ ");
                }
            }
            System.out.println();
        }
    }
}
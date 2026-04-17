package Database;

import java.sql.*;

public class PatientModuleDAO {

    private final Connection connection;

    public PatientModuleDAO(Connection connection) {
        this.connection = connection;
    }

    // =========================
    // CREATE TABLES
    // =========================
    public void createTables() {

        String patientsTable = """
            CREATE TABLE IF NOT EXISTS patients (
                patient_id TEXT PRIMARY KEY,
                name TEXT,
                age INTEGER
                status TEXT
            );
        """;

        String notificationsTable = """
            CREATE TABLE IF NOT EXISTS notifications (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                patient_id TEXT,
                message TEXT,
                type TEXT,
                timestamp TEXT
            );
        """;

        String monitoringTable = """
            CREATE TABLE IF NOT EXISTS patient_monitoring (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                patient_id TEXT,
                nurse_id TEXT,
                status TEXT,
                monitor_time TEXT
            );
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(patientsTable);
            stmt.execute(notificationsTable);
            stmt.execute(monitoringTable);
            System.out.println("✅ Patient tables ready");
        } catch (SQLException e) {
            System.out.println("⚠️ Patient table creation failed");
            e.printStackTrace();
        }
    }

    // =========================
    // SAVE PATIENT
    // =========================
    public void savePatient(String patientId, String name, int age) {

        String sql = """
            INSERT OR IGNORE INTO patients (patient_id, name, age)
            VALUES (?, ?, ?)
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, patientId);
            ps.setString(2, name);
            ps.setInt(3, age);
            ps.executeUpdate();
            System.out.println("✅ Patient saved");
        } catch (SQLException e) {
            System.out.println("⚠️ Patient save failed");
        }
    }

    // =========================
    // SAVE NOTIFICATION
    // =========================
    public void saveNotification(String patientId, String message, String type, String timestamp) {

        String sql = """
            INSERT INTO notifications (patient_id, message, type, timestamp)
            VALUES (?, ?, ?, ?)
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, patientId);
            ps.setString(2, message);
            ps.setString(3, type);
            ps.setString(4, timestamp);
            ps.executeUpdate();
            System.out.println("🔔 Notification saved");
        } catch (SQLException e) {
            System.out.println("⚠️ Notification save failed");
        }
    }

    // =========================
    // LOAD PATIENT INFO
    // =========================
    public void loadPatient(String patientId) {

        String sql = """
            SELECT patient_id, name, age
            FROM patients
            WHERE patient_id = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, patientId);
            ResultSet rs = ps.executeQuery();

            System.out.println("\n=== PATIENT INFO ===");

            if (rs.next()) {
                System.out.println(
                        "ID: " + rs.getString("patient_id") +
                                " | Name: " + rs.getString("name") +
                                " | Age: " + rs.getInt("age")
                );
            } else {
                System.out.println("Patient not found");
            }

        } catch (SQLException e) {
            System.out.println("⚠️ Failed to load patient");
        }
    }

    // =========================
    // LOAD NOTIFICATIONS
    // =========================
    public void loadNotifications(String patientId) {

        String sql = """
            SELECT message, type, timestamp
            FROM notifications
            WHERE patient_id = ?
            ORDER BY timestamp DESC
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, patientId);
            ResultSet rs = ps.executeQuery();

            System.out.println("\n🔔 NOTIFICATIONS");

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println(
                        rs.getString("timestamp") + " | " +
                                rs.getString("type") + " | " +
                                rs.getString("message")
                );
            }

            if (!found) {
                System.out.println("No notifications found");
            }

        } catch (SQLException e) {
            System.out.println("⚠️ Failed to load notifications");
        }
    }
    public void savePatientMonitoring(
            String patientId,
            String nurseId,
            String status
    ) {

        String sql = """
        INSERT INTO patient_monitoring
        (patient_id, nurse_id, status, monitor_time)
        VALUES (?, ?, ?, datetime('now'))
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, patientId);
            ps.setString(2, nurseId);
            ps.setString(3, status);

            int rows = ps.executeUpdate();
            System.out.println("✔ Monitoring saved (" + rows + " row)");

        } catch (SQLException e) {
            System.out.println("❌ Failed to save monitoring");
            e.printStackTrace();
        }
    }

    // =========================
    // LOAD PATIENT MONITORING
    // =========================
    public void loadPatientMonitoring(String patientId) {

        String sql = """
            SELECT nurse_id, status, monitor_time
            FROM patient_monitoring
            WHERE patient_id = ?
            ORDER BY monitor_time DESC
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, patientId);
            ResultSet rs = ps.executeQuery();

            System.out.println("\n=== PATIENT MONITORING ===");

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println(
                        rs.getString("monitor_time") + " | Nurse: " +
                                rs.getString("nurse_id") + " | Status: " +
                                rs.getString("status")
                );
            }

            if (!found) {
                System.out.println("No monitoring records found");
            }

        } catch (SQLException e) {
            System.out.println("⚠️ Failed to load patient monitoring");
        }
    }
    public int countPatients() throws SQLException {
        String sql = "SELECT COUNT(*) FROM patients";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
}
package Database;

import java.sql.*;

public class MedicalModuleDAO {

    private Connection connection;

    public MedicalModuleDAO(Connection connection) {
        this.connection = connection;
    }

    // =========================
    // CREATE TABLES
    // =========================
    public void createTables() {

        try (Statement stmt = connection.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS medical_history (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    patient_id TEXT,
                    type TEXT,
                    value TEXT
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS test_results (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    patient_id TEXT,
                    test_name TEXT,
                    test_date TEXT,
                    result TEXT
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS daily_habits (
                    patient_id TEXT PRIMARY KEY,
                    smoking INTEGER,
                    sleep_hours INTEGER,
                    exercises INTEGER,
                    streak INTEGER
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS medication_schedule (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    patient_id TEXT,
                    medication_name TEXT,
                    dosage_time TEXT,
                    quantity INTEGER,
                    reminder TEXT
                )
            """);

            System.out.println("✅ Medical tables created successfully");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // =========================
    // SAVE MEDICAL HISTORY
    // =========================
    public void saveMedicalHistory(String patientId, String type, String value) throws SQLException {

        String sql = """
            INSERT INTO medical_history(patient_id, type, value)
            VALUES (?, ?, ?)
        """;

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, patientId);
        ps.setString(2, type);
        ps.setString(3, value);

        ps.executeUpdate();
    }

    // =========================
    // SAVE TEST RESULT
    // =========================
    public void saveTestResult(
            String patientId,
            String testName,
            String testDate,
            String result
    ) throws SQLException {

        String sql = """
            INSERT INTO test_results(patient_id, test_name, test_date, result)
            VALUES (?, ?, ?, ?)
        """;

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, patientId);
        ps.setString(2, testName);
        ps.setString(3, testDate);
        ps.setString(4, result);

        ps.executeUpdate();
    }
    public int getLastStreak(String patientId) throws SQLException {

        String sql = """
        SELECT streak
        FROM daily_habits
        WHERE patient_id = ?
        ORDER BY rowid DESC
        LIMIT 1
    """;

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, patientId);

        var rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getInt("streak");
        }

        return 0; // أول مرة
    }
    // =========================
    // SAVE / UPDATE DAILY HABITS (STREAK ✅)
    // =========================
    public void saveDailyHabits(
            String patientId,
            boolean smoking,
            int sleepHours,
            boolean exercises,
            int streak
    ) throws SQLException {

        String sql = """
            INSERT INTO daily_habits(patient_id, smoking, sleep_hours, exercises, streak)
            VALUES (?, ?, ?, ?, ?)
            ON CONFLICT(patient_id)
            DO UPDATE SET
                smoking = excluded.smoking,
                sleep_hours = excluded.sleep_hours,
                exercises = excluded.exercises,
                streak = excluded.streak
        """;

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, patientId);
        ps.setInt(2, smoking ? 1 : 0);
        ps.setInt(3, sleepHours);
        ps.setInt(4, exercises ? 1 : 0);
        ps.setInt(5, streak);

        ps.executeUpdate();
    }

    // =========================
    // SAVE MEDICATION SCHEDULE ✅
    // =========================
    public void saveMedicationSchedule(
            String patientId,
            String medicationName,
            String dosageTime,
            int quantity,
            String reminder
    ) throws SQLException {

        String sql = """
            INSERT INTO medication_schedule
            (patient_id, medication_name, dosage_time, quantity, reminder)
            VALUES (?, ?, ?, ?, ?)
        """;

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, patientId);
        ps.setString(2, medicationName);
        ps.setString(3, dosageTime);
        ps.setInt(4, quantity);
        ps.setString(5, reminder);

        ps.executeUpdate();
    }// =========================
    // LOAD MEDICAL HISTORY
// =========================
    public void loadMedicalHistory(String patientId) {

        String sql = """
        SELECT type, value
        FROM medical_history
        WHERE patient_id = ?
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, patientId);
            ResultSet rs = ps.executeQuery();

            System.out.println("\n=== MEDICAL HISTORY ===");

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println(
                        rs.getString("type") +
                                " : " +
                                rs.getString("value")
                );
            }

            if (!found) {
                System.out.println("No medical history found");
            }

        } catch (SQLException e) {
            System.out.println("⚠️ Failed to load medical history");
        }
    } // =========================
    // LOAD TEST RESULTS
// =========================
    public void loadTestResults(String patientId) {

        String sql = """
        SELECT test_name, test_date, result
        FROM test_results
        WHERE patient_id = ?
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, patientId);
            ResultSet rs = ps.executeQuery();

            System.out.println("\n=== TEST RESULTS ===");

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println(
                        "Test: " + rs.getString("test_name") +
                                " | Date: " + rs.getString("test_date") +
                                " | Result: " + rs.getString("result")
                );
            }

            if (!found) {
                System.out.println("No test results found");
            }

        } catch (SQLException e) {
            System.out.println("⚠️ Failed to load test results");
        }
    }// =========================
    // LOAD DAILY HABITS
// =========================
    public void loadDailyHabits(String patientId) {

        String sql = """
        SELECT *
        FROM daily_habits
        WHERE patient_id = ?
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, patientId);
            ResultSet rs = ps.executeQuery();

            System.out.println("\n=== DAILY HABITS ===");

            if (rs.next()) {
                System.out.println(
                        "Smoking: " + (rs.getInt("smoking") == 1 ? "Yes" : "No") +
                                " | Sleep Hours: " + rs.getInt("sleep_hours") +
                                " | Exercises: " + (rs.getInt("exercises") == 1 ? "Yes" : "No") +
                                " | Streak: " + rs.getInt("streak")
                );
            } else {
                System.out.println("No daily habits found");
            }

        } catch (SQLException e) {
            System.out.println("⚠️ Failed to load daily habits");
        }
    }// =========================
    // LOAD MEDICATION SCHEDULE
// =========================
    public void loadMedicationSchedule(String patientId) {

        String sql = """
        SELECT medication_name, dosage_time, quantity, reminder
        FROM medication_schedule
        WHERE patient_id = ?
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, patientId);
            ResultSet rs = ps.executeQuery();

            System.out.println("\n=== MEDICATION SCHEDULE ===");

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println(
                        "Medication: " + rs.getString("medication_name") +
                                " | Time: " + rs.getString("dosage_time") +
                                " | Quantity: " + rs.getInt("quantity") +
                                " | Reminder: " + rs.getString("reminder")
                );
            }

            if (!found) {
                System.out.println("No medication schedule found");
            }

        } catch (SQLException e) {
            System.out.println("⚠️ Failed to load medication schedule");
        }
    }
}
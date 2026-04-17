package Database;

import java.sql.*;

import Hospital.Staff;
import Hospital.Doctor;
import Hospital.Nurses;

public class StaffModuleDAO {

    private final Connection connection;

    public StaffModuleDAO(Connection connection) {
        this.connection = connection;
    }

    // =========================
    // CREATE TABLES
    // =========================
    public void createTables() {

        try (Statement stmt = connection.createStatement()) {

            // ===== STAFF
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS staff (
                    staff_id TEXT PRIMARY KEY,
                    name TEXT,
                    type TEXT,
                    specialization TEXT
                )
            """);

            // ===== DOCTORS
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS doctors (
                    doctor_id TEXT PRIMARY KEY,
                    name TEXT,
                    specialization TEXT
                )
            """);

            // ===== NURSES
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS nurses (
                    nurse_id TEXT PRIMARY KEY,
                    name TEXT,
                    department TEXT
                )
            """);

            // ===== SHIFTS
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS staff_shifts (
                    staff_id TEXT,
                    shift TEXT,
                    PRIMARY KEY (staff_id, shift)
                )
            """);

            System.out.println("✅ Staff tables ready");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // =========================
    // SAVE BASE STAFF
    // =========================
    private void saveStaffBase(
            String id,
            String name,
            String type,
            String specialization
    ) throws SQLException {

        String sql = """
            INSERT OR IGNORE INTO staff
            (staff_id, name, type, specialization)
            VALUES (?, ?, ?, ?)
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, name);
            ps.setString(3, type);
            ps.setString(4, specialization);
            ps.executeUpdate();
        }
    }

    // =========================
    // SAVE DOCTOR
    // =========================
    public void saveDoctor(String id, String name, String specialization) {

        try {
            saveStaffBase(id, name, "DOCTOR", specialization);

            String sql = """
                INSERT OR IGNORE INTO doctors
                (doctor_id, name, specialization)
                VALUES (?, ?, ?)
            """;

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, id);
                ps.setString(2, name);
                ps.setString(3, specialization);
                ps.executeUpdate();
            }

            System.out.println("👨‍⚕️ Doctor saved");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // =========================
    // SAVE NURSE
    // =========================
    public void saveNurse(String id, String name, String department) {

        try {
            saveStaffBase(id, name, "NURSE", null);

            String sql = """
                INSERT OR IGNORE INTO nurses
                (nurse_id, name, department)
                VALUES (?, ?, ?)
            """;

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, id);
                ps.setString(2, name);
                ps.setString(3, department);
                ps.executeUpdate();
            }

            System.out.println("👩‍⚕️ Nurse saved");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // =========================
    // SAVE SHIFT
    // =========================
    public void assignShift(String staffId, String shift) {

        String sql = """
            INSERT OR IGNORE INTO staff_shifts
            (staff_id, shift)
            VALUES (?, ?)
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, staffId);
            ps.setString(2, shift);
            ps.executeUpdate();
            System.out.println("🕒 Shift assigned");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }// =========================
    // LOAD ALL STAFF
// =========================
    public void loadAllStaff() {

        String sql = "SELECT * FROM staff";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\n=== ALL STAFF ===");

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println(
                        rs.getString("staff_id") + " | " +
                                rs.getString("name") + " | " +
                                rs.getString("type") + " | " +
                                rs.getString("specialization")
                );
            }

            if (!found) {
                System.out.println("No staff found");
            }

        } catch (SQLException e) {
            System.out.println("⚠️ Failed to load staff");
        }
    }// =========================
    // LOAD DOCTORS
// =========================
    public void loadDoctors() {

        String sql = "SELECT * FROM doctors";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\n=== DOCTORS ===");

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println(
                        rs.getString("doctor_id") + " | " +
                                rs.getString("name") + " | " +
                                rs.getString("specialization")
                );
            }

            if (!found) {
                System.out.println("No doctors found");
            }

        } catch (SQLException e) {
            System.out.println("⚠️ Failed to load doctors");
        }
    }// =========================
    // LOAD NURSES
// =========================
    public void loadNurses() {

        String sql = "SELECT * FROM nurses";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\n=== NURSES ===");

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println(
                        rs.getString("nurse_id") + " | " +
                                rs.getString("name") + " | " +
                                rs.getString("department")
                );
            }

            if (!found) {
                System.out.println("No nurses found");
            }

        } catch (SQLException e) {
            System.out.println("⚠️ Failed to load nurses");
        }
    }// =========================
    // LOAD STAFF SHIFTS
// =========================
    public void loadStaffShifts() {

        String sql = "SELECT * FROM staff_shifts";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\n=== STAFF SHIFTS ===");

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println(
                        "Staff: " + rs.getString("staff_id") +
                                " | Shift: " + rs.getString("shift")
                );
            }

            if (!found) {
                System.out.println("No shifts found");
            }

        } catch (SQLException e) {
            System.out.println("⚠️ Failed to load staff shifts");
        }
    }
}
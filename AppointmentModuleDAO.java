package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.ResultSet;

public class AppointmentModuleDAO {

    private final Connection connection;

    public AppointmentModuleDAO(Connection connection) {
        this.connection = connection;
    }

    // =========================
    // CREATE TABLE
    // =========================
    public void createTables() throws SQLException {

        Statement stmt = connection.createStatement();

        stmt.execute("""
            CREATE TABLE IF NOT EXISTS appointment (
                appointment_id INTEGER PRIMARY KEY AUTOINCREMENT,
                patient_id TEXT,
                doctor_id TEXT,
                appointment_date TEXT,
                appointment_time TEXT,
                status TEXT
            )
        """);

        System.out.println("✅ Appointment table created");
    }

    // =========================
    // SAVE APPOINTMENT
    // =========================
    public void saveAppointment(
            String patientId,
            String doctorId,
            String date,
            String time,
            String status
    ) throws SQLException {

        String sql = """
            INSERT INTO appointment(patient_id, doctor_id, appointment_date, appointment_time, status)
            VALUES (?, ?, ?, ?, ?)
        """;

        PreparedStatement ps = connection.prepareStatement(sql);

        ps.setString(1, patientId);
        ps.setString(2, doctorId);
        ps.setString(3, date);
        ps.setString(4, time);
        ps.setString(5, status);

        ps.executeUpdate();

        System.out.println("✅ Appointment saved successfully");
    }

    // =========================
    // VIEW APPOINTMENTS (OPTIONAL BUT USEFUL)
    // =========================
    public ResultSet getAppointmentsForPatient(String patientId) throws SQLException {

        String sql = """
            SELECT * FROM appointment
            WHERE patient_id = ?
        """;

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, patientId);

        return ps.executeQuery();
    }
    // =========================
// LOAD ALL APPOINTMENTS
// =========================
    public void loadAppointments() {

        String sql = """
        SELECT appointment_id,
               patient_id,
               doctor_id,
               appointment_date,
               appointment_time,
               status
        FROM appointment
        ORDER BY appointment_date, appointment_time
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n📅 APPOINTMENTS LIST");

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println(
                        "ID: " + rs.getInt("appointment_id") +
                                " | Patient: " + rs.getString("patient_id") +
                                " | Doctor: " + rs.getString("doctor_id") +
                                " | Date: " + rs.getString("appointment_date") +
                                " | Time: " + rs.getString("appointment_time") +
                                " | Status: " + rs.getString("status")
                );
            }

            if (!found) {
                System.out.println("No appointments found.");
            }

        } catch (SQLException e) {
            System.out.println("⚠️ Cannot load appointments");
        }
    }
    public int countAppointment() throws SQLException {
        String sql = "SELECT COUNT(*) FROM appointment";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
}
package Database;

import java.sql.*;

public class BillingModuleDAO {

    private final Connection connection;

    public BillingModuleDAO(Connection connection) {
        this.connection = connection;
    }

    // =========================
    // CREATE TABLE
    // =========================
    public void createTables() {

        try (Statement stmt = connection.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS billing (
                    bill_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    patient_id TEXT,
                    amount REAL,
                    description TEXT,
                    bill_date TEXT,
                    status TEXT
                )
            """);

            System.out.println("✅ Billing table ready");

        } catch (SQLException e) {
            System.out.println("⚠️ Billing table creation failed");
            e.printStackTrace();
        }
    }

    // =========================
    // CREATE BILL
    // =========================
    public void createBill(String patientId, double amount, String status) {

        String sql = """
            INSERT INTO billing
            (patient_id, amount, description, bill_date, status)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, patientId);
            ps.setDouble(2, amount);
            ps.setString(3, "Medical Services");
            ps.setString(4, java.time.LocalDateTime.now().toString());
            ps.setString(5, status);

            ps.executeUpdate();
            System.out.println("💰 Bill saved");

        } catch (SQLException e) {
            System.out.println("⚠️ Bill insert failed");
        }
    }
    public double getTotalByPatientId(String patientId) throws SQLException {
        double total = 0;

        String sql = "SELECT SUM(amount) FROM billing WHERE patient_id = ?";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, patientId);

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            total = rs.getDouble(1);
        }

        rs.close();
        ps.close();
        return total;
    }
    // =========================
    // LOAD BILLS ✅
    // =========================
    public void loadBills() {

        String sql = "SELECT * FROM billing";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n=== BILLS LOADED FROM DATABASE ===");

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println(
                        "Bill ID: " + rs.getInt("bill_id") +
                                " | Patient: " + rs.getString("patient_id") +
                                " | Amount: " + rs.getDouble("amount") +
                                " | Status: " + rs.getString("status") +
                                " | Date: " + rs.getString("bill_date")
                );
            }

            if (!found) {
                System.out.println("No bills found.");
            }

        } catch (SQLException e) {
            System.out.println("⚠️ Failed to load bills");
        }
    }

    // =========================
    // GET BILLS FOR PATIENT
    // =========================
    public ResultSet getBillsForPatient(String patientId) throws SQLException {

        String sql = """
            SELECT * FROM billing
            WHERE patient_id = ?
        """;

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, patientId);

        return ps.executeQuery();
    }
    public double calculateTotalRevenue() throws SQLException {
        String sql = "SELECT SUM(amount) FROM billing WHERE status = 'PAID'";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        return 0.0;
    }
}
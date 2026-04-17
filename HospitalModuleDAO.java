package Database;

import java.sql.*;

public class HospitalModuleDAO {

    private final Connection connection;

    public HospitalModuleDAO(Connection connection) {
        this.connection = connection;
    }

    // =========================
    // CREATE TABLES
    // =========================
    public void createTables() {
        execute("""
            CREATE TABLE IF NOT EXISTS hospital (
                hospital_id INTEGER PRIMARY KEY AUTOINCREMENT,
                hospital_name TEXT,
                location TEXT
            )
        """);

        execute("""
            CREATE TABLE IF NOT EXISTS rooms (
                room_id TEXT PRIMARY KEY,
                status TEXT DEFAULT 'Available'
            )
        """);

        execute("""
            CREATE TABLE IF NOT EXISTS beds (
                bed_id TEXT PRIMARY KEY,
                room_id TEXT,
                status TEXT DEFAULT 'Available'
            )
        """);

        execute("""
            CREATE TABLE IF NOT EXISTS room_assignment (
                patient_id TEXT PRIMARY KEY,
                room_id TEXT
            )
        """);

        execute("""
            CREATE TABLE IF NOT EXISTS bed_assignment (
                patient_id TEXT,
                bed_id TEXT,
                PRIMARY KEY (patient_id, bed_id)
            )
        """);

        execute("""
            CREATE TABLE IF NOT EXISTS operations (
                operation_id INTEGER PRIMARY KEY AUTOINCREMENT,
                patient_id TEXT,
                operation_name TEXT,
                operation_date TEXT
            )
        """);
    }

    // =========================
    // HOSPITAL
    // =========================
    public void insertHospital(String name, String location) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO hospital (hospital_name, location) VALUES (?, ?)")) {
            ps.setString(1, name);
            ps.setString(2, location);
            ps.executeUpdate();
        } catch (SQLException ignored) {}
    }

    // =========================
    // ROOMS
    // =========================
    public void insertRoom(String roomId) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT OR IGNORE INTO rooms (room_id, status) VALUES (?, 'Available')")) {
            ps.setString(1, roomId);
            ps.executeUpdate();
        } catch (SQLException ignored) {}
    }

    public void showAvailableRooms() {
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT room_id FROM rooms WHERE status='Available'")) {

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println("Room: " + rs.getString("room_id"));
            }
            if (!found) System.out.println("No available rooms");

        } catch (SQLException ignored) {}
    }

    public boolean assignRoom(String patientId, String roomId) {
        try (PreparedStatement ps = connection.prepareStatement(
                "UPDATE rooms SET status='Occupied' WHERE room_id=? AND status='Available'")) {

            ps.setString(1, roomId);
            int updated = ps.executeUpdate();

            if (updated == 0) return false;

            try (PreparedStatement ps2 = connection.prepareStatement(
                    "INSERT OR REPLACE INTO room_assignment VALUES (?, ?)")) {
                ps2.setString(1, patientId);
                ps2.setString(2, roomId);
                ps2.executeUpdate();
            }

            return true;

        } catch (SQLException e) {
            return false;
        }
    }

    public void loadRooms() {
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM rooms")) {

            while (rs.next()) {
                System.out.println(
                        rs.getString("room_id") + " | " + rs.getString("status"));
            }
        } catch (SQLException ignored) {}
    }

    public void loadRoomAssignments() {
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM room_assignment")) {

            while (rs.next()) {
                System.out.println(
                        rs.getString("patient_id") + " -> " + rs.getString("room_id"));
            }
        } catch (SQLException ignored) {}
    }

    // =========================
    // BEDS
    // =========================
    public void insertBed(String bedId, String roomId) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT OR IGNORE INTO beds (bed_id, room_id, status) VALUES (?, ?, 'Available')")) {
            ps.setString(1, bedId);
            ps.setString(2, roomId);
            ps.executeUpdate();
        } catch (SQLException ignored) {}
    }

    public void checkAvailableBeds() throws SQLException {
        String sql = "SELECT COUNT(*) FROM beds WHERE status = 'AVAILABLE'";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println("Available beds: " + count);
            }
        }
    }

    public boolean assignBed(String patientId, String bedId) {
        try (PreparedStatement ps = connection.prepareStatement(
                "UPDATE beds SET status='Occupied' WHERE bed_id=? AND status='Available'")) {

            ps.setString(1, bedId);
            int updated = ps.executeUpdate();

            if (updated == 0) return false;

            try (PreparedStatement ps2 = connection.prepareStatement(
                    "INSERT OR REPLACE INTO bed_assignment VALUES (?, ?)")) {
                ps2.setString(1, patientId);
                ps2.setString(2, bedId);
                ps2.executeUpdate();
            }

            return true;

        } catch (SQLException e) {
            return false;
        }
    }

    public void loadBeds() {
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM beds")) {

            while (rs.next()) {
                System.out.println(
                        rs.getString("bed_id") + " | " +
                                rs.getString("room_id") + " | " +
                                rs.getString("status"));
            }
        } catch (SQLException ignored) {}
    }

    // =========================
    // OPERATIONS
    // =========================
    public void scheduleOperation(String patientId, String name, String date) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO operations (patient_id, operation_name, operation_date) VALUES (?, ?, ?)")) {
            ps.setString(1, patientId);
            ps.setString(2, name);
            ps.setString(3, date);
            ps.executeUpdate();
        } catch (SQLException ignored) {}
    }

    public void loadOperations() {
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM operations")) {

            while (rs.next()) {
                System.out.println(
                        rs.getString("patient_id") + " | " +
                                rs.getString("operation_name") + " | " +
                                rs.getString("operation_date"));
            }
        } catch (SQLException ignored) {}
    }
    public void seedRoomsAndBedsIfEmpty() {
        try (Statement st = connection.createStatement()) {

            // ======================
            // SEED ROOMS
            // ======================
            ResultSet rsRooms = st.executeQuery("SELECT COUNT(*) FROM rooms");
            if (rsRooms.next() && rsRooms.getInt(1) == 0) {

                PreparedStatement roomPS = connection.prepareStatement(
                        "INSERT INTO rooms (room_id, status) VALUES (?, 'Available')");

                for (int i = 1; i <= 5; i++) {
                    roomPS.setString(1, String.valueOf(i));
                    roomPS.executeUpdate();
                }
                roomPS.close();
            }

            // ======================
            // SEED BEDS
            // ======================
            ResultSet rsBeds = st.executeQuery("SELECT COUNT(*) FROM beds");
            if (rsBeds.next() && rsBeds.getInt(1) == 0) {

                PreparedStatement bedPS = connection.prepareStatement(
                        "INSERT INTO beds (bed_id, room_id, status) VALUES (?, ?, 'Available')");

                bedPS.setString(1, "B1");
                bedPS.setString(2, "1");
                bedPS.executeUpdate();

                bedPS.setString(1, "B2");
                bedPS.setString(2, "1");
                bedPS.executeUpdate();

                bedPS.setString(1, "B3");
                bedPS.setString(2, "2");
                bedPS.executeUpdate();

                bedPS.setString(1, "B4");
                bedPS.setString(2, "2");
                bedPS.executeUpdate();

                bedPS.setString(1, "B5");
                bedPS.setString(2, "3");
                bedPS.executeUpdate();

                bedPS.close();
            }

        } catch (SQLException e) {
            System.out.println("Seeding rooms and beds failed");
        }
    }



    // =========================
    // HELPER
    // =========================
    private void execute(String sql) {
        try (Statement st = connection.createStatement()) {
            st.execute(sql);
        } catch (SQLException ignored) {}
    }

}
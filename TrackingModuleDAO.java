package Database;

import java.sql.*;
import Tracking.Ambulance;

public class TrackingModuleDAO {

    private final Connection connection;

    public TrackingModuleDAO(Connection connection) {
        this.connection = connection;
    }

    public void createTables() {

        try (Statement stmt = connection.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS ambulance (
                    ambulance_id TEXT PRIMARY KEY,
                    current_location TEXT,
                    status TEXT
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS ambulance_eta (
                    ambulance_id TEXT PRIMARY KEY,
                    estimated_time INTEGER,
                    last_updated TEXT
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS tracking_log (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    entity_type TEXT,
                    entity_id TEXT,
                    location TEXT,
                    status TEXT,
                    timestamp TEXT
                )
            """);

        } catch (SQLException e) {
            System.out.println("Tracking table creation skipped");
        }
    }

    public void saveAmbulance(Ambulance a) {

        String sql = """
            INSERT OR IGNORE INTO ambulance
            (ambulance_id, current_location, status)
            VALUES (?, ?, ?)
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, a.getId());
            ps.setString(2, a.getLocation());
            ps.setString(3, a.getStatus());
            ps.executeUpdate();

            saveTrackingLog(
                    "Ambulance",
                    a.getId(),
                    a.getLocation(),
                    "Created",
                    java.time.LocalDateTime.now().toString()
            );

        } catch (SQLException e) {
            System.out.println("Ambulance already exists");
        }
    }

    public String getETAByAmbulanceId(String ambulanceId) throws SQLException {
        String sql = """
        SELECT eta_minutes, estimated_arrival, update_time 
        FROM ambulance_eta 
        WHERE ambulance_id = ? 
        ORDER BY update_time DESC 
        LIMIT 1
    """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, ambulanceId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return String.format("ETA: %d minutes | Arrival: %s (Updated: %s)",
                        rs.getInt("eta_minutes"),
                        rs.getString("estimated_arrival"),
                        rs.getString("update_time"));
            }
        }
        return "No ETA available";
    }
    public void updateAmbulanceLocation(String ambulanceId, String location) {

        String sql = """
            UPDATE ambulance
            SET current_location = ?
            WHERE ambulance_id = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, location);
            ps.setString(2, ambulanceId);
            ps.executeUpdate();

            saveTrackingLog(
                    "Ambulance",
                    ambulanceId,
                    location,
                    "Moving",
                    java.time.LocalDateTime.now().toString()
            );

        } catch (SQLException e) {
            System.out.println("Location update failed");
        }
    }

    public void updateAmbulanceStatus(String ambulanceId, String status) {

        String sql = """
            UPDATE ambulance
            SET status = ?
            WHERE ambulance_id = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setString(2, ambulanceId);
            ps.executeUpdate();

            saveTrackingLog(
                    "Ambulance",
                    ambulanceId,
                    null,
                    status,
                    java.time.LocalDateTime.now().toString()
            );

        } catch (SQLException e) {
            System.out.println("Status update failed");
        }
    }

    public void saveETA(String ambulanceId, int eta, String timestamp) {

        String sql = """
            INSERT INTO ambulance_eta(ambulance_id, estimated_time, last_updated)
            VALUES (?, ?, ?)
            ON CONFLICT(ambulance_id)
            DO UPDATE SET
                estimated_time = excluded.estimated_time,
                last_updated = excluded.last_updated
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, ambulanceId);
            ps.setInt(2, eta);
            ps.setString(3, timestamp);
            ps.executeUpdate();

            saveTrackingLog(
                    "Ambulance",
                    ambulanceId,
                    null,
                    "ETA Updated: " + eta + " min",
                    timestamp
            );

        } catch (SQLException e) {
            System.out.println("ETA update skipped");
        }
    }

    public void saveTrackingLog(
            String entityType,
            String entityId,
            String location,
            String status,
            String timestamp
    ) {

        String sql = """
            INSERT INTO tracking_log
            (entity_type, entity_id, location, status, timestamp)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, entityType);
            ps.setString(2, entityId);
            ps.setString(3, location);
            ps.setString(4, status);
            ps.setString(5, timestamp);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Tracking log skipped");
        }
    }

    public void showTrackingHistory(String ambulanceId) {

        String sql = """
            SELECT * FROM tracking_log
            WHERE entity_id = ?
            ORDER BY timestamp
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, ambulanceId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                System.out.println(
                        rs.getString("timestamp") + " | " +
                                rs.getString("status") + " | " +
                                rs.getString("location")
                );
            }

        } catch (SQLException e) {
            System.out.println("Cannot fetch tracking history");
        }
    }

    public void loadAmbulances() {

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM ambulance")) {

            while (rs.next()) {
                System.out.println(
                        rs.getString("ambulance_id") + " | " +
                                rs.getString("current_location") + " | " +
                                rs.getString("status")
                );
            }

        } catch (SQLException e) {
            System.out.println("Failed to load ambulances");
        }
    }

    public void loadAmbulanceETA() {

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM ambulance_eta")) {

            while (rs.next()) {
                System.out.println(
                        rs.getString("ambulance_id") + " | " +
                                rs.getInt("estimated_time") + " | " +
                                rs.getString("last_updated")
                );
            }

        } catch (SQLException e) {
            System.out.println("Failed to load ETA");
        }
    }

    public void loadTrackingLog() {

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT * FROM tracking_log ORDER BY timestamp")) {

            while (rs.next()) {
                System.out.println(
                        rs.getString("timestamp") + " | " +
                                rs.getString("entity_type") + " | " +
                                rs.getString("entity_id") + " | " +
                                rs.getString("status") + " | " +
                                rs.getString("location")
                );
            }

        } catch (SQLException e) {
            System.out.println("Failed to load tracking log");
        }
    }

    public Ambulance getAmbulanceById(String id) {

        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM ambulance WHERE ambulance_id = ?")) {

            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Ambulance a = new Ambulance(
                        rs.getString("ambulance_id"),
                        rs.getString("current_location")
                );
                a.setStatus(rs.getString("status"));
                return a;
            }

        } catch (SQLException e) {
            System.out.println("Failed to load ambulance");
        }

        return null;
    }
    public int getActiveAmbulances() throws SQLException {
        String sql = "SELECT COUNT(*) FROM ambulance WHERE status = 'ACTIVE' OR status = 'ON_CALL'";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
}
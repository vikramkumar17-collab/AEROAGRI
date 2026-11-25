package com.aeroagri.repository;

import com.aeroagri.model.Scan;
import com.aeroagri.model.Sector;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

/** All SQL lives here (JDBC through JdbcTemplate). */
@Repository
public class AeroRepository {
    private final JdbcTemplate jdbc;

    public AeroRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // ---------- sensor readings ----------
    public void saveReading(int sensorId, double value, String details, String unit, String mode) {
        jdbc.update("INSERT INTO sensor_readings (sensor_id, reading_value, details, unit, simulated, mode) "
                + "VALUES (?, ?, ?, ?, TRUE, ?)", sensorId, value, details, unit, mode);
    }

    public List<Map<String, Object>> latestReadings() {
        String sql = "SELECT r.sensor_id, s.name, s.type, s.status, r.reading_value, r.details, r.unit, r.created_at "
                + "FROM sensor_readings r JOIN sensors s ON s.id = r.sensor_id "
                + "WHERE r.id IN (SELECT MAX(id) FROM sensor_readings GROUP BY sensor_id) ORDER BY r.sensor_id";
        return jdbc.query(sql, (rs, i) -> {
            Map<String, Object> m = new HashMap<>();
            m.put("sensorId", rs.getInt("sensor_id"));
            m.put("name", rs.getString("name"));
            m.put("type", rs.getString("type"));
            m.put("status", rs.getString("status"));
            m.put("value", rs.getDouble("reading_value"));
            m.put("details", rs.getString("details"));
            m.put("unit", rs.getString("unit"));
            m.put("simulated", true);
            m.put("time", rs.getTimestamp("created_at").toString());
            return m;
        });
    }

    // ---------- scans ----------
    public int insertScan(Scan scan) {
        KeyHolder keys = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO scans (field_name, mode, status, started_at) VALUES (?, ?, ?, ?)",
                    new String[] {"ID"});
            ps.setString(1, scan.getFieldName());
            ps.setString(2, scan.getMode());
            ps.setString(3, scan.getStatus());
            ps.setTimestamp(4, Timestamp.valueOf(scan.getStartedAt()));
            return ps;
        }, keys);
        return keys.getKey().intValue();
    }

    public void completeScan(Scan scan) {
        jdbc.update("UPDATE scans SET status=?, completed_at=?, overall_status=?, avg_height=?, warning=? WHERE id=?",
                scan.getStatus(), Timestamp.valueOf(scan.getCompletedAt()), scan.getOverallStatus(),
                scan.getAvgHeight(), scan.getWarning(), scan.getId());
    }

    private final RowMapper<Scan> scanMapper = (rs, i) -> {
        Scan s = new Scan();
        s.setId(rs.getInt("id"));
        s.setFieldName(rs.getString("field_name"));
        s.setMode(rs.getString("mode"));
        s.setStatus(rs.getString("status"));
        s.setStartedAt(rs.getTimestamp("started_at").toLocalDateTime());
        Timestamp done = rs.getTimestamp("completed_at");
        if (done != null) {
            s.setCompletedAt(done.toLocalDateTime());
        }
        s.setOverallStatus(rs.getString("overall_status"));
        s.setAvgHeight(rs.getDouble("avg_height"));
        s.setWarning(rs.getString("warning"));
        s.setProgress("COMPLETED".equals(s.getStatus()) ? 100 : 0);
        return s;
    };

    public Scan findScan(int id) {
        List<Scan> list = jdbc.query("SELECT * FROM scans WHERE id = ?", scanMapper, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public List<Scan> history() {
        return jdbc.query("SELECT * FROM scans ORDER BY id DESC LIMIT 20", scanMapper);
    }

    // ---------- scan results ----------
    public void saveResults(int scanId, List<Sector> sectors) {
        for (Sector s : sectors) {
            jdbc.update("INSERT INTO scan_results (scan_id, sector, avg_height, problem_row, status, note) "
                    + "VALUES (?, ?, ?, ?, ?, ?)", scanId, s.getName(), s.getAvgHeightCm(), s.getProblemRow(),
                    s.getStatus(), s.getNote());
        }
    }

    public List<Sector> findResults(int scanId) {
        return jdbc.query("SELECT * FROM scan_results WHERE scan_id = ? ORDER BY sector",
                (rs, i) -> new Sector(rs.getString("sector"), rs.getDouble("avg_height"), rs.getInt("problem_row")),
                scanId);
    }
}

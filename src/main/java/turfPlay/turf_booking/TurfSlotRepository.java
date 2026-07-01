package turfPlay.turf_booking;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class TurfSlotRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<TurfSlot> slotRowMapper = (rs, rowNum) -> {
        TurfSlot slot = new TurfSlot();
        slot.setId(rs.getLong("id"));
        slot.setTurfId(rs.getLong("turf_id"));
        slot.setTurfName(rs.getString("turf_name"));
        slot.setSlotDate(rs.getDate("slot_date").toLocalDate());
        slot.setStartTime(rs.getTime("start_time").toLocalTime());
        slot.setEndTime(rs.getTime("end_time").toLocalTime());
        slot.setStatus(rs.getString("status"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            slot.setCreatedAt(createdAt.toLocalDateTime());
        }

        return slot;
    };

    public TurfSlotRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<TurfSlot> findAll() {
        String sql = """
                SELECT ts.id, ts.turf_id, t.name AS turf_name, ts.slot_date,
                       ts.start_time, ts.end_time, ts.status, ts.created_at
                FROM turf_slots ts
                JOIN turfs t ON ts.turf_id = t.id
                ORDER BY ts.slot_date DESC, ts.start_time ASC
                """;

        return jdbcTemplate.query(sql, slotRowMapper);
    }

    public List<TurfSlot> findByTurfId(Long turfId) {
        String sql = """
                SELECT ts.id, ts.turf_id, t.name AS turf_name, ts.slot_date,
                       ts.start_time, ts.end_time, ts.status, ts.created_at
                FROM turf_slots ts
                JOIN turfs t ON ts.turf_id = t.id
                WHERE ts.turf_id = ?
                ORDER BY ts.slot_date ASC, ts.start_time ASC
                """;

        return jdbcTemplate.query(sql, slotRowMapper, turfId);
    }

    public Optional<TurfSlot> findById(Long id) {
        String sql = """
                SELECT ts.id, ts.turf_id, t.name AS turf_name, ts.slot_date,
                       ts.start_time, ts.end_time, ts.status, ts.created_at
                FROM turf_slots ts
                JOIN turfs t ON ts.turf_id = t.id
                WHERE ts.id = ?
                """;

        return jdbcTemplate.query(sql, slotRowMapper, id).stream().findFirst();
    }

    public void save(TurfSlot slot) {
        String sql = """
                INSERT INTO turf_slots (turf_id, slot_date, start_time, end_time, status)
                VALUES (?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(
                sql,
                slot.getTurfId(),
                slot.getSlotDate(),
                slot.getStartTime(),
                slot.getEndTime(),
                slot.getStatus()
        );
    }

    public void updateStatus(Long id, String status) {
        String sql = "UPDATE turf_slots SET status = ? WHERE id = ?";
        jdbcTemplate.update(sql, status, id);
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM turf_slots WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
    public List<TurfSlot> findAvailableByTurfId(Long turfId) {
        String sql = """
                SELECT ts.id, ts.turf_id, t.name AS turf_name, ts.slot_date,
                       ts.start_time, ts.end_time, ts.status, ts.created_at
                FROM turf_slots ts
                JOIN turfs t ON ts.turf_id = t.id
                WHERE ts.turf_id = ?
                  AND ts.status = 'AVAILABLE'
                  AND ts.slot_date >= CURRENT_DATE
                ORDER BY ts.slot_date ASC, ts.start_time ASC
                """;

        return jdbcTemplate.query(sql, slotRowMapper, turfId);
    }
}

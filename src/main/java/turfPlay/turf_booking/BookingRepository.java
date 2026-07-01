package turfPlay.turf_booking;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.sql.PreparedStatement;
import java.sql.Statement;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

@Repository
public class BookingRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Booking> bookingRowMapper = (rs, rowNum) -> {
        Booking booking = new Booking();

        booking.setId(rs.getLong("id"));
        booking.setUserId(rs.getLong("user_id"));
        booking.setTurfSlotId(rs.getLong("turf_slot_id"));
        booking.setBookingStatus(rs.getString("booking_status"));

        Timestamp bookedAt = rs.getTimestamp("booked_at");
        if (bookedAt != null) {
            booking.setBookedAt(bookedAt.toLocalDateTime());
        }

        booking.setUserName(rs.getString("full_name"));
        booking.setUserEmail(rs.getString("email"));
        booking.setTurfName(rs.getString("turf_name"));
        booking.setTurfLocation(rs.getString("location"));
        booking.setSlotDate(rs.getDate("slot_date").toLocalDate());
        booking.setStartTime(rs.getTime("start_time").toLocalTime());
        booking.setEndTime(rs.getTime("end_time").toLocalTime());

        return booking;
    };

    public BookingRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long createBooking(Long userId, Long slotId) {
        String sql = """
                INSERT INTO bookings (user_id, turf_slot_id, booking_status)
                VALUES (?, ?, 'CONFIRMED')
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, userId);
            ps.setLong(2, slotId);
            return ps;
        }, keyHolder);
        
        if (keyHolder.getKey() != null) {
            return keyHolder.getKey().longValue();
        }
        return null;
    }

    public Optional<Booking> findById(Long id) {
        String sql = """
                SELECT b.id, b.user_id, b.turf_slot_id, b.booking_status, b.booked_at,
                       u.full_name, u.email,
                       t.name AS turf_name, t.location,
                       ts.slot_date, ts.start_time, ts.end_time
                FROM bookings b
                JOIN users u ON b.user_id = u.id
                JOIN turf_slots ts ON b.turf_slot_id = ts.id
                JOIN turfs t ON ts.turf_id = t.id
                WHERE b.id = ?
                """;
        List<Booking> results = jdbcTemplate.query(sql, bookingRowMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public List<Booking> findByUserId(Long userId) {
        String sql = """
                SELECT b.id, b.user_id, b.turf_slot_id, b.booking_status, b.booked_at,
                       u.full_name, u.email,
                       t.name AS turf_name, t.location,
                       ts.slot_date, ts.start_time, ts.end_time
                FROM bookings b
                JOIN users u ON b.user_id = u.id
                JOIN turf_slots ts ON b.turf_slot_id = ts.id
                JOIN turfs t ON ts.turf_id = t.id
                WHERE b.user_id = ?
                ORDER BY ts.slot_date DESC, ts.start_time DESC
                """;

        return jdbcTemplate.query(sql, bookingRowMapper, userId);
    }

    public List<Booking> findAll() {
        String sql = """
                SELECT b.id, b.user_id, b.turf_slot_id, b.booking_status, b.booked_at,
                       u.full_name, u.email,
                       t.name AS turf_name, t.location,
                       ts.slot_date, ts.start_time, ts.end_time
                FROM bookings b
                JOIN users u ON b.user_id = u.id
                JOIN turf_slots ts ON b.turf_slot_id = ts.id
                JOIN turfs t ON ts.turf_id = t.id
                ORDER BY b.booked_at DESC
                """;

        return jdbcTemplate.query(sql, bookingRowMapper);
    }

    public void cancelBooking(Long bookingId) {
        String sql = "UPDATE bookings SET booking_status = 'CANCELLED' WHERE id = ?";
        jdbcTemplate.update(sql, bookingId);
    }
    public Long findSlotIdByBookingId(Long bookingId) {
        String sql = "SELECT turf_slot_id FROM bookings WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, bookingId);
    }
}

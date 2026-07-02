package turfPlay.turf_booking;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class SplitContributionRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<SplitContribution> contributionRowMapper = (rs, rowNum) -> {
        SplitContribution contribution = new SplitContribution();
        contribution.setId(rs.getLong("id"));
        contribution.setBookingId(rs.getLong("booking_id"));
        contribution.setContributorName(rs.getString("contributor_name"));
        contribution.setAmount(rs.getBigDecimal("amount"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            contribution.setCreatedAt(createdAt.toLocalDateTime());
        }
        return contribution;
    };

    public SplitContributionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addContribution(Long bookingId, String contributorName, BigDecimal amount) {
        String sql = "INSERT INTO split_contributions (booking_id, contributor_name, amount) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, bookingId, contributorName, amount);
    }

    public List<SplitContribution> findByBookingId(Long bookingId) {
        String sql = "SELECT * FROM split_contributions WHERE booking_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, contributionRowMapper, bookingId);
    }
}

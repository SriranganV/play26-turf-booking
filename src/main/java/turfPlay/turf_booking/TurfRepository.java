package turfPlay.turf_booking;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class TurfRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Turf> turfRowMapper = (rs, rowNum) -> {
        Turf turf = new Turf();
        turf.setId(rs.getLong("id"));
        turf.setName(rs.getString("name"));
        turf.setLocation(rs.getString("location"));
        turf.setDescription(rs.getString("description"));
        turf.setPricePerHour(rs.getBigDecimal("price_per_hour"));
        turf.setSupportedSports(rs.getString("supported_sports"));
        turf.setActive(rs.getBoolean("active"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            turf.setCreatedAt(createdAt.toLocalDateTime());
        }

        return turf;
    };

    public TurfRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Turf> findAll() {
        String sql = """
                SELECT id, name, location, description, price_per_hour, supported_sports, active, created_at
                FROM turfs
                ORDER BY created_at DESC
                """;

        return jdbcTemplate.query(sql, turfRowMapper);
    }

    public List<Turf> findAllActive() {
        String sql = """
                SELECT id, name, location, description, price_per_hour, supported_sports, active, created_at
                FROM turfs
                WHERE active = TRUE
                ORDER BY created_at DESC
                """;

        return jdbcTemplate.query(sql, turfRowMapper);
    }

    public Optional<Turf> findById(Long id) {
        String sql = """
                SELECT id, name, location, description, price_per_hour, supported_sports, active, created_at
                FROM turfs
                WHERE id = ?
                """;

        return jdbcTemplate.query(sql, turfRowMapper, id).stream().findFirst();
    }

    public void save(Turf turf) {
        String sql = """
                INSERT INTO turfs (name, location, description, price_per_hour, supported_sports, active)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(
                sql,
                turf.getName(),
                turf.getLocation(),
                turf.getDescription(),
                turf.getPricePerHour(),
                turf.getSupportedSports(),
                turf.isActive()
        );
    }

    public void update(Turf turf) {
        String sql = """
                UPDATE turfs
                SET name = ?, location = ?, description = ?, price_per_hour = ?, supported_sports = ?, active = ?
                WHERE id = ?
                """;

        jdbcTemplate.update(
                sql,
                turf.getName(),
                turf.getLocation(),
                turf.getDescription(),
                turf.getPricePerHour(),
                turf.getSupportedSports(),
                turf.isActive(),
                turf.getId()
        );
    }

    public void deactivateById(Long id) {
        String sql = "UPDATE turfs SET active = FALSE WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM turfs WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public int countAll() {
        Integer c = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM turfs", Integer.class);
        return c == null ? 0 : c;
    }
}

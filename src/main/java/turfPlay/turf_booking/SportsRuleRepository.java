package turfPlay.turf_booking;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class SportsRuleRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<SportsRule> sportsRuleRowMapper = (rs, rowNum) -> {
        SportsRule rule = new SportsRule();
        rule.setId(rs.getLong("id"));
        rule.setSportName(rs.getString("sport_name"));
        rule.setTitle(rs.getString("title"));
        rule.setContent(rs.getString("content"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            rule.setCreatedAt(createdAt.toLocalDateTime());
        }

        return rule;
    };

    public SportsRuleRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<SportsRule> findAll() {
        String sql = """
                SELECT id, sport_name, title, content, created_at
                FROM sports_rules
                ORDER BY sport_name ASC, created_at DESC
                """;

        return jdbcTemplate.query(sql, sportsRuleRowMapper);
    }

    public List<SportsRule> findBySportName(String sportName) {
        String sql = """
                SELECT id, sport_name, title, content, created_at
                FROM sports_rules
                WHERE sport_name = ?
                ORDER BY created_at DESC
                """;

        return jdbcTemplate.query(sql, sportsRuleRowMapper, sportName);
    }

    public Optional<SportsRule> findById(Long id) {
        String sql = """
                SELECT id, sport_name, title, content, created_at
                FROM sports_rules
                WHERE id = ?
                """;

        return jdbcTemplate.query(sql, sportsRuleRowMapper, id).stream().findFirst();
    }

    public void save(SportsRule rule) {
        String sql = """
                INSERT INTO sports_rules (sport_name, title, content)
                VALUES (?, ?, ?)
                """;

        jdbcTemplate.update(
                sql,
                rule.getSportName(),
                rule.getTitle(),
                rule.getContent()
        );
    }

    public void update(SportsRule rule) {
        String sql = """
                UPDATE sports_rules
                SET sport_name = ?, title = ?, content = ?
                WHERE id = ?
                """;

        jdbcTemplate.update(
                sql,
                rule.getSportName(),
                rule.getTitle(),
                rule.getContent(),
                rule.getId()
        );
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM sports_rules WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}

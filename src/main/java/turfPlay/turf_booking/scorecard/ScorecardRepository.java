package turfPlay.turf_booking.scorecard;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import turfPlay.turf_booking.GeneratedKeyExtractor;

import java.sql.*;
import java.util.List;
import java.util.Optional;

@Repository
public class ScorecardRepository {

    private final JdbcTemplate jdbc;

    private static final String SELECT_FULL = """
        SELECT sc.id, sc.match_id, sc.innings, sc.batting_team_id, sc.bowling_team_id,
               sc.total_runs, sc.total_wickets, sc.total_overs, sc.extras,
               sc.target, sc.status, sc.created_at, sc.updated_at,
               bt.team_name AS batting_team_name,
               bwt.team_name AS bowling_team_name
        FROM scorecards sc
        LEFT JOIN teams bt ON sc.batting_team_id = bt.id
        LEFT JOIN teams bwt ON sc.bowling_team_id = bwt.id
        """;

    private final RowMapper<Scorecard> rm = (rs, n) -> {
        Scorecard sc = new Scorecard();
        sc.setId(rs.getLong("id"));
        sc.setMatchId(rs.getLong("match_id"));
        sc.setInnings(rs.getObject("innings", Integer.class));
        sc.setBattingTeamId(rs.getObject("batting_team_id", Long.class));
        sc.setBattingTeamName(rs.getString("batting_team_name"));
        sc.setBowlingTeamId(rs.getObject("bowling_team_id", Long.class));
        sc.setBowlingTeamName(rs.getString("bowling_team_name"));
        sc.setTotalRuns(rs.getObject("total_runs", Integer.class));
        sc.setTotalWickets(rs.getObject("total_wickets", Integer.class));
        sc.setTotalOvers(rs.getObject("total_overs", Double.class));
        sc.setExtras(rs.getObject("extras", Integer.class));
        sc.setTarget(rs.getObject("target", Integer.class));
        sc.setStatus(rs.getString("status"));
        Timestamp ca = rs.getTimestamp("created_at"); if (ca != null) sc.setCreatedAt(ca.toLocalDateTime());
        Timestamp ua = rs.getTimestamp("updated_at"); if (ua != null) sc.setUpdatedAt(ua.toLocalDateTime());
        return sc;
    };

    public ScorecardRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public List<Scorecard> findAll() {
        return jdbc.query(SELECT_FULL + "ORDER BY sc.created_at DESC", rm);
    }

    public Optional<Scorecard> findById(Long id) {
        return jdbc.query(SELECT_FULL + "WHERE sc.id = ?", rm, id).stream().findFirst();
    }

    public List<Scorecard> findByMatchId(Long matchId) {
        return jdbc.query(SELECT_FULL + "WHERE sc.match_id = ? ORDER BY sc.innings ASC", rm, matchId);
    }

    public long save(Scorecard sc) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO scorecards(match_id,innings,batting_team_id,bowling_team_id,total_runs,total_wickets,total_overs,extras,target,status) VALUES(?,?,?,?,?,?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, sc.getMatchId()); ps.setObject(2, sc.getInnings());
            ps.setObject(3, sc.getBattingTeamId()); ps.setObject(4, sc.getBowlingTeamId());
            ps.setObject(5, sc.getTotalRuns()); ps.setObject(6, sc.getTotalWickets());
            ps.setObject(7, sc.getTotalOvers()); ps.setObject(8, sc.getExtras());
            ps.setObject(9, sc.getTarget()); ps.setString(10, sc.getStatus() == null ? "IN_PROGRESS" : sc.getStatus());
            return ps;
        }, kh);
        Long id = GeneratedKeyExtractor.extractId(kh);
        return id != null ? id : 0L;
    }

    public void update(Scorecard sc) {
        jdbc.update("UPDATE scorecards SET innings=?,batting_team_id=?,bowling_team_id=?,total_runs=?,total_wickets=?,total_overs=?,extras=?,target=?,status=? WHERE id=?",
            sc.getInnings(), sc.getBattingTeamId(), sc.getBowlingTeamId(),
            sc.getTotalRuns(), sc.getTotalWickets(), sc.getTotalOvers(),
            sc.getExtras(), sc.getTarget(), sc.getStatus(), sc.getId());
    }

    public void deleteById(Long id) { jdbc.update("DELETE FROM scorecards WHERE id = ?", id); }

    public long count() {
        Integer c = jdbc.queryForObject("SELECT COUNT(*) FROM scorecards", Integer.class);
        return c == null ? 0 : c;
    }
}

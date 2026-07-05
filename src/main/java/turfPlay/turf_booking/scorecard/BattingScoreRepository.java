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
public class BattingScoreRepository {

    private final JdbcTemplate jdbc;

    private static final String SELECT_FULL = "SELECT bs.id, bs.scorecard_id, bs.player_id, bs.runs, bs.balls, " +
               "bs.fours, bs.sixes, bs.strike_rate, bs.is_out, bs.dismissal_type, " +
               "bs.bowler_id, bs.fielder_id, bs.batting_position, bs.created_at, " +
               "p.player_name, " +
               "b.player_name AS bowler_name, " +
               "f.player_name AS fielder_name " +
        "FROM batting_scores bs " +
        "LEFT JOIN players p ON bs.player_id = p.id " +
        "LEFT JOIN players b ON bs.bowler_id = b.id " +
        "LEFT JOIN players f ON bs.fielder_id = f.id ";

    private final RowMapper<BattingScore> rm = (rs, n) -> {
        BattingScore bs = new BattingScore();
        bs.setId(rs.getLong("id"));
        bs.setScorecardId(rs.getLong("scorecard_id"));
        bs.setPlayerId(rs.getObject("player_id", Long.class));
        bs.setPlayerName(rs.getString("player_name"));
        bs.setRuns(rs.getObject("runs", Integer.class));
        bs.setBalls(rs.getObject("balls", Integer.class));
        bs.setFours(rs.getObject("fours", Integer.class));
        bs.setSixes(rs.getObject("sixes", Integer.class));
        bs.setStrikeRate(rs.getObject("strike_rate", Double.class));
        bs.setIsOut(rs.getBoolean("is_out"));
        bs.setDismissalType(rs.getString("dismissal_type"));
        bs.setBowlerId(rs.getObject("bowler_id", Long.class));
        bs.setBowlerName(rs.getString("bowler_name"));
        bs.setFielderId(rs.getObject("fielder_id", Long.class));
        bs.setFielderName(rs.getString("fielder_name"));
        bs.setBattingPosition(rs.getObject("batting_position", Integer.class));
        Timestamp ca = rs.getTimestamp("created_at"); if (ca != null) bs.setCreatedAt(ca.toLocalDateTime());
        return bs;
    };

    public BattingScoreRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public List<BattingScore> findByScorecardId(Long scorecardId) {
        return jdbc.query(SELECT_FULL + "WHERE bs.scorecard_id = ? ORDER BY bs.batting_position ASC", rm, scorecardId);
    }

    public Optional<BattingScore> findById(Long id) {
        return jdbc.query(SELECT_FULL + "WHERE bs.id = ?", rm, id).stream().findFirst();
    }

    public long save(BattingScore bs) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO batting_scores(scorecard_id,player_id,runs,balls,fours,sixes,strike_rate,is_out,dismissal_type,bowler_id,fielder_id,batting_position) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
            if (bs.getScorecardId() != null) ps.setLong(1, bs.getScorecardId()); else ps.setNull(1, Types.BIGINT);
            if (bs.getPlayerId() != null) ps.setLong(2, bs.getPlayerId()); else ps.setNull(2, Types.BIGINT);
            if (bs.getRuns() != null) ps.setInt(3, bs.getRuns()); else ps.setNull(3, Types.INTEGER);
            if (bs.getBalls() != null) ps.setInt(4, bs.getBalls()); else ps.setNull(4, Types.INTEGER);
            if (bs.getFours() != null) ps.setInt(5, bs.getFours()); else ps.setNull(5, Types.INTEGER);
            if (bs.getSixes() != null) ps.setInt(6, bs.getSixes()); else ps.setNull(6, Types.INTEGER);
            if (bs.getStrikeRate() != null) ps.setDouble(7, bs.getStrikeRate()); else ps.setNull(7, Types.DOUBLE);
            ps.setBoolean(8, bs.isIsOut());
            ps.setString(9, bs.getDismissalType());
            if (bs.getBowlerId() != null) ps.setLong(10, bs.getBowlerId()); else ps.setNull(10, Types.BIGINT);
            if (bs.getFielderId() != null) ps.setLong(11, bs.getFielderId()); else ps.setNull(11, Types.BIGINT);
            if (bs.getBattingPosition() != null) ps.setInt(12, bs.getBattingPosition()); else ps.setNull(12, Types.INTEGER);
            return ps;
        }, kh);
        Long id = GeneratedKeyExtractor.extractId(kh);
        return id != null ? id : 0L;
    }

    public void update(BattingScore bs) {
        jdbc.update("UPDATE batting_scores SET player_id=?,runs=?,balls=?,fours=?,sixes=?,strike_rate=?,is_out=?,dismissal_type=?,bowler_id=?,fielder_id=?,batting_position=? WHERE id=?",
            bs.getPlayerId(), bs.getRuns(), bs.getBalls(), bs.getFours(), bs.getSixes(),
            bs.getStrikeRate(), bs.isIsOut(), bs.getDismissalType(), bs.getBowlerId(),
            bs.getFielderId(), bs.getBattingPosition(), bs.getId());
    }

    public void deleteById(Long id) { jdbc.update("DELETE FROM batting_scores WHERE id = ?", id); }

    public int countByScorecardId(Long scorecardId) {
        Integer c = jdbc.queryForObject("SELECT COUNT(*) FROM batting_scores WHERE scorecard_id = ?", Integer.class, scorecardId);
        return c == null ? 0 : c;
    }

    public Integer sumRunsByScorecardId(Long scorecardId) {
        Integer s = jdbc.queryForObject("SELECT COALESCE(SUM(runs),0) FROM batting_scores WHERE scorecard_id = ?", Integer.class, scorecardId);
        return s == null ? 0 : s;
    }

    public Integer countWicketsByScorecardId(Long scorecardId) {
        Integer c = jdbc.queryForObject("SELECT COUNT(*) FROM batting_scores WHERE scorecard_id = ? AND is_out = TRUE", Integer.class, scorecardId);
        return c == null ? 0 : c;
    }
}

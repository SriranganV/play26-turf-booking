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
public class BowlingScoreRepository {

    private final JdbcTemplate jdbc;

    private static final String SELECT_FULL = """
        SELECT bw.id, bw.scorecard_id, bw.player_id, bw.overs, bw.maidens,
               bw.runs, bw.wickets, bw.economy, bw.dots, bw.wides, bw.no_balls,
               bw.created_at,
               p.player_name
        FROM bowling_scores bw
        LEFT JOIN players p ON bw.player_id = p.id
        """;

    private final RowMapper<BowlingScore> rm = (rs, n) -> {
        BowlingScore bw = new BowlingScore();
        bw.setId(rs.getLong("id"));
        bw.setScorecardId(rs.getLong("scorecard_id"));
        bw.setPlayerId(rs.getObject("player_id", Long.class));
        bw.setPlayerName(rs.getString("player_name"));
        bw.setOvers(rs.getObject("overs", Double.class));
        bw.setMaidens(rs.getObject("maidens", Integer.class));
        bw.setRuns(rs.getObject("runs", Integer.class));
        bw.setWickets(rs.getObject("wickets", Integer.class));
        bw.setEconomy(rs.getObject("economy", Double.class));
        bw.setDots(rs.getObject("dots", Integer.class));
        bw.setWides(rs.getObject("wides", Integer.class));
        bw.setNoBalls(rs.getObject("no_balls", Integer.class));
        Timestamp ca = rs.getTimestamp("created_at"); if (ca != null) bw.setCreatedAt(ca.toLocalDateTime());
        return bw;
    };

    public BowlingScoreRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public List<BowlingScore> findByScorecardId(Long scorecardId) {
        return jdbc.query(SELECT_FULL + "WHERE bw.scorecard_id = ? ORDER BY bw.id ASC", rm, scorecardId);
    }

    public Optional<BowlingScore> findById(Long id) {
        return jdbc.query(SELECT_FULL + "WHERE bw.id = ?", rm, id).stream().findFirst();
    }

    public long save(BowlingScore bw) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO bowling_scores(scorecard_id,player_id,overs,maidens,runs,wickets,economy,dots,wides,no_balls) VALUES(?,?,?,?,?,?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, bw.getScorecardId()); ps.setObject(2, bw.getPlayerId());
            ps.setObject(3, bw.getOvers()); ps.setObject(4, bw.getMaidens());
            ps.setObject(5, bw.getRuns()); ps.setObject(6, bw.getWickets());
            ps.setObject(7, bw.getEconomy()); ps.setObject(8, bw.getDots());
            ps.setObject(9, bw.getWides()); ps.setObject(10, bw.getNoBalls());
            return ps;
        }, kh);
        Long id = GeneratedKeyExtractor.extractId(kh);
        return id != null ? id : 0L;
    }

    public void update(BowlingScore bw) {
        jdbc.update("UPDATE bowling_scores SET player_id=?,overs=?,maidens=?,runs=?,wickets=?,economy=?,dots=?,wides=?,no_balls=? WHERE id=?",
            bw.getPlayerId(), bw.getOvers(), bw.getMaidens(), bw.getRuns(), bw.getWickets(),
            bw.getEconomy(), bw.getDots(), bw.getWides(), bw.getNoBalls(), bw.getId());
    }

    public void deleteById(Long id) { jdbc.update("DELETE FROM bowling_scores WHERE id = ?", id); }
}

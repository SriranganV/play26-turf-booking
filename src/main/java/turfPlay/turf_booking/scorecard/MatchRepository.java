package turfPlay.turf_booking.scorecard;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;
import java.util.Optional;

@Repository
public class MatchRepository {

    private final JdbcTemplate jdbc;

    // Full JOIN query resolves team names, tournament name, turf name from FKs
    private static final String SELECT_FULL = """
        SELECT m.id, m.tournament_id, m.match_number,
               m.team_a_id, m.team_b_id, m.turf_id,
               m.match_date, m.match_time, m.venue, m.overs,
               m.toss_winner, m.toss_decision, m.winner, m.man_of_match,
               m.match_stage, m.result, m.status, m.created_at,
               trn.tournament_name,
               ta.team_name AS team_a_name,
               tb.team_name AS team_b_name,
               tf.name AS turf_name,
               tw.team_name AS toss_winner_name,
               wt.team_name AS winner_name,
               wp.player_name AS mom_name
        FROM matches m
        LEFT JOIN tournaments trn ON m.tournament_id = trn.id
        LEFT JOIN teams ta  ON m.team_a_id = ta.id
        LEFT JOIN teams tb  ON m.team_b_id = tb.id
        LEFT JOIN turfs tf  ON m.turf_id = tf.id
        LEFT JOIN teams tw  ON m.toss_winner = tw.id
        LEFT JOIN teams wt  ON m.winner = wt.id
        LEFT JOIN players wp ON m.man_of_match = wp.id
        """;

    private final RowMapper<Match> rm = (rs, n) -> {
        Match m = new Match();
        m.setId(rs.getLong("id"));
        m.setTournamentId(rs.getObject("tournament_id", Long.class));
        m.setTournamentName(rs.getString("tournament_name"));
        m.setMatchNumber(rs.getString("match_number"));
        m.setTeamAId(rs.getObject("team_a_id", Long.class));
        m.setTeamAName(rs.getString("team_a_name"));
        m.setTeamBId(rs.getObject("team_b_id", Long.class));
        m.setTeamBName(rs.getString("team_b_name"));
        m.setTurfId(rs.getObject("turf_id", Long.class));
        m.setTurfName(rs.getString("turf_name"));
        Date md = rs.getDate("match_date"); if (md != null) m.setMatchDate(md.toLocalDate());
        Time mt = rs.getTime("match_time"); if (mt != null) m.setMatchTime(mt.toLocalTime());
        m.setVenue(rs.getString("venue"));
        m.setOvers(rs.getObject("overs", Integer.class));
        m.setTossWinnerId(rs.getObject("toss_winner", Long.class));
        m.setTossWinnerName(rs.getString("toss_winner_name"));
        m.setTossDecision(rs.getString("toss_decision"));
        m.setWinnerId(rs.getObject("winner", Long.class));
        m.setWinnerName(rs.getString("winner_name"));
        m.setManOfMatchId(rs.getObject("man_of_match", Long.class));
        m.setManOfMatchName(rs.getString("mom_name"));
        m.setMatchStage(rs.getString("match_stage"));
        m.setResult(rs.getString("result"));
        m.setStatus(rs.getString("status"));
        Timestamp ca = rs.getTimestamp("created_at"); if (ca != null) m.setCreatedAt(ca.toLocalDateTime());
        return m;
    };

    public MatchRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public List<Match> findAll() {
        return jdbc.query(SELECT_FULL + " ORDER BY m.match_date DESC, m.match_time DESC", rm);
    }

    public List<Match> findBySearch(String q) {
        String like = "%" + q + "%";
        return jdbc.query(SELECT_FULL + " WHERE m.match_number LIKE ? OR m.venue LIKE ? OR m.status LIKE ? OR trn.tournament_name LIKE ? ORDER BY m.match_date DESC", rm, like, like, like, like);
    }

    public List<Match> findByTournamentId(Long tid) {
        return jdbc.query(SELECT_FULL + " WHERE m.tournament_id=? ORDER BY m.match_date ASC", rm, tid);
    }

    public Optional<Match> findById(Long id) {
        return jdbc.query(SELECT_FULL + " WHERE m.id=?", rm, id).stream().findFirst();
    }

    public long save(Match m) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO matches(tournament_id,match_number,team_a_id,team_b_id,turf_id,match_date,match_time,venue,overs,toss_winner,toss_decision,winner,man_of_match,match_stage,result,status) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, m.getTournamentId()); ps.setString(2, m.getMatchNumber());
            ps.setObject(3, m.getTeamAId()); ps.setObject(4, m.getTeamBId());
            ps.setObject(5, m.getTurfId()); ps.setObject(6, m.getMatchDate());
            ps.setObject(7, m.getMatchTime()); ps.setString(8, m.getVenue());
            ps.setObject(9, m.getOvers()); ps.setObject(10, m.getTossWinnerId());
            ps.setString(11, m.getTossDecision()); ps.setObject(12, m.getWinnerId());
            ps.setObject(13, m.getManOfMatchId()); ps.setString(14, m.getMatchStage());
            ps.setString(15, m.getResult()); ps.setString(16, m.getStatus() == null ? "UPCOMING" : m.getStatus());
            return ps;
        }, kh);
        return kh.getKey().longValue();
    }

    public void update(Match m) {
        jdbc.update("UPDATE matches SET tournament_id=?,match_number=?,team_a_id=?,team_b_id=?,turf_id=?,match_date=?,match_time=?,venue=?,overs=?,toss_winner=?,toss_decision=?,winner=?,man_of_match=?,match_stage=?,result=?,status=? WHERE id=?",
            m.getTournamentId(), m.getMatchNumber(), m.getTeamAId(), m.getTeamBId(), m.getTurfId(),
            m.getMatchDate(), m.getMatchTime(), m.getVenue(), m.getOvers(),
            m.getTossWinnerId(), m.getTossDecision(), m.getWinnerId(), m.getManOfMatchId(),
            m.getMatchStage(), m.getResult(), m.getStatus(), m.getId());
    }

    public void deleteById(Long id) { jdbc.update("DELETE FROM matches WHERE id=?", id); }
    public int countAll() { Integer c = jdbc.queryForObject("SELECT COUNT(*) FROM matches", Integer.class); return c == null ? 0 : c; }
    public int countByStatus(String s) { Integer c = jdbc.queryForObject("SELECT COUNT(*) FROM matches WHERE status=?", Integer.class, s); return c == null ? 0 : c; }
}
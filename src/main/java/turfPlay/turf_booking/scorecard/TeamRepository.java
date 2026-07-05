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
public class TeamRepository {
    private final JdbcTemplate jdbc;

    private final RowMapper<Team> rm = (rs, n) -> {
        Team t = new Team();
        t.setId(rs.getLong("id"));
        t.setTournamentId(rs.getObject("tournament_id", Long.class));
        t.setTournamentName(rs.getString("tournament_name"));
        t.setTeamName(rs.getString("team_name"));
        t.setShortName(rs.getString("short_name"));
        t.setTeamCode(rs.getString("team_code"));
        t.setLogo(rs.getString("logo"));
        t.setCity(rs.getString("city"));
        t.setDescription(rs.getString("description"));
        t.setCoachName(rs.getString("coach_name"));
        t.setCaptainId(rs.getObject("captain_id", Long.class));
        t.setViceCaptainId(rs.getObject("vice_captain_id", Long.class));
        t.setHomeGround(rs.getString("home_ground"));
        t.setMatchesPlayed(rs.getObject("matches_played", Integer.class));
        t.setWins(rs.getObject("wins", Integer.class));
        t.setLosses(rs.getObject("losses", Integer.class));
        t.setTies(rs.getObject("ties", Integer.class));
        t.setNoResult(rs.getObject("no_result", Integer.class));
        t.setPoints(rs.getObject("points", Integer.class));
        t.setNetRunRate(rs.getBigDecimal("net_run_rate"));
        t.setTotalRuns(rs.getObject("total_runs", Integer.class));
        t.setTotalWickets(rs.getObject("total_wickets", Integer.class));
        t.setStatus(rs.getString("status"));
        Timestamp ca = rs.getTimestamp("created_at"); if (ca != null) t.setCreatedAt(ca.toLocalDateTime());
        return t;
    };

    public TeamRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private static final String SEL = "SELECT t.*, trn.tournament_name FROM teams t LEFT JOIN tournaments trn ON t.tournament_id=trn.id ";

    public List<Team> findAll() { return jdbc.query(SEL + "ORDER BY t.team_name", rm); }
    public List<Team> findByTournament(Long tid) { return jdbc.query(SEL + "WHERE t.tournament_id=? ORDER BY t.points DESC", rm, tid); }
    public Optional<Team> findById(Long id) { return jdbc.query(SEL + "WHERE t.id=?", rm, id).stream().findFirst(); }

    public long save(Team t) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO teams(tournament_id,team_name,short_name,team_code,logo,city,description,coach_name,home_ground,status) VALUES(?,?,?,?,?,?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
            
            if (t.getTournamentId() != null) ps.setLong(1, t.getTournamentId()); else ps.setNull(1, Types.BIGINT);
            ps.setString(2, t.getTeamName());
            ps.setString(3, t.getShortName());
            ps.setString(4, t.getTeamCode());
            ps.setString(5, t.getLogo());
            ps.setString(6, t.getCity());
            ps.setString(7, t.getDescription());
            ps.setString(8, t.getCoachName());
            ps.setString(9, t.getHomeGround());
            ps.setString(10, t.getStatus() == null ? "ACTIVE" : t.getStatus());
            
            return ps;
        }, kh);
        Long id = GeneratedKeyExtractor.extractId(kh);
        return id != null ? id : 0L;
    }

    public void update(Team t) {
        jdbc.update("UPDATE teams SET tournament_id=?,team_name=?,short_name=?,team_code=?,logo=?,city=?,description=?,coach_name=?,home_ground=?,status=? WHERE id=?",
            t.getTournamentId(),t.getTeamName(),t.getShortName(),t.getTeamCode(),t.getLogo(),
            t.getCity(),t.getDescription(),t.getCoachName(),t.getHomeGround(),t.getStatus(),t.getId());
    }
    public void deleteById(Long id) { jdbc.update("DELETE FROM teams WHERE id=?", id); }
    public int countAll() { Integer c = jdbc.queryForObject("SELECT COUNT(*) FROM teams", Integer.class); return c==null?0:c; }
}
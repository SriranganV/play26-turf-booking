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
public class TournamentRepository {

    private final JdbcTemplate jdbc;

    private final RowMapper<Tournament> rm = (rs, n) -> {
        Tournament t = new Tournament();
        t.setId(rs.getLong("id"));
        t.setTournamentName(rs.getString("tournament_name"));
        t.setDescription(rs.getString("description"));
        t.setTournamentType(rs.getString("tournament_type"));
        t.setMatchType(rs.getString("match_type"));
        t.setOvers(rs.getObject("overs", Integer.class));
        t.setBallType(rs.getString("ball_type"));
        t.setEntryFee(rs.getBigDecimal("entry_fee"));
        t.setPrizePool(rs.getBigDecimal("prize_pool"));
        t.setMaximumTeams(rs.getObject("maximum_teams", Integer.class));
        t.setRegisteredTeams(rs.getObject("registered_teams", Integer.class));
        t.setVenue(rs.getString("venue"));
        Date sd = rs.getDate("start_date"); if (sd != null) t.setStartDate(sd.toLocalDate());
        Date ed = rs.getDate("end_date"); if (ed != null) t.setEndDate(ed.toLocalDate());
        t.setBanner(rs.getString("banner"));
        t.setRules(rs.getString("rules"));
        t.setStatus(rs.getString("status"));
        t.setCreatedBy(rs.getObject("created_by", Long.class));
        Timestamp ca = rs.getTimestamp("created_at"); if (ca != null) t.setCreatedAt(ca.toLocalDateTime());
        return t;
    };

    public TournamentRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public List<Tournament> findAll() {
        return jdbc.query("SELECT * FROM tournaments ORDER BY created_at DESC", rm);
    }

    public List<Tournament> findBySearch(String q) {
        String like = "%" + q + "%";
        return jdbc.query("SELECT * FROM tournaments WHERE tournament_name LIKE ? OR venue LIKE ? OR status LIKE ? ORDER BY created_at DESC", rm, like, like, like);
    }

    public Optional<Tournament> findById(Long id) {
        return jdbc.query("SELECT * FROM tournaments WHERE id=?", rm, id).stream().findFirst();
    }

    public long save(Tournament t) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO tournaments(tournament_name,description,tournament_type,match_type,overs,ball_type,entry_fee,prize_pool,maximum_teams,registered_teams,venue,start_date,end_date,banner,rules,status) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, t.getTournamentName()); ps.setString(2, t.getDescription());
            ps.setString(3, t.getTournamentType()); ps.setString(4, t.getMatchType());
            ps.setObject(5, t.getOvers()); ps.setString(6, t.getBallType());
            ps.setBigDecimal(7, t.getEntryFee()); ps.setBigDecimal(8, t.getPrizePool());
            ps.setObject(9, t.getMaximumTeams()); ps.setObject(10, t.getRegisteredTeams() == null ? 0 : t.getRegisteredTeams());
            ps.setString(11, t.getVenue()); ps.setObject(12, t.getStartDate());
            ps.setObject(13, t.getEndDate()); ps.setString(14, t.getBanner());
            ps.setString(15, t.getRules()); ps.setString(16, t.getStatus() == null ? "UPCOMING" : t.getStatus());
            return ps;
        }, kh);
        return kh.getKey().longValue();
    }

    public void update(Tournament t) {
        jdbc.update("UPDATE tournaments SET tournament_name=?,description=?,tournament_type=?,match_type=?,overs=?,ball_type=?,entry_fee=?,prize_pool=?,maximum_teams=?,registered_teams=?,venue=?,start_date=?,end_date=?,banner=?,rules=?,status=? WHERE id=?",
            t.getTournamentName(), t.getDescription(), t.getTournamentType(), t.getMatchType(),
            t.getOvers(), t.getBallType(), t.getEntryFee(), t.getPrizePool(),
            t.getMaximumTeams(), t.getRegisteredTeams(), t.getVenue(),
            t.getStartDate(), t.getEndDate(), t.getBanner(), t.getRules(), t.getStatus(), t.getId());
    }

    public void deleteById(Long id) { jdbc.update("DELETE FROM tournaments WHERE id=?", id); }

    public int countAll() { Integer c = jdbc.queryForObject("SELECT COUNT(*) FROM tournaments", Integer.class); return c == null ? 0 : c; }
    public int countByStatus(String s) { Integer c = jdbc.queryForObject("SELECT COUNT(*) FROM tournaments WHERE status=?", Integer.class, s); return c == null ? 0 : c; }
}
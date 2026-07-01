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
public class PlayerRepository {
    private final JdbcTemplate jdbc;

    private final RowMapper<Player> rm = (rs, n) -> {
        Player p = new Player();
        p.setId(rs.getLong("id"));
        p.setTournamentId(rs.getObject("tournament_id", Long.class));
        p.setTournamentName(rs.getString("tournament_name"));
        p.setTeamId(rs.getObject("team_id", Long.class));
        p.setTeamName(rs.getString("team_name"));
        p.setPlayerName(rs.getString("player_name"));
        p.setJerseyNumber(rs.getObject("jersey_number", Integer.class));
        p.setPhoto(rs.getString("photo"));
        p.setRole(rs.getString("role"));
        p.setBattingStyle(rs.getString("batting_style"));
        p.setBowlingStyle(rs.getString("bowling_style"));
        p.setWicketKeeper(rs.getBoolean("wicket_keeper"));
        p.setCaptain(rs.getBoolean("captain"));
        p.setViceCaptain(rs.getBoolean("vice_captain"));
        Date dob = rs.getDate("date_of_birth"); if (dob != null) p.setDateOfBirth(dob.toLocalDate());
        p.setAge(rs.getObject("age", Integer.class));
        p.setNationality(rs.getString("nationality"));
        p.setPhone(rs.getString("phone"));
        p.setEmail(rs.getString("email"));
        p.setStatus(rs.getString("status"));
        Timestamp ca = rs.getTimestamp("created_at"); if (ca != null) p.setCreatedAt(ca.toLocalDateTime());
        return p;
    };

    public PlayerRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private static final String SEL = """
        SELECT p.*, trn.tournament_name, t.team_name
        FROM players p
        LEFT JOIN tournaments trn ON p.tournament_id=trn.id
        LEFT JOIN teams t ON p.team_id=t.id
        """;

    public List<Player> findAll() { return jdbc.query(SEL + "ORDER BY p.player_name", rm); }
    public List<Player> findByTeam(Long tid) { return jdbc.query(SEL + "WHERE p.team_id=? ORDER BY p.jersey_number", rm, tid); }
    public List<Player> findByTournament(Long tid) { return jdbc.query(SEL + "WHERE p.tournament_id=? ORDER BY p.player_name", rm, tid); }
    public Optional<Player> findById(Long id) { return jdbc.query(SEL + "WHERE p.id=?", rm, id).stream().findFirst(); }

    public long save(Player p) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO players(tournament_id,team_id,player_name,jersey_number,photo,role,batting_style,bowling_style,wicket_keeper,captain,vice_captain,date_of_birth,age,nationality,phone,email,status) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1,p.getTournamentId()); ps.setObject(2,p.getTeamId());
            ps.setString(3,p.getPlayerName()); ps.setObject(4,p.getJerseyNumber());
            ps.setString(5,p.getPhoto()); ps.setString(6,p.getRole());
            ps.setString(7,p.getBattingStyle()); ps.setString(8,p.getBowlingStyle());
            ps.setBoolean(9,p.isWicketKeeper()); ps.setBoolean(10,p.isCaptain());
            ps.setBoolean(11,p.isViceCaptain()); ps.setObject(12,p.getDateOfBirth());
            ps.setObject(13,p.getAge()); ps.setString(14,p.getNationality());
            ps.setString(15,p.getPhone()); ps.setString(16,p.getEmail());
            ps.setString(17,p.getStatus()==null?"ACTIVE":p.getStatus());
            return ps;
        }, kh);
        return kh.getKey().longValue();
    }

    public void update(Player p) {
        jdbc.update("UPDATE players SET tournament_id=?,team_id=?,player_name=?,jersey_number=?,photo=?,role=?,batting_style=?,bowling_style=?,wicket_keeper=?,captain=?,vice_captain=?,date_of_birth=?,age=?,nationality=?,phone=?,email=?,status=? WHERE id=?",
            p.getTournamentId(),p.getTeamId(),p.getPlayerName(),p.getJerseyNumber(),p.getPhoto(),
            p.getRole(),p.getBattingStyle(),p.getBowlingStyle(),p.isWicketKeeper(),p.isCaptain(),
            p.isViceCaptain(),p.getDateOfBirth(),p.getAge(),p.getNationality(),p.getPhone(),p.getEmail(),p.getStatus(),p.getId());
    }
    public void deleteById(Long id) { jdbc.update("DELETE FROM players WHERE id=?", id); }
    public int countAll() { Integer c = jdbc.queryForObject("SELECT COUNT(*) FROM players", Integer.class); return c==null?0:c; }
}
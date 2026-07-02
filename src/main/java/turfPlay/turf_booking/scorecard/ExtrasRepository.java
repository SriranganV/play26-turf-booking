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
public class ExtrasRepository {

    private final JdbcTemplate jdbc;

    private final RowMapper<Extras> rm = (rs, n) -> {
        Extras e = new Extras();
        e.setId(rs.getLong("id"));
        e.setScorecardId(rs.getLong("scorecard_id"));
        e.setWides(rs.getObject("wides", Integer.class));
        e.setNoBalls(rs.getObject("no_balls", Integer.class));
        e.setByes(rs.getObject("byes", Integer.class));
        e.setLegByes(rs.getObject("leg_byes", Integer.class));
        e.setPenalty(rs.getObject("penalty", Integer.class));
        e.setTotal(rs.getObject("total", Integer.class));
        Timestamp ca = rs.getTimestamp("created_at"); if (ca != null) e.setCreatedAt(ca.toLocalDateTime());
        return e;
    };

    public ExtrasRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public Optional<Extras> findByScorecardId(Long scorecardId) {
        return jdbc.query("SELECT * FROM extras WHERE scorecard_id = ?", rm, scorecardId).stream().findFirst();
    }

    public Optional<Extras> findById(Long id) {
        return jdbc.query("SELECT * FROM extras WHERE id = ?", rm, id).stream().findFirst();
    }

    public long save(Extras e) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO extras(scorecard_id,wides,no_balls,byes,leg_byes,penalty,total) VALUES(?,?,?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, e.getScorecardId()); ps.setObject(2, e.getWides());
            ps.setObject(3, e.getNoBalls()); ps.setObject(4, e.getByes());
            ps.setObject(5, e.getLegByes()); ps.setObject(6, e.getPenalty());
            ps.setObject(7, e.getTotal());
            return ps;
        }, kh);
        Long id = GeneratedKeyExtractor.extractId(kh);
        return id != null ? id : 0L;
    }

    public void update(Extras e) {
        jdbc.update("UPDATE extras SET wides=?,no_balls=?,byes=?,leg_byes=?,penalty=?,total=? WHERE id=?",
            e.getWides(), e.getNoBalls(), e.getByes(), e.getLegByes(), e.getPenalty(), e.getTotal(), e.getId());
    }

    public void deleteById(Long id) { jdbc.update("DELETE FROM extras WHERE id = ?", id); }
}

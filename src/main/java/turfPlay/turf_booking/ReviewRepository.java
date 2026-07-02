package turfPlay.turf_booking;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ReviewRepository {

    private final JdbcTemplate jdbcTemplate;

    public ReviewRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Review> reviewRowMapper = (rs, rowNum) -> {
        Review review = new Review();
        review.setId(rs.getLong("id"));
        review.setUserId(rs.getLong("user_id"));
        review.setTurfId(rs.getLong("turf_id"));
        review.setRating(rs.getInt("rating"));
        review.setComment(rs.getString("comment"));
        if (rs.getTimestamp("created_at") != null) {
            review.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        
        // We will join users table to get the user's name
        try {
            review.setUserName(rs.getString("user_name"));
        } catch (Exception e) {
            // Ignore if column doesn't exist in result set
        }
        
        return review;
    };

    public void save(Review review) {
        String sql = "INSERT INTO reviews (user_id, turf_id, rating, comment) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, review.getUserId(), review.getTurfId(), review.getRating(), review.getComment());
    }

    public List<Review> findByTurfId(Long turfId) {
        String sql = "SELECT r.*, u.full_name as user_name FROM reviews r JOIN users u ON r.user_id = u.id WHERE r.turf_id = ? ORDER BY r.created_at DESC";
        return jdbcTemplate.query(sql, reviewRowMapper, turfId);
    }

    public Double getAverageRating(Long turfId) {
        String sql = "SELECT AVG(rating) FROM reviews WHERE turf_id = ?";
        return jdbcTemplate.queryForObject(sql, Double.class, turfId);
    }
    
    public Integer getReviewCount(Long turfId) {
        String sql = "SELECT COUNT(*) FROM reviews WHERE turf_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, turfId);
    }
}

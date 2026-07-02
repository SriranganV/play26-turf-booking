package turfPlay.turf_booking;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public void addReview(Long userId, Long turfId, int rating, String comment) {
        Review review = new Review(userId, turfId, rating, comment);
        reviewRepository.save(review);
    }

    public List<Review> getReviewsForTurf(Long turfId) {
        return reviewRepository.findByTurfId(turfId);
    }

    public Double getAverageRating(Long turfId) {
        Double avg = reviewRepository.getAverageRating(turfId);
        return avg != null ? avg : 0.0;
    }
    
    public Integer getReviewCount(Long turfId) {
        Integer count = reviewRepository.getReviewCount(turfId);
        return count != null ? count : 0;
    }
}

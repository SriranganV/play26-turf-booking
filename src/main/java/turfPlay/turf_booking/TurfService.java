package turfPlay.turf_booking;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TurfService {

    private final TurfRepository turfRepository;
    private final ReviewService reviewService;

    public TurfService(TurfRepository turfRepository, ReviewService reviewService) {
        this.turfRepository = turfRepository;
        this.reviewService = reviewService;
    }

    private void populateReviews(Turf turf) {
        if (turf != null) {
            turf.setAverageRating(reviewService.getAverageRating(turf.getId()));
            turf.setReviewCount(reviewService.getReviewCount(turf.getId()));
        }
    }

    public List<Turf> getAllTurfs() {
        List<Turf> turfs = turfRepository.findAll();
        turfs.forEach(this::populateReviews);
        return turfs;
    }

    public List<Turf> getActiveTurfs() {
        List<Turf> turfs = turfRepository.findAllActive();
        turfs.forEach(this::populateReviews);
        return turfs;
    }

    public Optional<Turf> getTurfById(Long id) {
        Optional<Turf> turf = turfRepository.findById(id);
        turf.ifPresent(this::populateReviews);
        return turf;
    }

    public void saveTurf(Turf turf) {
        turf.setActive(true);
        turfRepository.save(turf);
    }

    public void updateTurf(Turf turf) {
        turfRepository.update(turf);
    }

    public void deactivateTurf(Long id) {
        turfRepository.deactivateById(id);
    }

    public void deleteTurf(Long id) {
        turfRepository.deleteById(id);
    }

    public int countAll() {
        return turfRepository.countAll();
    }
}

package turfPlay.turf_booking.scorecard;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class BowlingScoreService {
    private final BowlingScoreRepository repo;
    public BowlingScoreService(BowlingScoreRepository repo) { this.repo = repo; }

    public List<BowlingScore> getByScorecardId(Long scorecardId) { return repo.findByScorecardId(scorecardId); }
    public Optional<BowlingScore> getById(Long id) { return repo.findById(id); }

    public long save(BowlingScore bw) {
        calculateEconomy(bw);
        return repo.save(bw);
    }

    public void update(BowlingScore bw) {
        calculateEconomy(bw);
        repo.update(bw);
    }

    public void delete(Long id) { repo.deleteById(id); }

    private void calculateEconomy(BowlingScore bw) {
        if (bw.getOvers() != null && bw.getOvers() > 0 && bw.getRuns() != null) {
            bw.setEconomy(Math.round((bw.getRuns() / bw.getOvers()) * 100.0) / 100.0);
        } else {
            bw.setEconomy(0.0);
        }
    }
}

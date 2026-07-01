package turfPlay.turf_booking.scorecard;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class BattingScoreService {
    private final BattingScoreRepository repo;
    public BattingScoreService(BattingScoreRepository repo) { this.repo = repo; }

    public List<BattingScore> getByScorecardId(Long scorecardId) { return repo.findByScorecardId(scorecardId); }
    public Optional<BattingScore> getById(Long id) { return repo.findById(id); }

    public long save(BattingScore bs) {
        calculateStrikeRate(bs);
        return repo.save(bs);
    }

    public void update(BattingScore bs) {
        calculateStrikeRate(bs);
        repo.update(bs);
    }

    public void delete(Long id) { repo.deleteById(id); }
    public int countByScorecardId(Long scorecardId) { return repo.countByScorecardId(scorecardId); }
    public Integer sumRunsByScorecardId(Long scorecardId) { return repo.sumRunsByScorecardId(scorecardId); }
    public Integer countWicketsByScorecardId(Long scorecardId) { return repo.countWicketsByScorecardId(scorecardId); }

    private void calculateStrikeRate(BattingScore bs) {
        if (bs.getBalls() != null && bs.getBalls() > 0 && bs.getRuns() != null) {
            bs.setStrikeRate(Math.round((bs.getRuns() * 100.0) / bs.getBalls() * 100.0) / 100.0);
        } else {
            bs.setStrikeRate(0.0);
        }
    }
}

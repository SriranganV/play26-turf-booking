package turfPlay.turf_booking.scorecard;

import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ExtrasService {
    private final ExtrasRepository repo;
    public ExtrasService(ExtrasRepository repo) { this.repo = repo; }

    public Optional<Extras> getByScorecardId(Long scorecardId) { return repo.findByScorecardId(scorecardId); }
    public Optional<Extras> getById(Long id) { return repo.findById(id); }

    public long save(Extras e) {
        calculateTotal(e);
        return repo.save(e);
    }

    public void update(Extras e) {
        calculateTotal(e);
        repo.update(e);
    }

    public void delete(Long id) { repo.deleteById(id); }

    private void calculateTotal(Extras e) {
        int w = e.getWides() != null ? e.getWides() : 0;
        int nb = e.getNoBalls() != null ? e.getNoBalls() : 0;
        int b = e.getByes() != null ? e.getByes() : 0;
        int lb = e.getLegByes() != null ? e.getLegByes() : 0;
        int p = e.getPenalty() != null ? e.getPenalty() : 0;
        e.setTotal(w + nb + b + lb + p);
    }
}

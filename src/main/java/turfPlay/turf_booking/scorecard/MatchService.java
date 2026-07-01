package turfPlay.turf_booking.scorecard;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class MatchService {
    private final MatchRepository repo;
    public MatchService(MatchRepository repo) { this.repo = repo; }
    public List<Match> getAll() { return repo.findAll(); }
    public List<Match> search(String q) { return (q == null || q.isBlank()) ? repo.findAll() : repo.findBySearch(q.trim()); }
    public List<Match> getByTournament(Long tid) { return repo.findByTournamentId(tid); }
    public Optional<Match> getById(Long id) { return repo.findById(id); }
    public long save(Match m) { return repo.save(m); }
    public void update(Match m) { repo.update(m); }
    public void delete(Long id) { repo.deleteById(id); }
    public int countAll() { return repo.countAll(); }
    public int countByStatus(String s) { return repo.countByStatus(s); }
}
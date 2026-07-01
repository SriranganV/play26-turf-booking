package turfPlay.turf_booking.scorecard;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TournamentService {
    private final TournamentRepository repo;
    public TournamentService(TournamentRepository repo) { this.repo = repo; }
    public List<Tournament> getAll() { return repo.findAll(); }
    public List<Tournament> search(String q) { return (q == null || q.isBlank()) ? repo.findAll() : repo.findBySearch(q.trim()); }
    public Optional<Tournament> getById(Long id) { return repo.findById(id); }
    public long save(Tournament t) { return repo.save(t); }
    public void update(Tournament t) { repo.update(t); }
    public void delete(Long id) { repo.deleteById(id); }
    public int countAll() { return repo.countAll(); }
    public int countByStatus(String s) { return repo.countByStatus(s); }
}
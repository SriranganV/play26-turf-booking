package turfPlay.turf_booking.scorecard;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TeamService {
    private final TeamRepository repo;
    public TeamService(TeamRepository repo) { this.repo = repo; }
    public List<Team> getAll() { return repo.findAll(); }
    public List<Team> getByTournament(Long tid) { return repo.findByTournament(tid); }
    public Optional<Team> getById(Long id) { return repo.findById(id); }
    public long save(Team t) { return repo.save(t); }
    public void update(Team t) { repo.update(t); }
    public void delete(Long id) { repo.deleteById(id); }
    public int countAll() { return repo.countAll(); }
}
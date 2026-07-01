package turfPlay.turf_booking.scorecard;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PlayerService {
    private final PlayerRepository repo;
    public PlayerService(PlayerRepository repo) { this.repo = repo; }
    public List<Player> getAll() { return repo.findAll(); }
    public List<Player> getByTeam(Long tid) { return repo.findByTeam(tid); }
    public List<Player> getByTournament(Long tid) { return repo.findByTournament(tid); }
    public Optional<Player> getById(Long id) { return repo.findById(id); }
    public long save(Player p) { return repo.save(p); }
    public void update(Player p) { repo.update(p); }
    public void delete(Long id) { repo.deleteById(id); }
    public int countAll() { return repo.countAll(); }
}
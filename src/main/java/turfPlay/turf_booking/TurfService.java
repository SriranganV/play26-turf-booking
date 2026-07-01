package turfPlay.turf_booking;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TurfService {

    private final TurfRepository turfRepository;

    public TurfService(TurfRepository turfRepository) {
        this.turfRepository = turfRepository;
    }

    public List<Turf> getAllTurfs() {
        return turfRepository.findAll();
    }

    public List<Turf> getActiveTurfs() {
        return turfRepository.findAllActive();
    }

    public Optional<Turf> getTurfById(Long id) {
        return turfRepository.findById(id);
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

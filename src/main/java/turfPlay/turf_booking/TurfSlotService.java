package turfPlay.turf_booking;


import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TurfSlotService {

    private final TurfSlotRepository turfSlotRepository;

    public TurfSlotService(TurfSlotRepository turfSlotRepository) {
        this.turfSlotRepository = turfSlotRepository;
    }

    public List<TurfSlot> getAllSlots() {
        return turfSlotRepository.findAll();
    }

    public List<TurfSlot> getSlotsByTurfId(Long turfId) {
        return turfSlotRepository.findByTurfId(turfId);
    }

    public Optional<TurfSlot> getSlotById(Long id) {
        return turfSlotRepository.findById(id);
    }

    public void saveSlot(TurfSlot slot) {
        if (slot.getStatus() == null || slot.getStatus().isBlank()) {
            slot.setStatus("AVAILABLE");
        }

        turfSlotRepository.save(slot);
    }

    public void markAvailable(Long id) {
        turfSlotRepository.updateStatus(id, "AVAILABLE");
    }

    public void markClosed(Long id) {
        turfSlotRepository.updateStatus(id, "CLOSED");
    }

    public void markBooked(Long id) {
        turfSlotRepository.updateStatus(id, "BOOKED");
    }

    public void deleteSlot(Long id) {
        turfSlotRepository.deleteById(id);
    }
    public List<TurfSlot> getAvailableSlotsByTurfId(Long turfId) {
        return turfSlotRepository.findAvailableByTurfId(turfId);
    }
}
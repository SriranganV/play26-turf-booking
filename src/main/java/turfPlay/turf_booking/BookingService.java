package turfPlay.turf_booking;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final TurfSlotService turfSlotService;

    public BookingService(BookingRepository bookingRepository,
                          UserRepository userRepository,
                          TurfSlotService turfSlotService) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.turfSlotService = turfSlotService;
    }
    
    

    
    @Transactional
    public void bookSlot(String email, Long slotId) {
        Long userId = userRepository.findIdByEmail(email);

        TurfSlot slot = turfSlotService.getSlotById(slotId)
                .orElseThrow(() -> new IllegalArgumentException("Slot not found"));

        if (!"AVAILABLE".equals(slot.getStatus())) {
            throw new IllegalStateException("Slot is not available");
        }

        bookingRepository.createBooking(userId, slotId);
        turfSlotService.markBooked(slotId);
    }

    public List<Booking> getBookingsForUser(String email) {
        Long userId = userRepository.findIdByEmail(email);
        return bookingRepository.findByUserId(userId);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public void cancelBooking(Long bookingId) {
        Long slotId = bookingRepository.findSlotIdByBookingId(bookingId);

        bookingRepository.cancelBooking(bookingId);

        turfSlotService.markAvailable(slotId);
    }
}

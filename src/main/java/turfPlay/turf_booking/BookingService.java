package turfPlay.turf_booking;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final TurfSlotService turfSlotService;
    private final SplitContributionRepository contributionRepository;
    private final TurfService turfService;

    public BookingService(BookingRepository bookingRepository,
                          UserRepository userRepository,
                          TurfSlotService turfSlotService,
                          SplitContributionRepository contributionRepository,
                          TurfService turfService) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.turfSlotService = turfSlotService;
        this.contributionRepository = contributionRepository;
        this.turfService = turfService;
    }
    
    

    
    @Transactional
    public Long bookSlot(String email, Long slotId, String paymentType) {
        Long userId = userRepository.findIdByEmail(email);

        TurfSlot slot = turfSlotService.getSlotById(slotId)
                .orElseThrow(() -> new IllegalArgumentException("Slot not found"));

        if (!"AVAILABLE".equals(slot.getStatus())) {
            throw new IllegalStateException("Slot is not available");
        }
        
        Turf turf = turfService.getTurfById(slot.getTurfId())
                .orElseThrow(() -> new IllegalArgumentException("Turf not found"));
        
        BigDecimal totalPrice = turf.getPricePerHour();
        
        Long bookingId;
        if ("SPLIT".equals(paymentType)) {
            String uuid = UUID.randomUUID().toString();
            bookingId = bookingRepository.createBooking(userId, slotId, totalPrice, "SPLIT", uuid, "PENDING_FUNDS");
        } else {
            bookingId = bookingRepository.createBooking(userId, slotId, totalPrice, "FULL", null, "CONFIRMED");
        }
        
        turfSlotService.markBooked(slotId);
        
        return bookingId;
    }
    
    @Transactional
    public void addContribution(String uuid, String contributorName, BigDecimal amount) {
        Booking booking = bookingRepository.findBySplitLinkUuid(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Invalid split link"));
                
        if (!"PENDING_FUNDS".equals(booking.getBookingStatus())) {
            throw new IllegalStateException("This booking is already fully funded or cancelled.");
        }
        
        contributionRepository.addContribution(booking.getId(), contributorName, amount);
        bookingRepository.incrementAmountPaid(booking.getId(), amount);
        
        BigDecimal newAmountPaid = booking.getAmountPaid() != null ? booking.getAmountPaid().add(amount) : amount;
        
        if (newAmountPaid.compareTo(booking.getTotalPrice()) >= 0) {
            bookingRepository.updateBookingStatus(booking.getId(), "CONFIRMED");
        }
    }

    public java.util.Optional<Booking> getBookingBySplitUuid(String uuid) {
        return bookingRepository.findBySplitLinkUuid(uuid);
    }
    
    public List<SplitContribution> getContributions(Long bookingId) {
        return contributionRepository.findByBookingId(bookingId);
    }

    public java.util.Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
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

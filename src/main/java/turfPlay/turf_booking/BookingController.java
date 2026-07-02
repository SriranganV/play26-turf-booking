package turfPlay.turf_booking;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.util.List;

import java.security.Principal;

@Controller
public class BookingController {

    private final BookingService bookingService;
    private final TurfSlotService turfSlotService;
    private final TurfService turfService;

    public BookingController(BookingService bookingService,
                             TurfSlotService turfSlotService,
                             TurfService turfService) {
        this.bookingService = bookingService;
        this.turfSlotService = turfSlotService;
        this.turfService = turfService;
    }

    /**
     * Show Checkout Review page.
     */
    @GetMapping("/bookings/checkout/{slotId}")
    public String checkout(@PathVariable Long slotId, Model model) {
        TurfSlot slot = turfSlotService.getSlotById(slotId)
                .orElseThrow(() -> new IllegalArgumentException("Slot not found."));
        
        Turf turf = turfService.getTurfById(slot.getTurfId())
                .orElseThrow(() -> new IllegalArgumentException("Turf not found."));

        model.addAttribute("pageTitle", "Checkout");
        model.addAttribute("slot", slot);
        model.addAttribute("turf", turf);
        return "bookings/checkout";
    }

    /**
     * Show Receipt page.
     */
    @GetMapping("/bookings/receipt/{bookingId}")
    public String receipt(@PathVariable Long bookingId, Model model) {
        Booking booking = bookingService.getBookingById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found."));
        
        model.addAttribute("pageTitle", "Booking Receipt");
        model.addAttribute("booking", booking);
        return "bookings/receipt";
    }

    /**
     * Book a slot.
     * Looks up the turf the slot belongs to and redirects back to
     * that turf's detail page with a success/error flash message.
     * This keeps the user in context so they can see the slot is now gone.
     */
    @PostMapping("/bookings/create/{slotId}")
    public String createBooking(@PathVariable Long slotId,
                                @RequestParam(name = "paymentType", defaultValue = "FULL") String paymentType,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {

        Long turfId = null;

        try {
            // Resolve the slot first so we know which turf to redirect to (in case of error)
            TurfSlot slot = turfSlotService.getSlotById(slotId)
                    .orElseThrow(() -> new IllegalArgumentException("Slot not found."));

            turfId = slot.getTurfId();

            Long bookingId = bookingService.bookSlot(principal.getName(), slotId, paymentType);
            
            if ("SPLIT".equals(paymentType)) {
                redirectAttributes.addFlashAttribute("successMessage",
                        "✅ Split booking created! Share the link on the receipt to collect funds.");
            } else {
                redirectAttributes.addFlashAttribute("successMessage",
                        "✅ Slot booked successfully! Here is your receipt.");
            }
                    
            return "redirect:/bookings/receipt/" + bookingId;

        } catch (IllegalStateException e) {
            // Slot was just taken by someone else
            redirectAttributes.addFlashAttribute("errorMessage",
                    "⚠️ " + e.getMessage() + " — someone else may have just booked it.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "⚠️ Booking failed: " + e.getMessage());
        }

        // Return user to the turf detail page they came from on error
        if (turfId != null) {
            return "redirect:/turfs/" + turfId;
        }

        // Fallback if slot lookup itself failed
        return "redirect:/turfs";
    }

    /**
     * Cancel a booking and restore the slot to AVAILABLE.
     */
    @PostMapping("/bookings/cancel/{bookingId}")
    public String cancelBooking(@PathVariable Long bookingId,
                                RedirectAttributes redirectAttributes) {
        try {
            bookingService.cancelBooking(bookingId);
            redirectAttributes.addFlashAttribute("successMessage",
                    "✅ Booking cancelled. The slot is now available again.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "⚠️ Could not cancel booking: " + e.getMessage());
        }
        return "redirect:/bookings/my";
    }

    /**
     * My Bookings page.
     */
    @GetMapping("/bookings/my")
    public String myBookings(Model model, Principal principal) {
        model.addAttribute("pageTitle", "My Bookings");
        model.addAttribute("bookings",
                bookingService.getBookingsForUser(principal.getName()));
        return "bookings/my";
    }

    /**
     * Public split payment page.
     */
    @GetMapping("/bookings/split/{uuid}")
    public String splitPaymentPage(@PathVariable String uuid, Model model) {
        Booking booking = bookingService.getBookingBySplitUuid(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Invalid split link."));
        
        List<SplitContribution> contributions = bookingService.getContributions(booking.getId());
        
        model.addAttribute("pageTitle", "Split Payment - PLAY26");
        model.addAttribute("booking", booking);
        model.addAttribute("contributions", contributions);
        return "bookings/split-payment";
    }
    
    /**
     * Public split payment contribution.
     */
    @PostMapping("/bookings/split/{uuid}/contribute")
    public String contribute(@PathVariable String uuid,
                             @RequestParam String contributorName,
                             @RequestParam BigDecimal amount,
                             RedirectAttributes redirectAttributes) {
        try {
            bookingService.addContribution(uuid, contributorName, amount);
            redirectAttributes.addFlashAttribute("successMessage", "✅ Payment successful! Thank you.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "⚠️ Could not process payment: " + e.getMessage());
        }
        return "redirect:/bookings/split/" + uuid;
    }
}

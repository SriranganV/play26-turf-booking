package turfPlay.turf_booking;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class BookingController {

    private final BookingService bookingService;
    private final TurfSlotService turfSlotService;

    public BookingController(BookingService bookingService,
                             TurfSlotService turfSlotService) {
        this.bookingService = bookingService;
        this.turfSlotService = turfSlotService;
    }

    /**
     * Book a slot.
     * Looks up the turf the slot belongs to and redirects back to
     * that turf's detail page with a success/error flash message.
     * This keeps the user in context so they can see the slot is now gone.
     */
    @PostMapping("/bookings/create/{slotId}")
    public String createBooking(@PathVariable Long slotId,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {

        Long turfId = null;

        try {
            // Resolve the slot first so we know which turf to redirect to
            TurfSlot slot = turfSlotService.getSlotById(slotId)
                    .orElseThrow(() -> new IllegalArgumentException("Slot not found."));

            turfId = slot.getTurfId();

            bookingService.bookSlot(principal.getName(), slotId);

            redirectAttributes.addFlashAttribute("successMessage",
                    "✅ Slot booked successfully! You can manage it in My Bookings.");

        } catch (IllegalStateException e) {
            // Slot was just taken by someone else
            redirectAttributes.addFlashAttribute("errorMessage",
                    "⚠️ " + e.getMessage() + " — someone else may have just booked it.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "⚠️ Booking failed: " + e.getMessage());
        }

        // Return user to the turf detail page they came from
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
}

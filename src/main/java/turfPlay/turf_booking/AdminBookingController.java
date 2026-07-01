package turfPlay.turf_booking;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminBookingController {

    private final BookingService bookingService;

    public AdminBookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/admin/bookings")
    public String adminBookings(Model model) {
        model.addAttribute("pageTitle", "All Bookings");
        model.addAttribute("bookings", bookingService.getAllBookings());
        return "admin/bookings/list";
    }
    @PostMapping("/admin/bookings/cancel/{bookingId}")
    public String adminCancelBooking(@PathVariable Long bookingId,
                                     RedirectAttributes redirectAttributes) {
        bookingService.cancelBooking(bookingId);
        redirectAttributes.addFlashAttribute("successMessage", "Booking cancelled successfully.");
        return "redirect:/admin/bookings";
    }
}

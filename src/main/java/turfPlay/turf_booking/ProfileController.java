package turfPlay.turf_booking;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class ProfileController {

    private final UserRepository userRepository;
    private final BookingService bookingService;

    public ProfileController(UserRepository userRepository, BookingService bookingService) {
        this.userRepository = userRepository;
        this.bookingService = bookingService;
    }

    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        AppUser user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Booking> userBookings = bookingService.getBookingsForUser(user.getEmail());
        
        long upcomingCount = userBookings.stream()
                .filter(b -> "CONFIRMED".equals(b.getBookingStatus()) || "PENDING_FUNDS".equals(b.getBookingStatus()))
                .count();
                
        long pastCount = userBookings.stream()
                .filter(b -> "COMPLETED".equals(b.getBookingStatus()))
                .count();

        model.addAttribute("pageTitle", "My Profile - PLAY26");
        model.addAttribute("user", user);
        model.addAttribute("bookings", userBookings);
        model.addAttribute("totalBookings", userBookings.size());
        model.addAttribute("upcomingCount", upcomingCount);
        model.addAttribute("pastCount", pastCount);

        return "profile";
    }
}

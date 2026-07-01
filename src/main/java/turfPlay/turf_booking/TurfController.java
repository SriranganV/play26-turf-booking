package turfPlay.turf_booking;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class TurfController {

    private final TurfService turfService;
    private final TurfSlotService turfSlotService;

    @Autowired
    public TurfController(TurfService turfService, TurfSlotService turfSlotService) {
        this.turfService = turfService;
        this.turfSlotService = turfSlotService;
    }

    @GetMapping("/turfs")
    public String listTurfs(Model model) {
        model.addAttribute("pageTitle", "Available Turfs");
        model.addAttribute("turfs", turfService.getActiveTurfs());
        return "turfs";
    }


    @GetMapping("/turfs/{id}")
    public String turfDetails(@PathVariable Long id,
                              Model model,
                              RedirectAttributes redirectAttributes) {

        System.out.println("Turf ID from URL = " + id);

        Turf turf = turfService.getTurfById(id).orElse(null);

        List<TurfSlot> slots = turfSlotService.getAvailableSlotsByTurfId(id);

        System.out.println("Slots found = " + slots.size());

        model.addAttribute("slots", slots);
        model.addAttribute("turf", turf);

        return "turf-details";
    }
}
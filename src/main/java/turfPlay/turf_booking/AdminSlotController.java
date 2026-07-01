package turfPlay.turf_booking;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminSlotController {

    private final TurfSlotService turfSlotService;
    private final TurfService turfService;

    public AdminSlotController(TurfSlotService turfSlotService, TurfService turfService) {
        this.turfSlotService = turfSlotService;
        this.turfService = turfService;
    }

    @GetMapping("/admin/slots")
    public String listSlots(Model model) {
        model.addAttribute("pageTitle", "Manage Slots");
        model.addAttribute("slots", turfSlotService.getAllSlots());
        return "admin/slots/list";
    }

    @GetMapping("/admin/slots/new")
    public String newSlotForm(Model model) {
        model.addAttribute("pageTitle", "Add Slot");
        model.addAttribute("slot", new TurfSlot());
        model.addAttribute("turfs", turfService.getActiveTurfs());
        return "admin/slots/form";
    }

    @PostMapping("/admin/slots/save")
    public String saveSlot(
            @ModelAttribute TurfSlot slot,
            RedirectAttributes redirectAttributes
    ) {
        turfSlotService.saveSlot(slot);
        redirectAttributes.addFlashAttribute("successMessage", "Slot added successfully.");
        return "redirect:/admin/slots";
    }

    @PostMapping("/admin/slots/available/{id}")
    public String markAvailable(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        turfSlotService.markAvailable(id);
        redirectAttributes.addFlashAttribute("successMessage", "Slot marked as available.");
        return "redirect:/admin/slots";
    }

    @PostMapping("/admin/slots/closed/{id}")
    public String markClosed(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        turfSlotService.markClosed(id);
        redirectAttributes.addFlashAttribute("successMessage", "Slot closed successfully.");
        return "redirect:/admin/slots";
    }

    @PostMapping("/admin/slots/delete/{id}")
    public String deleteSlot(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        turfSlotService.deleteSlot(id);
        redirectAttributes.addFlashAttribute("successMessage", "Slot deleted successfully.");
        return "redirect:/admin/slots";
    }
}

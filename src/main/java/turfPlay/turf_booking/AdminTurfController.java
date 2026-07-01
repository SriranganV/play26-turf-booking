package turfPlay.turf_booking;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminTurfController {

    private final TurfService turfService;

    public AdminTurfController(TurfService turfService) {
        this.turfService = turfService;
    }

    @GetMapping("/admin/turfs")
    public String listTurfs(Model model) {
        model.addAttribute("pageTitle", "Manage Turfs");
        model.addAttribute("turfs", turfService.getAllTurfs());
        return "admin/turfs/list";
    }

    @GetMapping("/admin/turfs/new")
    public String newTurfForm(Model model) {
        model.addAttribute("pageTitle", "Add Turf");
        model.addAttribute("turf", new Turf());
        model.addAttribute("formAction", "/admin/turfs/save");
        return "admin/turfs/form";
    }

    @PostMapping("/admin/turfs/save")
    public String saveTurf(
            @ModelAttribute Turf turf,
            RedirectAttributes redirectAttributes
    ) {
        turfService.saveTurf(turf);
        redirectAttributes.addFlashAttribute("successMessage", "Turf added successfully.");
        return "redirect:/admin/turfs";
    }

    @GetMapping("/admin/turfs/edit/{id}")
    public String editTurfForm(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        Turf turf = turfService.getTurfById(id).orElse(null);

        if (turf == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Turf not found.");
            return "redirect:/admin/turfs";
        }

        model.addAttribute("pageTitle", "Edit Turf");
        model.addAttribute("turf", turf);
        model.addAttribute("formAction", "/admin/turfs/update");
        return "admin/turfs/form";
    }

    @PostMapping("/admin/turfs/update")
    public String updateTurf(
            @ModelAttribute Turf turf,
            RedirectAttributes redirectAttributes
    ) {
        turfService.updateTurf(turf);
        redirectAttributes.addFlashAttribute("successMessage", "Turf updated successfully.");
        return "redirect:/admin/turfs";
    }

    @PostMapping("/admin/turfs/deactivate/{id}")
    public String deactivateTurf(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        turfService.deactivateTurf(id);
        redirectAttributes.addFlashAttribute("successMessage", "Turf deactivated successfully.");
        return "redirect:/admin/turfs";
    }

    @PostMapping("/admin/turfs/delete/{id}")
    public String deleteTurf(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        turfService.deleteTurf(id);
        redirectAttributes.addFlashAttribute("successMessage", "Turf deleted successfully.");
        return "redirect:/admin/turfs";
    }
}

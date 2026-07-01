package turfPlay.turf_booking;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminSportsRuleController {

    private final SportsRuleService sportsRuleService;

    public AdminSportsRuleController(SportsRuleService sportsRuleService) {
        this.sportsRuleService = sportsRuleService;
    }

    @GetMapping("/admin/rules")
    public String listRules(Model model) {
        model.addAttribute("pageTitle", "Manage Sports Rules");
        model.addAttribute("rules", sportsRuleService.getAllRules());
        return "admin/rules/list";
    }

    @GetMapping("/admin/rules/new")
    public String newRuleForm(Model model) {
        model.addAttribute("pageTitle", "Add Sports Rule");
        model.addAttribute("rule", new SportsRule());
        model.addAttribute("formAction", "/admin/rules/save");
        return "admin/rules/form";
    }

    @PostMapping("/admin/rules/save")
    public String saveRule(@ModelAttribute SportsRule rule,
                           RedirectAttributes redirectAttributes) {
        sportsRuleService.saveRule(rule);
        redirectAttributes.addFlashAttribute("successMessage", "Sports rule added successfully.");
        return "redirect:/admin/rules";
    }

    @GetMapping("/admin/rules/edit/{id}")
    public String editRuleForm(@PathVariable Long id,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        SportsRule rule = sportsRuleService.getRuleById(id).orElse(null);

        if (rule == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Rule not found.");
            return "redirect:/admin/rules";
        }

        model.addAttribute("pageTitle", "Edit Sports Rule");
        model.addAttribute("rule", rule);
        model.addAttribute("formAction", "/admin/rules/update");
        return "admin/rules/form";
    }

    @PostMapping("/admin/rules/update")
    public String updateRule(@ModelAttribute SportsRule rule,
                             RedirectAttributes redirectAttributes) {
        sportsRuleService.updateRule(rule);
        redirectAttributes.addFlashAttribute("successMessage", "Sports rule updated successfully.");
        return "redirect:/admin/rules";
    }

    @PostMapping("/admin/rules/delete/{id}")
    public String deleteRule(@PathVariable Long id,
                             RedirectAttributes redirectAttributes) {
        sportsRuleService.deleteRule(id);
        redirectAttributes.addFlashAttribute("successMessage", "Sports rule deleted successfully.");
        return "redirect:/admin/rules";
    }
}
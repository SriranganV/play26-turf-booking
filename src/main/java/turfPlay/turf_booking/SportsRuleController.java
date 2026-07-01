package turfPlay.turf_booking;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SportsRuleController {

    private final SportsRuleService sportsRuleService;

    public SportsRuleController(SportsRuleService sportsRuleService) {
        this.sportsRuleService = sportsRuleService;
    }

    @GetMapping("/rules")
    public String rules(@RequestParam(required = false) String sport,
                        Model model) {
        model.addAttribute("pageTitle", "Sports Rules");
        model.addAttribute("selectedSport", sport);

        if (sport == null || sport.isBlank()) {
            model.addAttribute("rules", sportsRuleService.getAllRules());
        } else {
            model.addAttribute("rules", sportsRuleService.getRulesBySportName(sport));
        }

        return "rules";
    }
}

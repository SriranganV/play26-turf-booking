package turfPlay.turf_booking.scorecard;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tournaments")
public class TournamentController {

    private final TournamentService svc;
    public TournamentController(TournamentService svc) { this.svc = svc; }

    @GetMapping
    public String list(@RequestParam(required=false) String q, Model m) {
        m.addAttribute("pageTitle","Tournaments");
        m.addAttribute("tournaments", svc.search(q));
        m.addAttribute("q", q);
        m.addAttribute("totalCount", svc.countAll());
        m.addAttribute("liveCount", svc.countByStatus("LIVE"));
        m.addAttribute("upcomingCount", svc.countByStatus("UPCOMING"));
        m.addAttribute("completedCount", svc.countByStatus("COMPLETED"));
        return "tournament/list";
    }

    @GetMapping("/new")
    public String newForm(Model m) {
        m.addAttribute("pageTitle","New Tournament");
        m.addAttribute("tournament", new Tournament());
        return "tournament/create";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Tournament t, RedirectAttributes ra) {
        long id = svc.save(t);
        ra.addFlashAttribute("successMessage","Tournament created successfully!");
        return "redirect:/tournaments/view/" + id;
    }

    @GetMapping("/view/{id}")
    public String view(@PathVariable Long id, Model m, RedirectAttributes ra) {
        Tournament t = svc.getById(id).orElse(null);
        if (t == null) { ra.addFlashAttribute("errorMessage","Tournament not found."); return "redirect:/tournaments"; }
        m.addAttribute("pageTitle", t.getTournamentName());
        m.addAttribute("tournament", t);
        return "tournament/view";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model m, RedirectAttributes ra) {
        Tournament t = svc.getById(id).orElse(null);
        if (t == null) { ra.addFlashAttribute("errorMessage","Tournament not found."); return "redirect:/tournaments"; }
        m.addAttribute("pageTitle","Edit Tournament");
        m.addAttribute("tournament", t);
        return "tournament/edit";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute Tournament t, RedirectAttributes ra) {
        svc.update(t);
        ra.addFlashAttribute("successMessage","Tournament updated!");
        return "redirect:/tournaments/view/" + t.getId();
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        svc.delete(id);
        ra.addFlashAttribute("successMessage","Tournament deleted.");
        return "redirect:/tournaments";
    }
}
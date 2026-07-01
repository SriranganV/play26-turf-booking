package turfPlay.turf_booking.scorecard;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/teams")
public class TeamController {
    private final TeamService teamSvc;
    private final TournamentService tournamentSvc;
    public TeamController(TeamService teamSvc, TournamentService tournamentSvc) {
        this.teamSvc = teamSvc; this.tournamentSvc = tournamentSvc;
    }
    @GetMapping public String list(Model m) {
        m.addAttribute("pageTitle","Teams"); m.addAttribute("teams", teamSvc.getAll());
        m.addAttribute("totalCount", teamSvc.countAll());
        return "team/list";
    }
    @GetMapping("/new") public String newForm(Model m) {
        m.addAttribute("pageTitle","New Team"); m.addAttribute("team", new Team());
        m.addAttribute("tournaments", tournamentSvc.getAll()); return "team/create";
    }
    @PostMapping("/save") public String save(@ModelAttribute Team t, RedirectAttributes ra) {
        long id = teamSvc.save(t); ra.addFlashAttribute("successMessage","Team created!"); return "redirect:/teams/view/"+id;
    }
    @GetMapping("/view/{id}") public String view(@PathVariable Long id, Model m, RedirectAttributes ra) {
        Team t = teamSvc.getById(id).orElse(null);
        if(t==null){ra.addFlashAttribute("errorMessage","Not found."); return "redirect:/teams";}
        m.addAttribute("pageTitle",t.getTeamName()); m.addAttribute("team",t); return "team/view";
    }
    @GetMapping("/edit/{id}") public String edit(@PathVariable Long id, Model m, RedirectAttributes ra) {
        Team t = teamSvc.getById(id).orElse(null);
        if(t==null){ra.addFlashAttribute("errorMessage","Not found."); return "redirect:/teams";}
        m.addAttribute("pageTitle","Edit Team"); m.addAttribute("team",t);
        m.addAttribute("tournaments", tournamentSvc.getAll()); return "team/edit";
    }
    @PostMapping("/update") public String update(@ModelAttribute Team t, RedirectAttributes ra) {
        teamSvc.update(t); ra.addFlashAttribute("successMessage","Team updated!"); return "redirect:/teams/view/"+t.getId();
    }
    @PostMapping("/delete/{id}") public String delete(@PathVariable Long id, RedirectAttributes ra) {
        teamSvc.delete(id); ra.addFlashAttribute("successMessage","Team deleted."); return "redirect:/teams";
    }
}
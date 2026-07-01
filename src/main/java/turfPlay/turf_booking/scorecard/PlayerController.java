package turfPlay.turf_booking.scorecard;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/players")
public class PlayerController {
    private final PlayerService playerSvc;
    private final TeamService teamSvc;
    private final TournamentService tournamentSvc;

    public PlayerController(PlayerService playerSvc, TeamService teamSvc, TournamentService tournamentSvc) {
        this.playerSvc = playerSvc; this.teamSvc = teamSvc; this.tournamentSvc = tournamentSvc;
    }

    @GetMapping public String list(Model m) {
        m.addAttribute("pageTitle","Players"); m.addAttribute("players", playerSvc.getAll());
        m.addAttribute("totalCount", playerSvc.countAll()); return "player/list";
    }
    @GetMapping("/new") public String newForm(Model m) {
        m.addAttribute("pageTitle","New Player"); m.addAttribute("player", new Player());
        m.addAttribute("teams", teamSvc.getAll()); m.addAttribute("tournaments", tournamentSvc.getAll());
        return "player/create";
    }
    @PostMapping("/save") public String save(@ModelAttribute Player p, RedirectAttributes ra) {
        long id = playerSvc.save(p); ra.addFlashAttribute("successMessage","Player created!"); return "redirect:/players/view/"+id;
    }
    @GetMapping("/view/{id}") public String view(@PathVariable Long id, Model m, RedirectAttributes ra) {
        Player p = playerSvc.getById(id).orElse(null);
        if(p==null){ra.addFlashAttribute("errorMessage","Not found."); return "redirect:/players";}
        m.addAttribute("pageTitle",p.getPlayerName()); m.addAttribute("player",p); return "player/view";
    }
    @GetMapping("/edit/{id}") public String edit(@PathVariable Long id, Model m, RedirectAttributes ra) {
        Player p = playerSvc.getById(id).orElse(null);
        if(p==null){ra.addFlashAttribute("errorMessage","Not found."); return "redirect:/players";}
        m.addAttribute("pageTitle","Edit Player"); m.addAttribute("player",p);
        m.addAttribute("teams", teamSvc.getAll()); m.addAttribute("tournaments", tournamentSvc.getAll());
        return "player/edit";
    }
    @PostMapping("/update") public String update(@ModelAttribute Player p, RedirectAttributes ra) {
        playerSvc.update(p); ra.addFlashAttribute("successMessage","Player updated!"); return "redirect:/players/view/"+p.getId();
    }
    @PostMapping("/delete/{id}") public String delete(@PathVariable Long id, RedirectAttributes ra) {
        playerSvc.delete(id); ra.addFlashAttribute("successMessage","Player deleted."); return "redirect:/players";
    }
}
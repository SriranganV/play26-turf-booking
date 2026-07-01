package turfPlay.turf_booking.scorecard;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import turfPlay.turf_booking.TurfService;

@Controller
@RequestMapping("/matches")
public class MatchController {

    private final MatchService matchSvc;
    private final TournamentService tournamentSvc;
    private final TeamService teamSvc;
    private final TurfService turfSvc;
    private final PlayerService playerSvc;

    public MatchController(MatchService matchSvc, TournamentService tournamentSvc,
                           TeamService teamSvc, TurfService turfSvc, PlayerService playerSvc) {
        this.matchSvc = matchSvc; this.tournamentSvc = tournamentSvc;
        this.teamSvc = teamSvc; this.turfSvc = turfSvc; this.playerSvc = playerSvc;
    }

    @GetMapping
    public String list(@RequestParam(required=false) String q, Model m) {
        m.addAttribute("pageTitle","Matches");
        m.addAttribute("matches", matchSvc.search(q));
        m.addAttribute("q", q);
        m.addAttribute("totalCount", matchSvc.countAll());
        m.addAttribute("liveCount", matchSvc.countByStatus("LIVE"));
        m.addAttribute("upcomingCount", matchSvc.countByStatus("UPCOMING"));
        m.addAttribute("completedCount", matchSvc.countByStatus("COMPLETED"));
        return "match/list";
    }

    @GetMapping("/new")
    public String newForm(Model m) {
        m.addAttribute("pageTitle","New Match");
        m.addAttribute("match", new Match());
        m.addAttribute("tournaments", tournamentSvc.getAll());
        m.addAttribute("teams", teamSvc.getAll());
        m.addAttribute("turfs", turfSvc.getAllTurfs());
    //    m.addAttribute("players", playerSvc.getAllPlayers());
        return "match/create";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Match match, RedirectAttributes ra) {
        long id = matchSvc.save(match);
        ra.addFlashAttribute("successMessage","Match created!");
        return "redirect:/matches/view/" + id;
    }

    @GetMapping("/view/{id}")
    public String view(@PathVariable Long id, Model m, RedirectAttributes ra) {
        Match match = matchSvc.getById(id).orElse(null);
        if (match == null) { ra.addFlashAttribute("errorMessage","Match not found."); return "redirect:/matches"; }
        m.addAttribute("pageTitle", match.getTeamAName() + " vs " + match.getTeamBName());
        m.addAttribute("match", match);
        return "match/view";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model m, RedirectAttributes ra) {
        Match match = matchSvc.getById(id).orElse(null);
        if (match == null) { ra.addFlashAttribute("errorMessage","Match not found."); return "redirect:/matches"; }
        m.addAttribute("pageTitle","Edit Match");
        m.addAttribute("match", match);
        m.addAttribute("tournaments", tournamentSvc.getAll());
        m.addAttribute("teams", teamSvc.getAll());
        m.addAttribute("turfs", turfSvc.getAllTurfs());
   //     m.addAttribute("players", playerSvc.getAllPlayers());
        return "match/edit";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute Match match, RedirectAttributes ra) {
        matchSvc.update(match);
        ra.addFlashAttribute("successMessage","Match updated!");
        return "redirect:/matches/view/" + match.getId();
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        matchSvc.delete(id);
        ra.addFlashAttribute("successMessage","Match deleted.");
        return "redirect:/matches";
    }
}
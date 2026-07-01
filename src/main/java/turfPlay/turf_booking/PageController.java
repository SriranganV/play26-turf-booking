package turfPlay.turf_booking;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import turfPlay.turf_booking.scorecard.TournamentService;
import turfPlay.turf_booking.scorecard.MatchService;
import turfPlay.turf_booking.scorecard.TeamService;
import turfPlay.turf_booking.scorecard.PlayerService;

@Controller
public class PageController {

    private final TurfService turfService;
    private final TournamentService tournamentService;
    private final MatchService matchService;
    private final TeamService teamService;
    private final PlayerService playerService;
    private final GlobalSportsService globalSportsService;

    public PageController(TurfService turfService, TournamentService tournamentService,
                          MatchService matchService, TeamService teamService,
                          PlayerService playerService, GlobalSportsService globalSportsService) {
        this.turfService = turfService;
        this.tournamentService = tournamentService;
        this.matchService = matchService;
        this.teamService = teamService;
        this.playerService = playerService;
        this.globalSportsService = globalSportsService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("pageTitle", "Sports Turf Booking");
        return "index";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("pageTitle", "About");
        return "about";
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("pageTitle", "Contact");
        return "contact";
    }

    @GetMapping("/sports")
    public String sports(Model model) {
        model.addAttribute("pageTitle", "Sports");
        return "sports";
    }

    @GetMapping("/blogs")
    public String blogs(Model model) {
        model.addAttribute("pageTitle", "Global Sports Hub");
        
        // Fetch News
        model.addAttribute("latestNews", globalSportsService.fetchLatestNews());
        return "blogs";
    }

    @GetMapping("/leaderboard")
    public String leaderboard(Model model) {
        model.addAttribute("pageTitle", "Global Leaderboards");
        
        // Fetch Football Standings (Cached in Service)
        model.addAttribute("premierLeague", globalSportsService.fetchFootballStandings("PL"));
        model.addAttribute("worldCup", globalSportsService.fetchFootballStandings("WC"));
        model.addAttribute("championsLeague", globalSportsService.fetchFootballStandings("CL"));
        model.addAttribute("bundesliga", globalSportsService.fetchFootballStandings("BL1"));
        model.addAttribute("ligue1", globalSportsService.fetchFootballStandings("FL1"));
        model.addAttribute("serieA", globalSportsService.fetchFootballStandings("SA"));
        
        // Fetch Cricket Standings
        model.addAttribute("iplStandings", globalSportsService.fetchIplStandings());
        
        return "leaderboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", "Dashboard");
        model.addAttribute("totalTurfs", turfService.countAll());
        model.addAttribute("totalTournaments", tournamentService.countAll());
        model.addAttribute("totalMatches", matchService.countAll());
        model.addAttribute("totalTeams", teamService.countAll());
        model.addAttribute("totalPlayers", playerService.countAll());
        model.addAttribute("liveMatches", matchService.countByStatus("LIVE"));
        model.addAttribute("completedMatches", matchService.countByStatus("COMPLETED"));
        model.addAttribute("upcomingMatches", matchService.countByStatus("UPCOMING"));
        return "dashboard";
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        model.addAttribute("pageTitle", "Admin Dashboard");
        return "admin/dashboard";
    }
}

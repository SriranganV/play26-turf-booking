package turfPlay.turf_booking.scorecard;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import turfPlay.turf_booking.TurfService;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/scorecard")
public class ScorecardController {

    private final ScorecardService scorecardService;
    private final BattingScoreService battingService;
    private final BowlingScoreService bowlingService;
    private final ExtrasService extrasService;
    private final MatchService matchService;
    private final TeamService teamService;
    private final PlayerService playerService;
    private final TournamentService tournamentService;
    private final TurfService turfService;

    public ScorecardController(ScorecardService scorecardService, BattingScoreService battingService,
                               BowlingScoreService bowlingService, ExtrasService extrasService,
                               MatchService matchService, TeamService teamService,
                               PlayerService playerService, TournamentService tournamentService,
                               TurfService turfService) {
        this.scorecardService = scorecardService;
        this.battingService = battingService;
        this.bowlingService = bowlingService;
        this.extrasService = extrasService;
        this.matchService = matchService;
        this.teamService = teamService;
        this.playerService = playerService;
        this.tournamentService = tournamentService;
        this.turfService = turfService;
    }

    // ── Dashboard ──────────────────────────────────────────────────
    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", "Scorecard Dashboard");
        model.addAttribute("totalTournaments", tournamentService.countAll());
        model.addAttribute("totalMatches", matchService.countAll());
        model.addAttribute("totalTeams", teamService.countAll());
        model.addAttribute("totalPlayers", playerService.countAll());
        model.addAttribute("liveMatches", matchService.countByStatus("LIVE"));
        model.addAttribute("completedMatches", matchService.countByStatus("COMPLETED"));
        model.addAttribute("upcomingMatches", matchService.countByStatus("UPCOMING"));
        model.addAttribute("liveTournaments", tournamentService.countByStatus("LIVE"));

        List<Match> allMatches = matchService.getAll();
        List<Match> recentMatches = allMatches.stream().limit(10).collect(Collectors.toList());
        model.addAttribute("recentMatches", recentMatches);

        return "scorecard/dashboard";
    }

    // ── Quick Match ────────────────────────────────────────────────
    @GetMapping("/quick-match")
    public String quickMatchForm(Model model) {
        model.addAttribute("pageTitle", "Start Quick Match");
        model.addAttribute("quickMatch", new QuickMatchDTO());
        return "scorecard/quick-match";
    }

    @PostMapping("/quick-match/save")
    public String saveQuickMatch(@ModelAttribute QuickMatchDTO dto, RedirectAttributes ra) {
        Team teamA = new Team();
        teamA.setTeamName(dto.getTeamAName());
        teamA.setShortName(dto.getTeamAName().substring(0, Math.min(3, dto.getTeamAName().length())).toUpperCase());
        teamA.setStatus("ACTIVE");
        long teamAId = teamService.save(teamA);

        Team teamB = new Team();
        teamB.setTeamName(dto.getTeamBName());
        teamB.setShortName(dto.getTeamBName().substring(0, Math.min(3, dto.getTeamBName().length())).toUpperCase());
        teamB.setStatus("ACTIVE");
        long teamBId = teamService.save(teamB);

        Match match = new Match();
        match.setTeamAId(teamAId);
        match.setTeamAName(dto.getTeamAName());
        match.setTeamBId(teamBId);
        match.setTeamBName(dto.getTeamBName());
        match.setOvers(dto.getOvers());
        match.setTotalPlayers(dto.getTotalPlayers() != null ? dto.getTotalPlayers() : 11);
        match.setVenue(dto.getVenue());
        match.setMatchDate(dto.getMatchDate());
        match.setMatchTime(dto.getMatchTime());
        
        if ("TEAM_A".equals(dto.getTossWinner())) {
            match.setTossWinnerId(teamAId);
            match.setTossWinnerName(dto.getTeamAName());
        } else if ("TEAM_B".equals(dto.getTossWinner())) {
            match.setTossWinnerId(teamBId);
            match.setTossWinnerName(dto.getTeamBName());
        }
        match.setTossDecision(dto.getTossDecision());
        match.setStatus("LIVE");
        match.setMatchStage("LEAGUE");
        
        long matchId = matchService.save(match);
        ra.addFlashAttribute("successMessage", "Quick Match created successfully!");
        return "redirect:/scorecard/match/" + matchId;
    }

    // ── Match Summary (full scorecard view) ────────────────────────
    @GetMapping("/match/{matchId}")
    public String matchSummary(@PathVariable Long matchId, Model model, RedirectAttributes ra) {
        Match match = matchService.getById(matchId).orElse(null);
        if (match == null) { ra.addFlashAttribute("errorMessage", "Match not found."); return "redirect:/scorecard"; }

        List<Scorecard> scorecards = scorecardService.getByMatchId(matchId);
        Map<Long, List<BattingScore>> battingMap = new HashMap<>();
        Map<Long, List<BowlingScore>> bowlingMap = new HashMap<>();
        Map<Long, Extras> extrasMap = new HashMap<>();

        for (Scorecard sc : scorecards) {
            battingMap.put(sc.getId(), battingService.getByScorecardId(sc.getId()));
            bowlingMap.put(sc.getId(), bowlingService.getByScorecardId(sc.getId()));
            extrasService.getByScorecardId(sc.getId()).ifPresent(e -> extrasMap.put(sc.getId(), e));
        }

        model.addAttribute("pageTitle", match.getTeamAName() + " vs " + match.getTeamBName());
        model.addAttribute("match", match);
        model.addAttribute("scorecards", scorecards);
        model.addAttribute("battingScores", battingMap);
        model.addAttribute("bowlingScores", bowlingMap);
        model.addAttribute("extrasMap", extrasMap);
        return "scorecard/summary";
    }

    @GetMapping("/match/{matchId}/start")
    public String startMatchScorecard(@PathVariable Long matchId) {
        return "redirect:/scorecard/match/" + matchId + "/batting/1";
    }

    // ── Batting Entry ──────────────────────────────────────────────
    @GetMapping("/match/{matchId}/batting/{innings}")
    public String battingForm(@PathVariable Long matchId, @PathVariable int innings, Model model, RedirectAttributes ra) {
        Match match = matchService.getById(matchId).orElse(null);
        if (match == null) { ra.addFlashAttribute("errorMessage", "Match not found."); return "redirect:/scorecard"; }

        Long battingTeamId = (innings == 1) ? match.getTeamAId() : match.getTeamBId();
        Long bowlingTeamId = (innings == 1) ? match.getTeamBId() : match.getTeamAId();
        Scorecard scorecard = scorecardService.getOrCreateScorecard(matchId, innings, battingTeamId, bowlingTeamId);

        List<BattingScore> battingScores = battingService.getByScorecardId(scorecard.getId());
        List<Player> battingPlayers = playerService.getByTeam(battingTeamId);
        List<Player> bowlingPlayers = playerService.getByTeam(bowlingTeamId);

        model.addAttribute("pageTitle", "Batting - Innings " + innings);
        model.addAttribute("match", match);
        model.addAttribute("scorecard", scorecard);
        model.addAttribute("battingScores", battingScores);
        BattingScore newScore = new BattingScore();
        newScore.setScorecardId(scorecard.getId());
        model.addAttribute("battingScore", newScore);
        model.addAttribute("players", battingPlayers);
        model.addAttribute("bowlers", bowlingPlayers);
        model.addAttribute("innings", innings);
        return "scorecard/batting";
    }

    @PostMapping("/match/{matchId}/batting/save")
    public String saveBatting(@PathVariable Long matchId, @ModelAttribute BattingScore battingScore,
                              @RequestParam int innings, @RequestParam(required = false) String newPlayerName, RedirectAttributes ra) {
        if (battingScore.getScorecardId() == null) {
            Match match = matchService.getById(matchId).orElse(null);
            if (match != null) {
                Long battingTeamId = (innings == 1) ? match.getTeamAId() : match.getTeamBId();
                Long bowlingTeamId = (innings == 1) ? match.getTeamBId() : match.getTeamAId();
                Scorecard scorecard = scorecardService.getOrCreateScorecard(matchId, innings, battingTeamId, bowlingTeamId);
                battingScore.setScorecardId(scorecard.getId());
            }
        }
        if (newPlayerName != null && !newPlayerName.trim().isEmpty()) {
            Match match = matchService.getById(matchId).orElse(null);
            if (match != null) {
                Long battingTeamId = (innings == 1) ? match.getTeamAId() : match.getTeamBId();
                Player p = new Player();
                p.setTeamId(battingTeamId);
                p.setPlayerName(newPlayerName.trim());
                p.setTournamentId(match.getTournamentId());
                p.setStatus("ACTIVE");
                long pid = playerService.save(p);
                battingScore.setPlayerId(pid);
                battingScore.setPlayerName(newPlayerName.trim());
            }
        }
        battingService.save(battingScore);
        scorecardService.recalculateTotals(battingScore.getScorecardId());
        ra.addFlashAttribute("successMessage", "Batting score saved!");
        return "redirect:/scorecard/match/" + matchId + "/batting/" + innings;
    }

    @PostMapping("/match/{matchId}/batting/delete/{id}")
    public String deleteBatting(@PathVariable Long matchId, @PathVariable Long id,
                                @RequestParam int innings, @RequestParam Long scorecardId, RedirectAttributes ra) {
        battingService.delete(id);
        scorecardService.recalculateTotals(scorecardId);
        ra.addFlashAttribute("successMessage", "Batting entry deleted!");
        return "redirect:/scorecard/match/" + matchId + "/batting/" + innings;
    }

    // ── Bowling Entry ──────────────────────────────────────────────
    @GetMapping("/match/{matchId}/bowling/{innings}")
    public String bowlingForm(@PathVariable Long matchId, @PathVariable int innings, Model model, RedirectAttributes ra) {
        Match match = matchService.getById(matchId).orElse(null);
        if (match == null) { ra.addFlashAttribute("errorMessage", "Match not found."); return "redirect:/scorecard"; }

        Long battingTeamId = (innings == 1) ? match.getTeamAId() : match.getTeamBId();
        Long bowlingTeamId = (innings == 1) ? match.getTeamBId() : match.getTeamAId();
        Scorecard scorecard = scorecardService.getOrCreateScorecard(matchId, innings, battingTeamId, bowlingTeamId);

        List<BowlingScore> bowlingScores = bowlingService.getByScorecardId(scorecard.getId());
        List<Player> bowlingPlayers = playerService.getByTeam(bowlingTeamId);

        model.addAttribute("pageTitle", "Bowling - Innings " + innings);
        model.addAttribute("match", match);
        model.addAttribute("scorecard", scorecard);
        model.addAttribute("bowlingScores", bowlingScores);
        BowlingScore newScore = new BowlingScore();
        newScore.setScorecardId(scorecard.getId());
        model.addAttribute("bowlingScore", newScore);
        model.addAttribute("players", bowlingPlayers);
        model.addAttribute("innings", innings);
        return "scorecard/bowling";
    }

    @PostMapping("/match/{matchId}/bowling/save")
    public String saveBowling(@PathVariable Long matchId, @ModelAttribute BowlingScore bowlingScore,
                              @RequestParam int innings, @RequestParam(required = false) String newPlayerName, RedirectAttributes ra) {
        if (bowlingScore.getScorecardId() == null) {
            Match match = matchService.getById(matchId).orElse(null);
            if (match != null) {
                Long battingTeamId = (innings == 1) ? match.getTeamAId() : match.getTeamBId();
                Long bowlingTeamId = (innings == 1) ? match.getTeamBId() : match.getTeamAId();
                Scorecard scorecard = scorecardService.getOrCreateScorecard(matchId, innings, battingTeamId, bowlingTeamId);
                bowlingScore.setScorecardId(scorecard.getId());
            }
        }
        if (newPlayerName != null && !newPlayerName.trim().isEmpty()) {
            Match match = matchService.getById(matchId).orElse(null);
            if (match != null) {
                Long bowlingTeamId = (innings == 1) ? match.getTeamBId() : match.getTeamAId();
                Player p = new Player();
                p.setTeamId(bowlingTeamId);
                p.setPlayerName(newPlayerName.trim());
                p.setTournamentId(match.getTournamentId());
                p.setStatus("ACTIVE");
                long pid = playerService.save(p);
                bowlingScore.setPlayerId(pid);
                bowlingScore.setPlayerName(newPlayerName.trim());
            }
        }
        bowlingService.save(bowlingScore);
        ra.addFlashAttribute("successMessage", "Bowling score saved!");
        return "redirect:/scorecard/match/" + matchId + "/bowling/" + innings;
    }

    @PostMapping("/match/{matchId}/bowling/delete/{id}")
    public String deleteBowling(@PathVariable Long matchId, @PathVariable Long id,
                                @RequestParam int innings, RedirectAttributes ra) {
        bowlingService.delete(id);
        ra.addFlashAttribute("successMessage", "Bowling entry deleted!");
        return "redirect:/scorecard/match/" + matchId + "/bowling/" + innings;
    }

    // ── Extras Entry ───────────────────────────────────────────────
    @GetMapping("/match/{matchId}/extras/{innings}")
    public String extrasForm(@PathVariable Long matchId, @PathVariable int innings, Model model, RedirectAttributes ra) {
        Match match = matchService.getById(matchId).orElse(null);
        if (match == null) { ra.addFlashAttribute("errorMessage", "Match not found."); return "redirect:/scorecard"; }

        Long battingTeamId = (innings == 1) ? match.getTeamAId() : match.getTeamBId();
        Long bowlingTeamId = (innings == 1) ? match.getTeamBId() : match.getTeamAId();
        Scorecard scorecard = scorecardService.getOrCreateScorecard(matchId, innings, battingTeamId, bowlingTeamId);

        Extras extras = extrasService.getByScorecardId(scorecard.getId()).orElseGet(() -> {
            Extras e = new Extras();
            e.setScorecardId(scorecard.getId());
            e.setWides(0); e.setNoBalls(0); e.setByes(0); e.setLegByes(0); e.setPenalty(0); e.setTotal(0);
            return e;
        });

        model.addAttribute("pageTitle", "Extras - Innings " + innings);
        model.addAttribute("match", match);
        model.addAttribute("scorecard", scorecard);
        model.addAttribute("extras", extras);
        model.addAttribute("innings", innings);
        return "scorecard/extras";
    }

    @PostMapping("/match/{matchId}/extras/save")
    public String saveExtras(@PathVariable Long matchId, @ModelAttribute Extras extras,
                             @RequestParam int innings, RedirectAttributes ra) {
        if (extras.getScorecardId() == null) {
            Match match = matchService.getById(matchId).orElse(null);
            if (match != null) {
                Long battingTeamId = (innings == 1) ? match.getTeamAId() : match.getTeamBId();
                Long bowlingTeamId = (innings == 1) ? match.getTeamBId() : match.getTeamAId();
                Scorecard scorecard = scorecardService.getOrCreateScorecard(matchId, innings, battingTeamId, bowlingTeamId);
                extras.setScorecardId(scorecard.getId());
            }
        }
        if (extras.getId() != null && extras.getId() > 0) {
            extrasService.update(extras);
        } else {
            extrasService.save(extras);
        }
        scorecardService.recalculateTotals(extras.getScorecardId());
        ra.addFlashAttribute("successMessage", "Extras saved!");
        return "redirect:/scorecard/match/" + matchId + "/extras/" + innings;
    }

    // ── Result ─────────────────────────────────────────────────────
    @GetMapping("/match/{matchId}/result")
    public String result(@PathVariable Long matchId, Model model, RedirectAttributes ra) {
        Match match = matchService.getById(matchId).orElse(null);
        if (match == null) { ra.addFlashAttribute("errorMessage", "Match not found."); return "redirect:/scorecard"; }

        List<Scorecard> scorecards = scorecardService.getByMatchId(matchId);
        Map<Long, List<BattingScore>> battingMap = new HashMap<>();
        Map<Long, List<BowlingScore>> bowlingMap = new HashMap<>();
        Map<Long, Extras> extrasMap = new HashMap<>();

        for (Scorecard sc : scorecards) {
            battingMap.put(sc.getId(), battingService.getByScorecardId(sc.getId()));
            bowlingMap.put(sc.getId(), bowlingService.getByScorecardId(sc.getId()));
            extrasService.getByScorecardId(sc.getId()).ifPresent(e -> extrasMap.put(sc.getId(), e));
        }

        model.addAttribute("pageTitle", "Match Result");
        model.addAttribute("match", match);
        model.addAttribute("scorecards", scorecards);
        model.addAttribute("battingScores", battingMap);
        model.addAttribute("bowlingScores", bowlingMap);
        model.addAttribute("extrasMap", extrasMap);
        model.addAttribute("resultText", scorecardService.calculateResult(matchId));

        // Basic Analysis
        int totalBoundaries = 0;
        int totalWicketsFall = 0;
        int maxRuns = -1;
        String topScorer = "N/A";
        int maxWickets = -1;
        String topBowler = "N/A";
        int bowlerConceded = 0;

        for (Scorecard sc : scorecards) {
            List<BattingScore> bsList = battingMap.get(sc.getId());
            if (bsList != null) {
                for (BattingScore bs : bsList) {
                    totalBoundaries += (bs.getFours() != null ? bs.getFours() : 0) + (bs.getSixes() != null ? bs.getSixes() : 0);
                    int r = bs.getRuns() != null ? bs.getRuns() : 0;
                    if (r > maxRuns) { maxRuns = r; topScorer = bs.getPlayerName(); }
                }
            }
            List<BowlingScore> bwList = bowlingMap.get(sc.getId());
            if (bwList != null) {
                for (BowlingScore bw : bwList) {
                    int w = bw.getWickets() != null ? bw.getWickets() : 0;
                    int r = bw.getRuns() != null ? bw.getRuns() : 0;
                    if (w > maxWickets || (w == maxWickets && r < bowlerConceded)) { 
                        maxWickets = w; topBowler = bw.getPlayerName(); bowlerConceded = r; 
                    }
                }
            }
            totalWicketsFall += (sc.getTotalWickets() != null ? sc.getTotalWickets() : 0);
        }

        model.addAttribute("totalBoundaries", totalBoundaries);
        model.addAttribute("totalWicketsFall", totalWicketsFall);
        model.addAttribute("topScorerName", topScorer);
        model.addAttribute("topScorerRuns", maxRuns >= 0 ? maxRuns : 0);
        model.addAttribute("topBowlerName", topBowler);
        model.addAttribute("topBowlerWickets", maxWickets >= 0 ? maxWickets : 0);
        model.addAttribute("topBowlerRuns", bowlerConceded);

        return "scorecard/result";
    }

    @PostMapping("/match/{matchId}/complete")
    public String completeMatch(@PathVariable Long matchId, RedirectAttributes ra) {
        scorecardService.completeMatch(matchId);
        ra.addFlashAttribute("successMessage", "Match completed!");
        return "redirect:/scorecard/match/" + matchId + "/result";
    }

    // ── Live View ──────────────────────────────────────────────────
    @GetMapping("/live/{matchId}")
    public String liveView(@PathVariable Long matchId, Model model, RedirectAttributes ra) {
        Match match = matchService.getById(matchId).orElse(null);
        if (match == null) { ra.addFlashAttribute("errorMessage", "Match not found."); return "redirect:/scorecard"; }

        List<Scorecard> scorecards = scorecardService.getByMatchId(matchId);
        Map<Long, List<BattingScore>> battingMap = new HashMap<>();
        Map<Long, List<BowlingScore>> bowlingMap = new HashMap<>();
        Map<Long, Extras> extrasMap = new HashMap<>();

        for (Scorecard sc : scorecards) {
            battingMap.put(sc.getId(), battingService.getByScorecardId(sc.getId()));
            bowlingMap.put(sc.getId(), bowlingService.getByScorecardId(sc.getId()));
            extrasService.getByScorecardId(sc.getId()).ifPresent(e -> extrasMap.put(sc.getId(), e));
        }

        model.addAttribute("pageTitle", "LIVE - " + match.getTeamAName() + " vs " + match.getTeamBName());
        model.addAttribute("match", match);
        model.addAttribute("scorecards", scorecards);
        model.addAttribute("battingScores", battingMap);
        model.addAttribute("bowlingScores", bowlingMap);
        model.addAttribute("extrasMap", extrasMap);
        return "scorecard/live";
    }
}
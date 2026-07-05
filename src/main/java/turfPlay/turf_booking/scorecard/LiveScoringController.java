package turfPlay.turf_booking.scorecard;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/scorecard/live")
public class LiveScoringController {

    private final ScorecardService scorecardService;
    private final BattingScoreService battingService;
    private final BowlingScoreService bowlingService;
    private final ExtrasService extrasService;
    private final MatchService matchService;
    private final PlayerService playerService;

    public LiveScoringController(ScorecardService scorecardService, BattingScoreService battingService,
                                 BowlingScoreService bowlingService, ExtrasService extrasService,
                                 MatchService matchService, PlayerService playerService) {
        this.scorecardService = scorecardService;
        this.battingService = battingService;
        this.bowlingService = bowlingService;
        this.extrasService = extrasService;
        this.matchService = matchService;
        this.playerService = playerService;
    }

    @GetMapping("/{matchId}/innings/{innings}")
    public String liveScoringUI(@PathVariable Long matchId, @PathVariable int innings, Model model) {
        Match match = matchService.getById(matchId).orElse(null);
        if (match == null) return "redirect:/scorecard";

        Long battingTeamId = (innings == 1) ? match.getTeamAId() : match.getTeamBId();
        Long bowlingTeamId = (innings == 1) ? match.getTeamBId() : match.getTeamAId();
        Scorecard scorecard = scorecardService.getOrCreateScorecard(matchId, innings, battingTeamId, bowlingTeamId);

        model.addAttribute("pageTitle", "Live Scoring");
        model.addAttribute("match", match);
        model.addAttribute("scorecard", scorecard);
        model.addAttribute("innings", innings);
        
        // Calculate Run Rate
        if (scorecard.getTotalOvers() != null && scorecard.getTotalOvers() > 0) {
            double overs = scorecard.getTotalOvers();
            int fullOvers = (int) overs;
            int balls = (int) Math.round((overs - fullOvers) * 10);
            double totalOversCalc = fullOvers + (balls / 6.0);
            double runRate = scorecard.getTotalRuns() / totalOversCalc;
            model.addAttribute("runRate", String.format("%.2f", runRate));
        } else {
            model.addAttribute("runRate", "0.00");
        }

        // Calculate Target Info for 2nd Innings
        if (innings == 2 && scorecard.getTarget() != null) {
            int runsNeeded = scorecard.getTarget() - (scorecard.getTotalRuns() != null ? scorecard.getTotalRuns() : 0);
            double overs = scorecard.getTotalOvers() != null ? scorecard.getTotalOvers() : 0.0;
            int fullOvers = (int) overs;
            int balls = (int) Math.round((overs - fullOvers) * 10);
            int ballsBowled = (fullOvers * 6) + balls;
            int totalMatchBalls = (match.getOvers() != null ? match.getOvers() : 20) * 6;
            int ballsRemaining = totalMatchBalls - ballsBowled;
            
            model.addAttribute("runsNeeded", Math.max(0, runsNeeded));
            model.addAttribute("ballsRemaining", Math.max(0, ballsRemaining));
        }
        
        // Load all batting and bowling scores for the dropdowns
        model.addAttribute("battingScores", battingService.getByScorecardId(scorecard.getId()));
        model.addAttribute("bowlingScores", bowlingService.getByScorecardId(scorecard.getId()));
        
        // Load team players to allow creating new score entries from the live UI
        model.addAttribute("battingPlayers", playerService.getByTeam(battingTeamId));
        model.addAttribute("bowlingPlayers", playerService.getByTeam(bowlingTeamId));

        return "scorecard/live-scoring";
    }

    @PostMapping("/ball")
    @ResponseBody
    public ResponseEntity<?> recordBall(@RequestBody BallEventDTO event) {
        try {
            // Update Batting Score
            if (event.getStrikerId() != null) {
                BattingScore striker = battingService.getById(event.getStrikerId()).orElse(null);
                if (striker != null) {
                    if (!event.isWide()) {
                        striker.setBalls((striker.getBalls() != null ? striker.getBalls() : 0) + 1);
                        if (!event.isLegBye() && !event.isBye()) {
                            striker.setRuns((striker.getRuns() != null ? striker.getRuns() : 0) + event.getRuns());
                            if (event.getRuns() == 4) striker.setFours((striker.getFours() != null ? striker.getFours() : 0) + 1);
                            if (event.getRuns() == 6) striker.setSixes((striker.getSixes() != null ? striker.getSixes() : 0) + 1);
                        }
                    }
                    if (event.isWicket()) {
                        striker.setIsOut(true);
                        striker.setDismissalType(event.getDismissalType());
                        striker.setBowlerId(event.getBowlerId());
                    }
                    battingService.update(striker);
                }
            }

            // Update Bowling Score
            if (event.getBowlerId() != null) {
                BowlingScore bowler = bowlingService.getById(event.getBowlerId()).orElse(null);
                if (bowler != null) {
                    if (!event.isBye() && !event.isLegBye()) { // Byes and leg byes don't count against bowler
                        if (!event.isWide() && !event.isNoBall()) {
                            int currentBalls = (int) ((bowler.getOvers() != null ? bowler.getOvers() : 0.0) * 10);
                            currentBalls++;
                            if (currentBalls % 10 == 6) {
                                currentBalls = (currentBalls / 10 + 1) * 10;
                            }
                            bowler.setOvers(currentBalls / 10.0);
                            if (event.getRuns() == 0) bowler.setDots((bowler.getDots() != null ? bowler.getDots() : 0) + 1);
                        }
                        
                        if (event.isWide()) bowler.setWides((bowler.getWides() != null ? bowler.getWides() : 0) + 1);
                        if (event.isNoBall()) bowler.setNoBalls((bowler.getNoBalls() != null ? bowler.getNoBalls() : 0) + 1);
                        
                        // Runs added to bowler
                        if (!event.isBye() && !event.isLegBye()) {
                            int bowlerRuns = event.getRuns();
                            if (event.isWide() || event.isNoBall()) bowlerRuns += 1;
                            bowler.setRuns((bowler.getRuns() != null ? bowler.getRuns() : 0) + bowlerRuns);
                        }

                        if (event.isWicket() && !"RUN_OUT".equals(event.getDismissalType())) {
                            bowler.setWickets((bowler.getWickets() != null ? bowler.getWickets() : 0) + 1);
                        }
                        bowlingService.update(bowler);
                    }
                }
            }

            // Update Extras
            if (event.isWide() || event.isNoBall() || event.isBye() || event.isLegBye()) {
                Extras extras = extrasService.getByScorecardId(event.getScorecardId())
                                .orElseGet(() -> {
                                    Extras e = new Extras();
                                    e.setScorecardId(event.getScorecardId());
                                    e.setWides(0);
                                    e.setNoBalls(0);
                                    e.setByes(0);
                                    e.setLegByes(0);
                                    e.setPenalty(0);
                                    return e;
                                });
                if (event.isWide()) extras.setWides((extras.getWides() != null ? extras.getWides() : 0) + 1 + event.getRuns());
                if (event.isNoBall()) extras.setNoBalls((extras.getNoBalls() != null ? extras.getNoBalls() : 0) + 1);
                if (event.isBye()) extras.setByes((extras.getByes() != null ? extras.getByes() : 0) + event.getRuns());
                if (event.isLegBye()) extras.setLegByes((extras.getLegByes() != null ? extras.getLegByes() : 0) + event.getRuns());
                if (extras.getId() != null) {
                    extrasService.update(extras);
                } else {
                    extrasService.save(extras);
                }
            }

            // Recalculate totals
            scorecardService.recalculateTotals(event.getScorecardId());

            boolean inningsComplete = false;
            Scorecard sc = scorecardService.getById(event.getScorecardId()).orElse(null);
            if (sc != null) {
                Match m = matchService.getById(sc.getMatchId()).orElse(null);
                if (m != null) {
                    int totalPlayers = (m.getTotalPlayers() != null) ? m.getTotalPlayers() : 11;
                    int maxWickets = Math.max(1, totalPlayers - 1);
                    double matchOvers = (m.getOvers() != null) ? m.getOvers().doubleValue() : 20.0;
                    
                    if (sc.getTotalWickets() != null && sc.getTotalWickets() >= maxWickets) {
                        inningsComplete = true;
                    }
                    if (sc.getTotalOvers() != null && sc.getTotalOvers() >= matchOvers) {
                        inningsComplete = true;
                    }
                }
            }

            return ResponseEntity.ok().body("{\"status\":\"success\", \"inningsComplete\":" + inningsComplete + "}");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/undo")
    @ResponseBody
    public ResponseEntity<?> undoBall(@RequestBody BallEventDTO event) {
        try {
            // Undo Batting Score
            if (event.getStrikerId() != null) {
                BattingScore striker = battingService.getById(event.getStrikerId()).orElse(null);
                if (striker != null) {
                    if (!event.isWide()) {
                        striker.setBalls(Math.max(0, (striker.getBalls() != null ? striker.getBalls() : 0) - 1));
                        if (!event.isLegBye() && !event.isBye()) {
                            striker.setRuns(Math.max(0, (striker.getRuns() != null ? striker.getRuns() : 0) - event.getRuns()));
                            if (event.getRuns() == 4) striker.setFours(Math.max(0, (striker.getFours() != null ? striker.getFours() : 0) - 1));
                            if (event.getRuns() == 6) striker.setSixes(Math.max(0, (striker.getSixes() != null ? striker.getSixes() : 0) - 1));
                        }
                    }
                    if (event.isWicket()) {
                        striker.setIsOut(false);
                        striker.setDismissalType(null);
                        striker.setBowlerId(null);
                    }
                    battingService.update(striker);
                }
            }

            // Undo Bowling Score
            if (event.getBowlerId() != null) {
                BowlingScore bowler = bowlingService.getById(event.getBowlerId()).orElse(null);
                if (bowler != null) {
                    if (!event.isBye() && !event.isLegBye()) {
                        if (!event.isWide() && !event.isNoBall()) {
                            int currentBalls = (int) ((bowler.getOvers() != null ? bowler.getOvers() : 0.0) * 10);
                            if (currentBalls > 0) {
                                currentBalls--;
                                if (currentBalls % 10 == 9) { // e.g. 1.0 -> 0.9 -> mathematically should be 0.5
                                    currentBalls = (currentBalls / 10) * 10 + 5;
                                }
                                bowler.setOvers(currentBalls / 10.0);
                            }
                            if (event.getRuns() == 0) bowler.setDots(Math.max(0, (bowler.getDots() != null ? bowler.getDots() : 0) - 1));
                        }
                        
                        if (event.isWide()) bowler.setWides(Math.max(0, (bowler.getWides() != null ? bowler.getWides() : 0) - 1));
                        if (event.isNoBall()) bowler.setNoBalls(Math.max(0, (bowler.getNoBalls() != null ? bowler.getNoBalls() : 0) - 1));
                        
                        if (!event.isBye() && !event.isLegBye()) {
                            int bowlerRuns = event.getRuns();
                            if (event.isWide() || event.isNoBall()) bowlerRuns += 1;
                            bowler.setRuns(Math.max(0, (bowler.getRuns() != null ? bowler.getRuns() : 0) - bowlerRuns));
                        }

                        if (event.isWicket() && !"RUN_OUT".equals(event.getDismissalType())) {
                            bowler.setWickets(Math.max(0, (bowler.getWickets() != null ? bowler.getWickets() : 0) - 1));
                        }
                        bowlingService.update(bowler);
                    }
                }
            }

            // Undo Extras
            if (event.isWide() || event.isNoBall() || event.isBye() || event.isLegBye()) {
                Extras extras = extrasService.getByScorecardId(event.getScorecardId()).orElse(null);
                if (extras != null) {
                    if (event.isWide()) extras.setWides(Math.max(0, (extras.getWides() != null ? extras.getWides() : 0) - (1 + event.getRuns())));
                    if (event.isNoBall()) extras.setNoBalls(Math.max(0, (extras.getNoBalls() != null ? extras.getNoBalls() : 0) - 1));
                    if (event.isBye()) extras.setByes(Math.max(0, (extras.getByes() != null ? extras.getByes() : 0) - event.getRuns()));
                    if (event.isLegBye()) extras.setLegByes(Math.max(0, (extras.getLegByes() != null ? extras.getLegByes() : 0) - event.getRuns()));
                    if (extras.getId() != null) {
                        extrasService.update(extras);
                    } else {
                        extrasService.save(extras);
                    }
                }
            }

            // Recalculate totals
            scorecardService.recalculateTotals(event.getScorecardId());

            return ResponseEntity.ok().body("{\"status\":\"success\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
    
    @PostMapping("/add-batting")
    @ResponseBody
    public ResponseEntity<?> addBattingAJAX(@RequestBody java.util.Map<String, String> payload) {
        try {
            Long matchId = Long.parseLong(payload.get("matchId"));
            int innings = Integer.parseInt(payload.get("innings"));
            String playerName = payload.get("playerName");
            
            Match match = matchService.getById(matchId).orElseThrow(() -> new RuntimeException("Match not found"));
            Long battingTeamId = (innings == 1) ? match.getTeamAId() : match.getTeamBId();
            Long bowlingTeamId = (innings == 1) ? match.getTeamBId() : match.getTeamAId();
            Scorecard scorecard = scorecardService.getOrCreateScorecard(matchId, innings, battingTeamId, bowlingTeamId);
            
            Player p = new Player();
            p.setTournamentId(match.getTournamentId());
            p.setTeamId(battingTeamId);
            p.setPlayerName(playerName);
            p.setRole("BATSMAN");
            p.setStatus("ACTIVE");
            long pid = playerService.save(p);
            
            BattingScore b = new BattingScore();
            b.setScorecardId(scorecard.getId());
            b.setPlayerId(pid);
            b.setPlayerName(playerName);
            b.setRuns(0); b.setBalls(0); b.setFours(0); b.setSixes(0); b.setIsOut(false);
            long bid = battingService.save(b);
            
            return ResponseEntity.ok().body("{\"status\":\"success\", \"id\":" + bid + "}");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/add-bowling")
    @ResponseBody
    public ResponseEntity<?> addBowlingAJAX(@RequestBody java.util.Map<String, String> payload) {
        try {
            Long matchId = Long.parseLong(payload.get("matchId"));
            int innings = Integer.parseInt(payload.get("innings"));
            String playerName = payload.get("playerName");
            
            Match match = matchService.getById(matchId).orElseThrow(() -> new RuntimeException("Match not found"));
            Long battingTeamId = (innings == 1) ? match.getTeamAId() : match.getTeamBId();
            Long bowlingTeamId = (innings == 1) ? match.getTeamBId() : match.getTeamAId();
            Scorecard scorecard = scorecardService.getOrCreateScorecard(matchId, innings, battingTeamId, bowlingTeamId);
            
            Player p = new Player();
            p.setTournamentId(match.getTournamentId());
            p.setTeamId(bowlingTeamId);
            p.setPlayerName(playerName);
            p.setRole("BOWLER");
            p.setStatus("ACTIVE");
            long pid = playerService.save(p);
            
            BowlingScore b = new BowlingScore();
            b.setScorecardId(scorecard.getId());
            b.setPlayerId(pid);
            b.setPlayerName(playerName);
            b.setOvers(0.0); b.setMaidens(0); b.setRuns(0); b.setWickets(0);
            b.setDots(0); b.setWides(0); b.setNoBalls(0);
            long bid = bowlingService.save(b);
            
            return ResponseEntity.ok().body("{\"status\":\"success\", \"id\":" + bid + "}");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/end-innings")
    @ResponseBody
    public ResponseEntity<?> endInnings() {
        return ResponseEntity.ok().body("{\"status\":\"success\", \"inningsComplete\": true}");
    }
}

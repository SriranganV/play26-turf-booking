package turfPlay.turf_booking.scorecard;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ScorecardService {

    private final ScorecardRepository scorecardRepo;
    private final BattingScoreRepository battingRepo;
    private final ExtrasRepository extrasRepo;
    private final MatchRepository matchRepo;

    public ScorecardService(ScorecardRepository scorecardRepo, BattingScoreRepository battingRepo,
                            ExtrasRepository extrasRepo, MatchRepository matchRepo) {
        this.scorecardRepo = scorecardRepo;
        this.battingRepo = battingRepo;
        this.extrasRepo = extrasRepo;
        this.matchRepo = matchRepo;
    }

    public List<Scorecard> getAll() { return scorecardRepo.findAll(); }
    public Optional<Scorecard> getById(Long id) { return scorecardRepo.findById(id); }
    public List<Scorecard> getByMatchId(Long matchId) { return scorecardRepo.findByMatchId(matchId); }
    public void save(Scorecard s) { scorecardRepo.save(s); }
    public void update(Scorecard s) { scorecardRepo.update(s); }
    public void delete(Long id) { scorecardRepo.deleteById(id); }
    public long count() { return scorecardRepo.count(); }

    /**
     * Get or create a scorecard for a specific match innings.
     */
    public Scorecard getOrCreateScorecard(Long matchId, int innings, Long battingTeamId, Long bowlingTeamId) {
        List<Scorecard> existing = scorecardRepo.findByMatchId(matchId);
        for (Scorecard s : existing) {
            if (s.getInnings() != null && s.getInnings() == innings) {
                return s;
            }
        }
        // Create new scorecard
        Scorecard sc = new Scorecard();
        sc.setMatchId(matchId);
        sc.setInnings(innings);
        sc.setBattingTeamId(battingTeamId);
        sc.setBowlingTeamId(bowlingTeamId);
        sc.setTotalRuns(0);
        sc.setTotalWickets(0);
        sc.setTotalOvers(0.0);
        sc.setExtras(0);
        sc.setStatus("IN_PROGRESS");
        if (innings == 2) {
            // Set target from innings 1
            for (Scorecard s : existing) {
                if (s.getInnings() != null && s.getInnings() == 1) {
                    sc.setTarget(s.getTotalRuns() + 1);
                    break;
                }
            }
        }
        scorecardRepo.save(sc);
        // Fetch back the newly created one
        List<Scorecard> refreshed = scorecardRepo.findByMatchId(matchId);
        for (Scorecard s : refreshed) {
            if (s.getInnings() != null && s.getInnings() == innings) {
                return s;
            }
        }
        return sc;
    }

    /**
     * Recalculate scorecard totals from batting + extras data.
     */
    public void recalculateTotals(Long scorecardId) {
        Scorecard sc = scorecardRepo.findById(scorecardId).orElse(null);
        if (sc == null) return;

        Integer battingRuns = battingRepo.sumRunsByScorecardId(scorecardId);
        Integer wickets = battingRepo.countWicketsByScorecardId(scorecardId);
        Optional<Extras> extrasOpt = extrasRepo.findByScorecardId(scorecardId);
        int extrasTotal = extrasOpt.map(Extras::getTotal).orElse(0);

        sc.setTotalRuns(battingRuns + extrasTotal);
        sc.setTotalWickets(wickets);
        sc.setExtras(extrasTotal);
        scorecardRepo.update(sc);
    }

    /**
     * Calculate match result string by comparing innings totals.
     */
    public String calculateResult(Long matchId) {
        List<Scorecard> scorecards = scorecardRepo.findByMatchId(matchId);
        if (scorecards.size() < 2) return "Match incomplete";

        Scorecard innings1 = null, innings2 = null;
        for (Scorecard s : scorecards) {
            if (s.getInnings() == 1) innings1 = s;
            if (s.getInnings() == 2) innings2 = s;
        }
        if (innings1 == null || innings2 == null) return "Match incomplete";

        int runs1 = innings1.getTotalRuns() != null ? innings1.getTotalRuns() : 0;
        int runs2 = innings2.getTotalRuns() != null ? innings2.getTotalRuns() : 0;
        int wickets2 = innings2.getTotalWickets() != null ? innings2.getTotalWickets() : 0;

        String battingTeam1 = innings1.getBattingTeamName() != null ? innings1.getBattingTeamName() : "Team A";
        String battingTeam2 = innings2.getBattingTeamName() != null ? innings2.getBattingTeamName() : "Team B";

        if (runs1 > runs2) {
            return battingTeam1 + " won by " + (runs1 - runs2) + " runs";
        } else if (runs2 > runs1) {
            int wicketsRemaining = 10 - wickets2;
            return battingTeam2 + " won by " + wicketsRemaining + " wickets";
        } else {
            return "Match tied";
        }
    }

    /**
     * Complete a match: determine winner and set match status.
     */
    public void completeMatch(Long matchId) {
        List<Scorecard> scorecards = scorecardRepo.findByMatchId(matchId);
        Match match = matchRepo.findById(matchId).orElse(null);
        if (match == null) return;

        String result = calculateResult(matchId);
        match.setResult(result);
        match.setStatus("COMPLETED");

        // Determine winner
        if (scorecards.size() >= 2) {
            Scorecard innings1 = null, innings2 = null;
            for (Scorecard s : scorecards) {
                if (s.getInnings() == 1) innings1 = s;
                if (s.getInnings() == 2) innings2 = s;
                s.setStatus("COMPLETED");
                scorecardRepo.update(s);
            }
            if (innings1 != null && innings2 != null) {
                int runs1 = innings1.getTotalRuns() != null ? innings1.getTotalRuns() : 0;
                int runs2 = innings2.getTotalRuns() != null ? innings2.getTotalRuns() : 0;
                if (runs1 > runs2) {
                    match.setWinnerId(innings1.getBattingTeamId());
                } else if (runs2 > runs1) {
                    match.setWinnerId(innings2.getBattingTeamId());
                }
            }
        }
        matchRepo.update(match);
    }
}

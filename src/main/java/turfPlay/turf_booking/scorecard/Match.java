package turfPlay.turf_booking.scorecard;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

// Schema: matches(id, tournament_id, match_number, team_a_id, team_b_id, turf_id,
//   match_date, match_time, venue, overs, toss_winner BIGINT, toss_decision ENUM(BAT/BOWL),
//   winner BIGINT, man_of_match BIGINT, match_stage ENUM, result TEXT,
//   status ENUM(UPCOMING/LIVE/COMPLETED/CANCELLED), created_at, updated_at)
public class Match {
    private Long id;
    private Long tournamentId;
    private String tournamentName;  // joined
    private String matchNumber;
    private Long teamAId;
    private String teamAName;       // joined
    private Long teamBId;
    private String teamBName;       // joined
    private Long turfId;
    private String turfName;        // joined
    private LocalDate matchDate;
    private LocalTime matchTime;
    private String venue;
    private Integer overs;
    private Long tossWinnerId;
    private String tossWinnerName;  // joined
    private String tossDecision;    // BAT / BOWL
    private Long winnerId;
    private String winnerName;      // joined
    private Long manOfMatchId;
    private String manOfMatchName;  // joined
    private String matchStage;      // LEAGUE/QUARTER_FINAL/SEMI_FINAL/FINAL
    private String result;
    private String status;          // UPCOMING/LIVE/COMPLETED/CANCELLED
    private LocalDateTime createdAt;

    public Long getId() { return id; } public void setId(Long v) { this.id = v; }
    public Long getTournamentId() { return tournamentId; } public void setTournamentId(Long v) { this.tournamentId = v; }
    public String getTournamentName() { return tournamentName; } public void setTournamentName(String v) { this.tournamentName = v; }
    public String getMatchNumber() { return matchNumber; } public void setMatchNumber(String v) { this.matchNumber = v; }
    public Long getTeamAId() { return teamAId; } public void setTeamAId(Long v) { this.teamAId = v; }
    public String getTeamAName() { return teamAName; } public void setTeamAName(String v) { this.teamAName = v; }
    public Long getTeamBId() { return teamBId; } public void setTeamBId(Long v) { this.teamBId = v; }
    public String getTeamBName() { return teamBName; } public void setTeamBName(String v) { this.teamBName = v; }
    public Long getTurfId() { return turfId; } public void setTurfId(Long v) { this.turfId = v; }
    public String getTurfName() { return turfName; } public void setTurfName(String v) { this.turfName = v; }
    public LocalDate getMatchDate() { return matchDate; } public void setMatchDate(LocalDate v) { this.matchDate = v; }
    public LocalTime getMatchTime() { return matchTime; } public void setMatchTime(LocalTime v) { this.matchTime = v; }
    public String getVenue() { return venue; } public void setVenue(String v) { this.venue = v; }
    public Integer getOvers() { return overs; } public void setOvers(Integer v) { this.overs = v; }
    public Long getTossWinnerId() { return tossWinnerId; } public void setTossWinnerId(Long v) { this.tossWinnerId = v; }
    public String getTossWinnerName() { return tossWinnerName; } public void setTossWinnerName(String v) { this.tossWinnerName = v; }
    public String getTossDecision() { return tossDecision; } public void setTossDecision(String v) { this.tossDecision = v; }
    public Long getWinnerId() { return winnerId; } public void setWinnerId(Long v) { this.winnerId = v; }
    public String getWinnerName() { return winnerName; } public void setWinnerName(String v) { this.winnerName = v; }
    public Long getManOfMatchId() { return manOfMatchId; } public void setManOfMatchId(Long v) { this.manOfMatchId = v; }
    public String getManOfMatchName() { return manOfMatchName; } public void setManOfMatchName(String v) { this.manOfMatchName = v; }
    public String getMatchStage() { return matchStage; } public void setMatchStage(String v) { this.matchStage = v; }
    public String getResult() { return result; } public void setResult(String v) { this.result = v; }
    public String getStatus() { return status; } public void setStatus(String v) { this.status = v; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
}
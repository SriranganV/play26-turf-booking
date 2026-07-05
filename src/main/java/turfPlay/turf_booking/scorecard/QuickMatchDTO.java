package turfPlay.turf_booking.scorecard;

import java.time.LocalDate;
import java.time.LocalTime;

public class QuickMatchDTO {
    private String teamAName;
    private String teamBName;
    private Integer overs;
    private Integer totalPlayers;
    private String venue;
    private LocalDate matchDate;
    private LocalTime matchTime;
    private String tossWinner; // "TEAM_A" or "TEAM_B"
    private String tossDecision; // "BAT" or "BOWL"

    public String getTeamAName() { return teamAName; }
    public void setTeamAName(String teamAName) { this.teamAName = teamAName; }
    public String getTeamBName() { return teamBName; }
    public void setTeamBName(String teamBName) { this.teamBName = teamBName; }
    public Integer getOvers() { return overs; }
    public void setOvers(Integer overs) { this.overs = overs; }
    public Integer getTotalPlayers() { return totalPlayers; }
    public void setTotalPlayers(Integer totalPlayers) { this.totalPlayers = totalPlayers; }
    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }
    public LocalDate getMatchDate() { return matchDate; }
    public void setMatchDate(LocalDate matchDate) { this.matchDate = matchDate; }
    public LocalTime getMatchTime() { return matchTime; }
    public void setMatchTime(LocalTime matchTime) { this.matchTime = matchTime; }
    public String getTossWinner() { return tossWinner; }
    public void setTossWinner(String tossWinner) { this.tossWinner = tossWinner; }
    public String getTossDecision() { return tossDecision; }
    public void setTossDecision(String tossDecision) { this.tossDecision = tossDecision; }
}

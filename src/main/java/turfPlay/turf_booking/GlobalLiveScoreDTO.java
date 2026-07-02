package turfPlay.turf_booking;

public class GlobalLiveScoreDTO {
    private String matchId;
    private String tournamentName;
    private String teamA;
    private String teamB;
    private String teamALogo;
    private String teamBLogo;
    private String scoreSummary;
    private String matchStatus; // e.g., "LIVE", "INNINGS BREAK", "COMPLETED"
    private String sportType; // "CRICKET" or "FOOTBALL"

    public GlobalLiveScoreDTO() {}

    public GlobalLiveScoreDTO(String matchId, String tournamentName, String teamA, String teamB, 
                              String scoreSummary, String matchStatus, String sportType) {
        this.matchId = matchId;
        this.tournamentName = tournamentName;
        this.teamA = teamA;
        this.teamB = teamB;
        this.scoreSummary = scoreSummary;
        this.matchStatus = matchStatus;
        this.sportType = sportType;
    }

    public String getMatchId() { return matchId; }
    public void setMatchId(String matchId) { this.matchId = matchId; }
    
    public String getTournamentName() { return tournamentName; }
    public void setTournamentName(String tournamentName) { this.tournamentName = tournamentName; }

    public String getTeamA() { return teamA; }
    public void setTeamA(String teamA) { this.teamA = teamA; }

    public String getTeamB() { return teamB; }
    public void setTeamB(String teamB) { this.teamB = teamB; }

    public String getTeamALogo() { return teamALogo; }
    public void setTeamALogo(String teamALogo) { this.teamALogo = teamALogo; }

    public String getTeamBLogo() { return teamBLogo; }
    public void setTeamBLogo(String teamBLogo) { this.teamBLogo = teamBLogo; }

    public String getScoreSummary() { return scoreSummary; }
    public void setScoreSummary(String scoreSummary) { this.scoreSummary = scoreSummary; }

    public String getMatchStatus() { return matchStatus; }
    public void setMatchStatus(String matchStatus) { this.matchStatus = matchStatus; }

    public String getSportType() { return sportType; }
    public void setSportType(String sportType) { this.sportType = sportType; }
}

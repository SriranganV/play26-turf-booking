package turfPlay.turf_booking;

public class GlobalStandingDTO {
    private int rank;
    private String teamName;
    private String teamLogoUrl;
    private int matchesPlayed;
    private int won;
    private int drawn;
    private int lost;
    
    // Football specifics
    private int goalsFor;
    private int goalsAgainst;
    private int goalDifference;
    
    // Cricket specifics
    private double netRunRate;
    
    private int points;

    public GlobalStandingDTO() {}

    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }
    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }
    public String getTeamLogoUrl() { return teamLogoUrl; }
    public void setTeamLogoUrl(String teamLogoUrl) { this.teamLogoUrl = teamLogoUrl; }
    public int getMatchesPlayed() { return matchesPlayed; }
    public void setMatchesPlayed(int matchesPlayed) { this.matchesPlayed = matchesPlayed; }
    public int getWon() { return won; }
    public void setWon(int won) { this.won = won; }
    public int getDrawn() { return drawn; }
    public void setDrawn(int drawn) { this.drawn = drawn; }
    public int getLost() { return lost; }
    public void setLost(int lost) { this.lost = lost; }
    public int getGoalsFor() { return goalsFor; }
    public void setGoalsFor(int goalsFor) { this.goalsFor = goalsFor; }
    public int getGoalsAgainst() { return goalsAgainst; }
    public void setGoalsAgainst(int goalsAgainst) { this.goalsAgainst = goalsAgainst; }
    public int getGoalDifference() { return goalDifference; }
    public void setGoalDifference(int goalDifference) { this.goalDifference = goalDifference; }
    public double getNetRunRate() { return netRunRate; }
    public void setNetRunRate(double netRunRate) { this.netRunRate = netRunRate; }
    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
}

package turfPlay.turf_booking.scorecard;

public class BallEventDTO {
    private Long scorecardId;
    private Long strikerId;
    private Long nonStrikerId;
    private Long bowlerId;
    private int runs;
    private boolean isWide;
    private boolean isNoBall;
    private boolean isBye;
    private boolean isLegBye;
    private boolean isWicket;
    private String dismissalType;

    // Getters and Setters
    public Long getScorecardId() { return scorecardId; }
    public void setScorecardId(Long scorecardId) { this.scorecardId = scorecardId; }
    public Long getStrikerId() { return strikerId; }
    public void setStrikerId(Long strikerId) { this.strikerId = strikerId; }
    public Long getNonStrikerId() { return nonStrikerId; }
    public void setNonStrikerId(Long nonStrikerId) { this.nonStrikerId = nonStrikerId; }
    public Long getBowlerId() { return bowlerId; }
    public void setBowlerId(Long bowlerId) { this.bowlerId = bowlerId; }
    public int getRuns() { return runs; }
    public void setRuns(int runs) { this.runs = runs; }
    public boolean isWide() { return isWide; }
    public void setWide(boolean wide) { this.isWide = wide; }
    public boolean isNoBall() { return isNoBall; }
    public void setNoBall(boolean noBall) { this.isNoBall = noBall; }
    public boolean isBye() { return isBye; }
    public void setBye(boolean bye) { this.isBye = bye; }
    public boolean isLegBye() { return isLegBye; }
    public void setLegBye(boolean legBye) { this.isLegBye = legBye; }
    public boolean isWicket() { return isWicket; }
    public void setWicket(boolean wicket) { this.isWicket = wicket; }
    public String getDismissalType() { return dismissalType; }
    public void setDismissalType(String dismissalType) { this.dismissalType = dismissalType; }
}

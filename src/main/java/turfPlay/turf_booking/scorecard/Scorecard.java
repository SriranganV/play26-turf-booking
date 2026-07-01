package turfPlay.turf_booking.scorecard;

import java.time.LocalDateTime;

/**
 * Scorecard POJO matching the `scorecards` table.
 * Each row represents one innings of a match.
 */
public class Scorecard {
    private Long id;
    private Long matchId;
    private Integer innings;           // 1 or 2
    private Long battingTeamId;
    private String battingTeamName;    // joined
    private Long bowlingTeamId;
    private String bowlingTeamName;    // joined
    private Integer totalRuns;
    private Integer totalWickets;
    private Double totalOvers;
    private Integer extras;
    private Integer target;
    private String status;             // IN_PROGRESS / COMPLETED
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Scorecard() {}

    public Long getId(){return id;} public void setId(Long v){this.id=v;}
    public Long getMatchId(){return matchId;} public void setMatchId(Long v){this.matchId=v;}
    public Integer getInnings(){return innings;} public void setInnings(Integer v){this.innings=v;}
    public Long getBattingTeamId(){return battingTeamId;} public void setBattingTeamId(Long v){this.battingTeamId=v;}
    public String getBattingTeamName(){return battingTeamName;} public void setBattingTeamName(String v){this.battingTeamName=v;}
    public Long getBowlingTeamId(){return bowlingTeamId;} public void setBowlingTeamId(Long v){this.bowlingTeamId=v;}
    public String getBowlingTeamName(){return bowlingTeamName;} public void setBowlingTeamName(String v){this.bowlingTeamName=v;}
    public Integer getTotalRuns(){return totalRuns;} public void setTotalRuns(Integer v){this.totalRuns=v;}
    public Integer getTotalWickets(){return totalWickets;} public void setTotalWickets(Integer v){this.totalWickets=v;}
    public Double getTotalOvers(){return totalOvers;} public void setTotalOvers(Double v){this.totalOvers=v;}
    public Integer getExtras(){return extras;} public void setExtras(Integer v){this.extras=v;}
    public Integer getTarget(){return target;} public void setTarget(Integer v){this.target=v;}
    public String getStatus(){return status;} public void setStatus(String v){this.status=v;}
    public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime v){this.createdAt=v;}
    public LocalDateTime getUpdatedAt(){return updatedAt;} public void setUpdatedAt(LocalDateTime v){this.updatedAt=v;}

    // Helper methods
    public String getScore() {
        int r = totalRuns == null ? 0 : totalRuns;
        int w = totalWickets == null ? 0 : totalWickets;
        return r + "/" + w;
    }

    public String getOversDisplay() {
        return (totalOvers == null ? "0.0" : totalOvers) + " Overs";
    }
}
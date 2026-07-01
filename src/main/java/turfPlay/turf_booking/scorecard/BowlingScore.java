package turfPlay.turf_booking.scorecard;

import java.time.LocalDateTime;

public class BowlingScore {
    private Long id;
    private Long scorecardId;
    private Long playerId;
    private String playerName;
    private Double overs;
    private Integer maidens;
    private Integer runs;
    private Integer wickets;
    private Double economy;
    private Integer dots;
    private Integer wides;
    private Integer noBalls;
    private LocalDateTime createdAt;

    public Long getId(){return id;} public void setId(Long v){this.id=v;}
    public Long getScorecardId(){return scorecardId;} public void setScorecardId(Long v){this.scorecardId=v;}
    public Long getPlayerId(){return playerId;} public void setPlayerId(Long v){this.playerId=v;}
    public String getPlayerName(){return playerName;} public void setPlayerName(String v){this.playerName=v;}
    public Double getOvers(){return overs;} public void setOvers(Double v){this.overs=v;}
    public Integer getMaidens(){return maidens;} public void setMaidens(Integer v){this.maidens=v;}
    public Integer getRuns(){return runs;} public void setRuns(Integer v){this.runs=v;}
    public Integer getWickets(){return wickets;} public void setWickets(Integer v){this.wickets=v;}
    public Double getEconomy(){return economy;} public void setEconomy(Double v){this.economy=v;}
    public Integer getDots(){return dots;} public void setDots(Integer v){this.dots=v;}
    public Integer getWides(){return wides;} public void setWides(Integer v){this.wides=v;}
    public Integer getNoBalls(){return noBalls;} public void setNoBalls(Integer v){this.noBalls=v;}
    public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime v){this.createdAt=v;}
}

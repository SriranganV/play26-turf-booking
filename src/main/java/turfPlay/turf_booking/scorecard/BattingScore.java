package turfPlay.turf_booking.scorecard;

import java.time.LocalDateTime;

public class BattingScore {
    private Long id;
    private Long scorecardId;
    private Long playerId;
    private String playerName;
    private Integer runs;
    private Integer balls;
    private Integer fours;
    private Integer sixes;
    private Double strikeRate;
    private boolean isOut;
    private String dismissalType;
    private Long bowlerId;
    private String bowlerName;
    private Long fielderId;
    private String fielderName;
    private Integer battingPosition;
    private LocalDateTime createdAt;

    public Long getId(){return id;} public void setId(Long v){this.id=v;}
    public Long getScorecardId(){return scorecardId;} public void setScorecardId(Long v){this.scorecardId=v;}
    public Long getPlayerId(){return playerId;} public void setPlayerId(Long v){this.playerId=v;}
    public String getPlayerName(){return playerName;} public void setPlayerName(String v){this.playerName=v;}
    public Integer getRuns(){return runs;} public void setRuns(Integer v){this.runs=v;}
    public Integer getBalls(){return balls;} public void setBalls(Integer v){this.balls=v;}
    public Integer getFours(){return fours;} public void setFours(Integer v){this.fours=v;}
    public Integer getSixes(){return sixes;} public void setSixes(Integer v){this.sixes=v;}
    public Double getStrikeRate(){return strikeRate;} public void setStrikeRate(Double v){this.strikeRate=v;}
    public boolean isIsOut(){return isOut;} public void setIsOut(boolean v){this.isOut=v;}
    public String getDismissalType(){return dismissalType;} public void setDismissalType(String v){this.dismissalType=v;}
    public Long getBowlerId(){return bowlerId;} public void setBowlerId(Long v){this.bowlerId=v;}
    public String getBowlerName(){return bowlerName;} public void setBowlerName(String v){this.bowlerName=v;}
    public Long getFielderId(){return fielderId;} public void setFielderId(Long v){this.fielderId=v;}
    public String getFielderName(){return fielderName;} public void setFielderName(String v){this.fielderName=v;}
    public Integer getBattingPosition(){return battingPosition;} public void setBattingPosition(Integer v){this.battingPosition=v;}
    public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime v){this.createdAt=v;}
}

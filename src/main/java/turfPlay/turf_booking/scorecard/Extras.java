package turfPlay.turf_booking.scorecard;

import java.time.LocalDateTime;

public class Extras {
    private Long id;
    private Long scorecardId;
    private Integer wides;
    private Integer noBalls;
    private Integer byes;
    private Integer legByes;
    private Integer penalty;
    private Integer total;
    private LocalDateTime createdAt;

    public Long getId(){return id;} public void setId(Long v){this.id=v;}
    public Long getScorecardId(){return scorecardId;} public void setScorecardId(Long v){this.scorecardId=v;}
    public Integer getWides(){return wides;} public void setWides(Integer v){this.wides=v;}
    public Integer getNoBalls(){return noBalls;} public void setNoBalls(Integer v){this.noBalls=v;}
    public Integer getByes(){return byes;} public void setByes(Integer v){this.byes=v;}
    public Integer getLegByes(){return legByes;} public void setLegByes(Integer v){this.legByes=v;}
    public Integer getPenalty(){return penalty;} public void setPenalty(Integer v){this.penalty=v;}
    public Integer getTotal(){return total;} public void setTotal(Integer v){this.total=v;}
    public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime v){this.createdAt=v;}
}

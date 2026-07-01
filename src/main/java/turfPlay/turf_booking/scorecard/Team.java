package turfPlay.turf_booking.scorecard;
import java.time.LocalDateTime;
public class Team {
    private Long id;
    private Long tournamentId;
    private String tournamentName;
    private String teamName;
    private String shortName;
    private String teamCode;
    private String logo;
    private String city;
    private String description;
    private String coachName;
    private Long captainId;
    private String captainName;
    private Long viceCaptainId;
    private String viceCaptainName;
    private String homeGround;
    private Integer matchesPlayed;
    private Integer wins;
    private Integer losses;
    private Integer ties;
    private Integer noResult;
    private Integer points;
    private java.math.BigDecimal netRunRate;
    private Integer totalRuns;
    private Integer totalWickets;
    private String status;
    private LocalDateTime createdAt;

    public Long getId(){return id;} public void setId(Long v){this.id=v;}
    public Long getTournamentId(){return tournamentId;} public void setTournamentId(Long v){this.tournamentId=v;}
    public String getTournamentName(){return tournamentName;} public void setTournamentName(String v){this.tournamentName=v;}
    public String getTeamName(){return teamName;} public void setTeamName(String v){this.teamName=v;}
    public String getShortName(){return shortName;} public void setShortName(String v){this.shortName=v;}
    public String getTeamCode(){return teamCode;} public void setTeamCode(String v){this.teamCode=v;}
    public String getLogo(){return logo;} public void setLogo(String v){this.logo=v;}
    public String getCity(){return city;} public void setCity(String v){this.city=v;}
    public String getDescription(){return description;} public void setDescription(String v){this.description=v;}
    public String getCoachName(){return coachName;} public void setCoachName(String v){this.coachName=v;}
    public Long getCaptainId(){return captainId;} public void setCaptainId(Long v){this.captainId=v;}
    public String getCaptainName(){return captainName;} public void setCaptainName(String v){this.captainName=v;}
    public Long getViceCaptainId(){return viceCaptainId;} public void setViceCaptainId(Long v){this.viceCaptainId=v;}
    public String getViceCaptainName(){return viceCaptainName;} public void setViceCaptainName(String v){this.viceCaptainName=v;}
    public String getHomeGround(){return homeGround;} public void setHomeGround(String v){this.homeGround=v;}
    public Integer getMatchesPlayed(){return matchesPlayed;} public void setMatchesPlayed(Integer v){this.matchesPlayed=v;}
    public Integer getWins(){return wins;} public void setWins(Integer v){this.wins=v;}
    public Integer getLosses(){return losses;} public void setLosses(Integer v){this.losses=v;}
    public Integer getTies(){return ties;} public void setTies(Integer v){this.ties=v;}
    public Integer getNoResult(){return noResult;} public void setNoResult(Integer v){this.noResult=v;}
    public Integer getPoints(){return points;} public void setPoints(Integer v){this.points=v;}
    public java.math.BigDecimal getNetRunRate(){return netRunRate;} public void setNetRunRate(java.math.BigDecimal v){this.netRunRate=v;}
    public Integer getTotalRuns(){return totalRuns;} public void setTotalRuns(Integer v){this.totalRuns=v;}
    public Integer getTotalWickets(){return totalWickets;} public void setTotalWickets(Integer v){this.totalWickets=v;}
    public String getStatus(){return status;} public void setStatus(String v){this.status=v;}
    public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime v){this.createdAt=v;}
}
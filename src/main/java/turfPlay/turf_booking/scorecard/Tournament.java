package turfPlay.turf_booking.scorecard;



import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Tournament {
    private Long id;
    private String tournamentName;
    private String description;
    private String tournamentType;
    private String matchType;
    private Integer overs;
    private String ballType;
    private BigDecimal entryFee;
    private BigDecimal prizePool;
    private Integer maximumTeams;
    private Integer registeredTeams;
    private String venue;
    private LocalDate startDate;
    private LocalDate endDate;
    private String banner;
    private String rules;
    private String status;
    private Long createdBy;
    private LocalDateTime createdAt;

    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getTournamentName() { return tournamentName; } public void setTournamentName(String v) { this.tournamentName = v; }
    public String getDescription() { return description; } public void setDescription(String v) { this.description = v; }
    public String getTournamentType() { return tournamentType; } public void setTournamentType(String v) { this.tournamentType = v; }
    public String getMatchType() { return matchType; } public void setMatchType(String v) { this.matchType = v; }
    public Integer getOvers() { return overs; } public void setOvers(Integer v) { this.overs = v; }
    public String getBallType() { return ballType; } public void setBallType(String v) { this.ballType = v; }
    public BigDecimal getEntryFee() { return entryFee; } public void setEntryFee(BigDecimal v) { this.entryFee = v; }
    public BigDecimal getPrizePool() { return prizePool; } public void setPrizePool(BigDecimal v) { this.prizePool = v; }
    public Integer getMaximumTeams() { return maximumTeams; } public void setMaximumTeams(Integer v) { this.maximumTeams = v; }
    public Integer getRegisteredTeams() { return registeredTeams; } public void setRegisteredTeams(Integer v) { this.registeredTeams = v; }
    public String getVenue() { return venue; } public void setVenue(String v) { this.venue = v; }
    public LocalDate getStartDate() { return startDate; } public void setStartDate(LocalDate v) { this.startDate = v; }
    public LocalDate getEndDate() { return endDate; } public void setEndDate(LocalDate v) { this.endDate = v; }
    public String getBanner() { return banner; } public void setBanner(String v) { this.banner = v; }
    public String getRules() { return rules; } public void setRules(String v) { this.rules = v; }
    public String getStatus() { return status; } public void setStatus(String v) { this.status = v; }
    public Long getCreatedBy() { return createdBy; } public void setCreatedBy(Long v) { this.createdBy = v; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
}
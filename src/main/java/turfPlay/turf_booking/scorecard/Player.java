package turfPlay.turf_booking.scorecard;
import java.time.LocalDate;
import java.time.LocalDateTime;

// Schema: players(id,tournament_id,team_id,player_name,jersey_number,photo,role,
//   batting_style,bowling_style,wicket_keeper,captain,vice_captain,
//   date_of_birth,age,nationality,phone,email,status,created_at,updated_at)
public class Player {
    private Long id;
    private Long tournamentId;
    private String tournamentName;
    private Long teamId;
    private String teamName;
    private String playerName;
    private Integer jerseyNumber;
    private String photo;
    private String role;
    private String battingStyle;
    private String bowlingStyle;
    private boolean wicketKeeper;
    private boolean captain;
    private boolean viceCaptain;
    private LocalDate dateOfBirth;
    private Integer age;
    private String nationality;
    private String phone;
    private String email;
    private String status;
    private LocalDateTime createdAt;

    public Long getId(){return id;} public void setId(Long v){this.id=v;}
    public Long getTournamentId(){return tournamentId;} public void setTournamentId(Long v){this.tournamentId=v;}
    public String getTournamentName(){return tournamentName;} public void setTournamentName(String v){this.tournamentName=v;}
    public Long getTeamId(){return teamId;} public void setTeamId(Long v){this.teamId=v;}
    public String getTeamName(){return teamName;} public void setTeamName(String v){this.teamName=v;}
    public String getPlayerName(){return playerName;} public void setPlayerName(String v){this.playerName=v;}
    public Integer getJerseyNumber(){return jerseyNumber;} public void setJerseyNumber(Integer v){this.jerseyNumber=v;}
    public String getPhoto(){return photo;} public void setPhoto(String v){this.photo=v;}
    public String getRole(){return role;} public void setRole(String v){this.role=v;}
    public String getBattingStyle(){return battingStyle;} public void setBattingStyle(String v){this.battingStyle=v;}
    public String getBowlingStyle(){return bowlingStyle;} public void setBowlingStyle(String v){this.bowlingStyle=v;}
    public boolean isWicketKeeper(){return wicketKeeper;} public void setWicketKeeper(boolean v){this.wicketKeeper=v;}
    public boolean isCaptain(){return captain;} public void setCaptain(boolean v){this.captain=v;}
    public boolean isViceCaptain(){return viceCaptain;} public void setViceCaptain(boolean v){this.viceCaptain=v;}
    public LocalDate getDateOfBirth(){return dateOfBirth;} public void setDateOfBirth(LocalDate v){this.dateOfBirth=v;}
    public Integer getAge(){return age;} public void setAge(Integer v){this.age=v;}
    public String getNationality(){return nationality;} public void setNationality(String v){this.nationality=v;}
    public String getPhone(){return phone;} public void setPhone(String v){this.phone=v;}
    public String getEmail(){return email;} public void setEmail(String v){this.email=v;}
    public String getStatus(){return status;} public void setStatus(String v){this.status=v;}
    public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime v){this.createdAt=v;}
}
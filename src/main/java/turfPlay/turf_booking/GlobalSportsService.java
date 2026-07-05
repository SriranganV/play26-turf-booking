package turfPlay.turf_booking;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.security.SecureRandom;

@Service
public class GlobalSportsService {

    @Value("${api.news.url}")
    private String newsApiUrl;

    @Value("${api.news.key}")
    private String newsApiKey;

    @Value("${api.football.url}")
    private String footballApiUrl;

    @Value("${api.football.key}")
    private String footballApiKey;

    @Value("${api.cricket.url}")
    private String cricketApiUrl;

    @Value("${api.cricket.key}")
    private String cricketApiKey;

    @Value("${api.cricket.seriesId}")
    private String cricketSeriesId;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    // Caching
    private final Map<String, List<GlobalStandingDTO>> standingsCache = new ConcurrentHashMap<>();
    private final Map<String, Long> standingsCacheTime = new ConcurrentHashMap<>();
    private static final long CACHE_DURATION_MS = 15 * 60 * 1000; // 15 minutes

    private List<GlobalLiveScoreDTO> liveScoresCache = null;
    private long liveScoresCacheTime = 0L;
    private static final long LIVE_CACHE_DURATION_MS = 60 * 1000; // 1 minute

    public GlobalSportsService() {
        this.restTemplate = createInsecureRestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    private RestTemplate createInsecureRestTemplate() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
            };
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());

            org.springframework.http.client.SimpleClientHttpRequestFactory factory = 
                new org.springframework.http.client.SimpleClientHttpRequestFactory() {
                    @Override
                    protected java.net.HttpURLConnection openConnection(java.net.URL url, java.net.Proxy proxy) throws java.io.IOException {
                        java.net.HttpURLConnection connection = super.openConnection(url, proxy);
                        if (connection instanceof HttpsURLConnection) {
                            ((HttpsURLConnection) connection).setSSLSocketFactory(sslContext.getSocketFactory());
                            ((HttpsURLConnection) connection).setHostnameVerifier((hostname, session) -> true);
                        }
                        return connection;
                    }
                };
            return new RestTemplate(factory);
        } catch (Exception e) {
            System.err.println("Failed to create insecure RestTemplate: " + e.getMessage());
            return new RestTemplate();
        }
    }

    /**
     * Fetches real live news from NewsAPI. Falls back to mock data if the key is missing or invalid.
     */
    public List<GlobalNewsDTO> fetchLatestNews() {
        List<GlobalNewsDTO> newsList = new ArrayList<>();
        
        // Check if API key is not configured
        if (newsApiKey == null || newsApiKey.equals("YOUR_NEWS_API_KEY_HERE")) {
            System.out.println("NewsAPI Key missing. Returning mock data.");
            return getMockNews();
        }

        try {
            String url = newsApiUrl + newsApiKey;
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode articles = root.path("articles");
            
            if (articles.isArray()) {
                int count = 0;
                for (JsonNode article : articles) {
                    if (count >= 6) break; // Limit to 6 articles
                    
                    GlobalNewsDTO news = new GlobalNewsDTO();
                    news.setId(String.valueOf(count + 1));
                    news.setTitle(article.path("title").asText("No Title"));
                    news.setSummary(article.path("description").asText(""));
                    news.setImageUrl(article.path("urlToImage").asText(""));
                    news.setSourceUrl(article.path("url").asText("#"));
                    news.setPublishedDate(article.path("publishedAt").asText("").substring(0, 10)); // Just the date
                    news.setSportCategory(article.path("source").path("name").asText("Sports"));
                    
                    // Skip articles without images to keep UI clean
                    if (!news.getImageUrl().isEmpty() && !news.getImageUrl().equals("null")) {
                        newsList.add(news);
                        count++;
                    }
                }
            }
            return newsList;
        } catch (Exception e) {
            System.err.println("Error fetching live news: " + e.getMessage());
            return getMockNews();
        }
    }

    /**
     * Fetches football standings by competition code (e.g., PL, WC, CL, BL1, FL1, SA).
     * Includes a 15-minute memory cache to avoid hitting the API rate limit (10 req/min).
     */
    public List<GlobalStandingDTO> fetchFootballStandings(String competitionCode) {
        // Check cache
        if (standingsCache.containsKey(competitionCode)) {
            long lastFetched = standingsCacheTime.getOrDefault(competitionCode, 0L);
            if (System.currentTimeMillis() - lastFetched < CACHE_DURATION_MS) {
                return standingsCache.get(competitionCode);
            }
        }

        List<GlobalStandingDTO> standings = new ArrayList<>();
        
        if (footballApiKey == null || footballApiKey.equals("YOUR_FOOTBALL_DATA_API_KEY_HERE") || footballApiKey.equals("YOUR_RAPIDAPI_KEY_HERE")) {
            System.out.println("Football-Data API Key missing. Returning mock data for " + competitionCode);
            return getMockFootballStandings(competitionCode);
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Auth-Token", footballApiKey);
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
            
            // Dynamic URL based on competition code
            String url = "https://api.football-data.org/v4/competitions/" + competitionCode + "/standings";
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode standingsArray = root.path("standings").get(0).path("table");
            
            if (standingsArray.isArray()) {
                for (JsonNode teamNode : standingsArray) {
                    GlobalStandingDTO t = new GlobalStandingDTO();
                    t.setRank(teamNode.path("position").asInt());
                    t.setTeamName(teamNode.path("team").path("name").asText());
                    t.setTeamLogoUrl(teamNode.path("team").path("crest").asText());
                    t.setPoints(teamNode.path("points").asInt());
                    t.setGoalDifference(teamNode.path("goalDifference").asInt());
                    
                    t.setMatchesPlayed(teamNode.path("playedGames").asInt());
                    t.setWon(teamNode.path("won").asInt());
                    t.setDrawn(teamNode.path("draw").asInt());
                    t.setLost(teamNode.path("lost").asInt());
                    t.setGoalsFor(teamNode.path("goalsFor").asInt());
                    t.setGoalsAgainst(teamNode.path("goalsAgainst").asInt());
                    
                    standings.add(t);
                }
            }
            
            // Save to cache
            standingsCache.put(competitionCode, standings);
            standingsCacheTime.put(competitionCode, System.currentTimeMillis());
            
            return standings;
        } catch (Exception e) {
            System.err.println("Error fetching live football standings for " + competitionCode + ": " + e.getMessage());
            // If cache exists but expired, return stale data rather than mock data if API fails
            if (standingsCache.containsKey(competitionCode)) {
                return standingsCache.get(competitionCode);
            }
            return getMockFootballStandings(competitionCode);
        }
    }

    /**
     * Fetches real Cricket (IPL) standings from CricAPI.
     */
    public List<GlobalStandingDTO> fetchIplStandings() {
        // Check cache for IPL
        if (standingsCache.containsKey("IPL")) {
            long lastFetched = standingsCacheTime.getOrDefault("IPL", 0L);
            if (System.currentTimeMillis() - lastFetched < CACHE_DURATION_MS) {
                return standingsCache.get("IPL");
            }
        }

        List<GlobalStandingDTO> standings = new ArrayList<>();
        
        if (cricketApiKey == null || cricketApiKey.equals("YOUR_CRICAPI_KEY_HERE")) {
            System.out.println("CricAPI Key missing. Returning mock data for IPL.");
            return getMockIplStandings();
        }

        try {
            String url = cricketApiUrl + "?apikey=" + cricketApiKey + "&seriesid=" + cricketSeriesId;
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            JsonNode root = objectMapper.readTree(response.getBody());
            String status = root.path("status").asText();
            
            if ("success".equals(status)) {
                JsonNode dataArray = root.path("data");
                if (dataArray.isArray()) {
                    int rank = 1;
                    for (JsonNode teamNode : dataArray) {
                        GlobalStandingDTO t = new GlobalStandingDTO();
                        t.setRank(rank++);
                        t.setTeamName(teamNode.path("teamname").asText());
                        
                        // Fallback fields for some CricAPI versions
                        if (t.getTeamName() == null || t.getTeamName().isEmpty()) {
                            t.setTeamName(teamNode.path("team_name").asText());
                        }
                        if (t.getTeamName() == null || t.getTeamName().isEmpty()) {
                             t.setTeamName(teamNode.path("team").asText("Unknown Team"));
                        }
                        
                        t.setMatchesPlayed(teamNode.path("matches").asInt());
                        t.setWon(teamNode.path("wins").asInt());
                        t.setLost(teamNode.path("loss").asInt());
                        // sometimes it's 'losses'
                        if(t.getLost() == 0) t.setLost(teamNode.path("losses").asInt());
                        
                        t.setPoints(teamNode.path("pts").asInt());
                        if(t.getPoints() == 0) t.setPoints(teamNode.path("points").asInt());
                        
                        t.setNetRunRate(teamNode.path("nrr").asDouble());
                        if(t.getNetRunRate() == 0.0) t.setNetRunRate(teamNode.path("net_run_rate").asDouble());
                        
                        standings.add(t);
                    }
                }
            } else {
                System.err.println("CricAPI returned status: " + status);
                return getMockIplStandings();
            }
            
            // Save to cache
            standingsCache.put("IPL", standings);
            standingsCacheTime.put("IPL", System.currentTimeMillis());
            
            return standings;
            
        } catch (Exception e) {
            System.err.println("Error fetching live IPL standings: " + e.getMessage());
            if (standingsCache.containsKey("IPL")) {
                return standingsCache.get("IPL");
            }
            return getMockIplStandings();
        }
    }

    /**
     * Fetches live cricket scores from CricAPI.
     */
    public List<GlobalLiveScoreDTO> fetchLiveCricketScores() {
        if (liveScoresCache != null && (System.currentTimeMillis() - liveScoresCacheTime < LIVE_CACHE_DURATION_MS)) {
            return liveScoresCache;
        }

        if (cricketApiKey == null || cricketApiKey.equals("YOUR_CRICAPI_KEY_HERE")) {
            System.out.println("CricAPI Key missing. Returning mock data for live scores.");
            return getMockLiveCricketScores();
        }

        List<GlobalLiveScoreDTO> scores = new ArrayList<>();
        try {
            // Fetch current matches
            String url = "https://api.cricapi.com/v1/currentMatches?apikey=" + cricketApiKey + "&offset=0";
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JsonNode root = objectMapper.readTree(response.getBody());
            
            if ("success".equals(root.path("status").asText())) {
                JsonNode dataArray = root.path("data");
                if (dataArray.isArray()) {
                    for (JsonNode match : dataArray) {
                        GlobalLiveScoreDTO dto = new GlobalLiveScoreDTO();
                        dto.setMatchId(match.path("id").asText());
                        dto.setTournamentName(match.path("name").asText());
                        dto.setMatchStatus(match.path("status").asText());
                        dto.setSportType("CRICKET");

                        JsonNode teamInfo = match.path("teamInfo");
                        if (teamInfo.isArray() && teamInfo.size() >= 2) {
                            dto.setTeamA(teamInfo.get(0).path("name").asText());
                            dto.setTeamB(teamInfo.get(1).path("name").asText());
                            dto.setTeamALogo(teamInfo.get(0).path("img").asText());
                            dto.setTeamBLogo(teamInfo.get(1).path("img").asText());
                        } else {
                            JsonNode teams = match.path("teams");
                            if (teams.isArray() && teams.size() >= 2) {
                                dto.setTeamA(teams.get(0).asText());
                                dto.setTeamB(teams.get(1).asText());
                            }
                        }

                        JsonNode scoreNode = match.path("score");
                        StringBuilder summary = new StringBuilder();
                        if (scoreNode.isArray()) {
                            for (JsonNode inn : scoreNode) {
                                if (summary.length() > 0) summary.append(" | ");
                                summary.append(inn.path("inning").asText().replace(" Inning", ""))
                                       .append(" ")
                                       .append(inn.path("r").asInt())
                                       .append("/")
                                       .append(inn.path("w").asInt())
                                       .append(" (")
                                       .append(inn.path("o").asDouble())
                                       .append(")");
                            }
                        }
                        
                        if (summary.length() == 0) {
                            summary.append("Match yet to begin");
                        }
                        
                        dto.setScoreSummary(summary.toString());
                        scores.add(dto);
                    }
                }
            }
            
            if (scores.isEmpty()) {
                return getMockLiveCricketScores();
            }

            liveScoresCache = scores;
            liveScoresCacheTime = System.currentTimeMillis();
            return scores;
        } catch (Exception e) {
            System.err.println("Error fetching live cricket scores: " + e.getMessage());
            if (liveScoresCache != null) return liveScoresCache;
            return getMockLiveCricketScores();
        }
    }

    /**
     * Fetches live football scores (mock data for now, would integrate with an API like football-data).
     */
    public List<GlobalLiveScoreDTO> fetchLiveFootballScores() {
        List<GlobalLiveScoreDTO> scores = new ArrayList<>();
        
        GlobalLiveScoreDTO m1 = new GlobalLiveScoreDTO("f1", "Premier League", "Arsenal", "Manchester United", 
                "2 - 1 (65')", "LIVE", "FOOTBALL");
        m1.setTeamALogo("https://upload.wikimedia.org/wikipedia/en/thumb/5/53/Arsenal_FC.svg/1200px-Arsenal_FC.svg.png");
        m1.setTeamBLogo("https://upload.wikimedia.org/wikipedia/en/thumb/7/7a/Manchester_United_FC_crest.svg/1200px-Manchester_United_FC_crest.svg.png");
        
        GlobalLiveScoreDTO m2 = new GlobalLiveScoreDTO("f2", "Champions League", "Real Madrid", "Bayern Munich", 
                "0 - 0", "HALF TIME", "FOOTBALL");
                
        GlobalLiveScoreDTO m3 = new GlobalLiveScoreDTO("f3", "La Liga", "Barcelona", "Atletico Madrid", 
                "3 - 1", "COMPLETED", "FOOTBALL");

        scores.add(m1);
        scores.add(m2);
        scores.add(m3);
        
        return scores;
    }

    // --- MOCK DATA FALLBACKS ---

    private List<GlobalLiveScoreDTO> getMockLiveCricketScores() {
        List<GlobalLiveScoreDTO> scores = new ArrayList<>();
        GlobalLiveScoreDTO m1 = new GlobalLiveScoreDTO("c1", "IPL 2026", "Chennai Super Kings", "Mumbai Indians", 
                "CSK: 210/5 (20.0) | MI: 205/9 (20.0)", "COMPLETED", "CRICKET");
        m1.setTeamALogo("https://upload.wikimedia.org/wikipedia/en/thumb/4/41/Flag_of_India.svg/255px-Flag_of_India.svg.png");
        m1.setTeamBLogo("https://upload.wikimedia.org/wikipedia/en/thumb/b/b9/Flag_of_Australia.svg/255px-Flag_of_Australia.svg.png");
        
        GlobalLiveScoreDTO m2 = new GlobalLiveScoreDTO("c2", "T20 World Cup", "India", "Australia", 
                "IND: 185/4 (20.0) | AUS: 172/8 (19.2)", "LIVE", "CRICKET");
        scores.add(m1);
        scores.add(m2);
        return scores;
    }

    private List<GlobalNewsDTO> getMockNews() {
        List<GlobalNewsDTO> news = new ArrayList<>();
        news.add(new GlobalNewsDTO("1", "Premier League Title Race Heats Up", "With only three games remaining, Arsenal and Manchester City are neck and neck at the top of the table. Every point matters in this thrilling conclusion to the season.", "https://images.unsplash.com/photo-1522778119026-d647f0596c20?auto=format&fit=crop&w=600&q=80", "#", "2 Hours Ago", "Football"));
        news.add(new GlobalNewsDTO("2", "IPL Final Set: Historic Clash Awaits", "Chennai Super Kings will face off against Kolkata Knight Riders in what promises to be a legendary IPL final. Analysts predict a high-scoring thriller.", "https://images.unsplash.com/photo-1540747913346-19e32dc3e97e?auto=format&fit=crop&w=600&q=80", "#", "5 Hours Ago", "Cricket"));
        news.add(new GlobalNewsDTO("3", "Champions League Quarter-Final Draw Released", "Real Madrid draws Bayern Munich in a classic European tie, while PSG faces Barcelona. The road to Wembley just got significantly harder.", "https://images.unsplash.com/photo-1508098682722-e99c43a406b2?auto=format&fit=crop&w=600&q=80", "#", "1 Day Ago", "Football"));
        news.add(new GlobalNewsDTO("4", "T20 World Cup Venues Confirmed", "The ICC has officially confirmed the stadiums hosting the upcoming T20 World Cup, with the final scheduled to be played in front of a record-breaking crowd.", "https://images.unsplash.com/photo-1593341646782-e0b495cff86d?auto=format&fit=crop&w=600&q=80", "#", "2 Days Ago", "Cricket"));
        return news;
    }

    private List<GlobalStandingDTO> getMockFootballStandings(String code) {
        switch (code) {
            case "WC": return getMockWorldCup();
            case "CL": return getMockChampionsLeague();
            case "BL1": return getMockBundesliga();
            case "FL1": return getMockLigue1();
            case "SA": return getMockSerieA();
            case "PL":
            default: return getMockPremierLeague();
        }
    }

    private List<GlobalStandingDTO> getMockPremierLeague() {
        List<GlobalStandingDTO> standings = new ArrayList<>();
        GlobalStandingDTO t1 = new GlobalStandingDTO(); t1.setRank(1); t1.setTeamName("Arsenal"); t1.setMatchesPlayed(35); t1.setWon(25); t1.setDrawn(5); t1.setLost(5); t1.setGoalsFor(85); t1.setGoalsAgainst(28); t1.setGoalDifference(57); t1.setPoints(80);
        GlobalStandingDTO t2 = new GlobalStandingDTO(); t2.setRank(2); t2.setTeamName("Manchester City"); t2.setMatchesPlayed(34); t2.setWon(24); t2.setDrawn(7); t2.setLost(3); t2.setGoalsFor(82); t2.setGoalsAgainst(32); t2.setGoalDifference(50); t2.setPoints(79);
        GlobalStandingDTO t3 = new GlobalStandingDTO(); t3.setRank(3); t3.setTeamName("Liverpool"); t3.setMatchesPlayed(35); t3.setWon(22); t3.setDrawn(9); t3.setLost(4); t3.setGoalsFor(77); t3.setGoalsAgainst(38); t3.setGoalDifference(39); t3.setPoints(75);
        GlobalStandingDTO t4 = new GlobalStandingDTO(); t4.setRank(4); t4.setTeamName("Aston Villa"); t4.setMatchesPlayed(35); t4.setWon(20); t4.setDrawn(7); t4.setLost(8); t4.setGoalsFor(71); t4.setGoalsAgainst(53); t4.setGoalDifference(18); t4.setPoints(67);
        standings.add(t1); standings.add(t2); standings.add(t3); standings.add(t4);
        return standings;
    }

    private List<GlobalStandingDTO> getMockWorldCup() {
        List<GlobalStandingDTO> standings = new ArrayList<>();
        GlobalStandingDTO t1 = new GlobalStandingDTO(); t1.setRank(1); t1.setTeamName("Argentina"); t1.setMatchesPlayed(7); t1.setWon(5); t1.setDrawn(1); t1.setLost(1); t1.setGoalsFor(15); t1.setGoalsAgainst(8); t1.setGoalDifference(7); t1.setPoints(16);
        GlobalStandingDTO t2 = new GlobalStandingDTO(); t2.setRank(2); t2.setTeamName("France"); t2.setMatchesPlayed(7); t2.setWon(5); t2.setDrawn(1); t2.setLost(1); t1.setGoalsFor(16); t1.setGoalsAgainst(7); t1.setGoalDifference(9); t1.setPoints(16);
        GlobalStandingDTO t3 = new GlobalStandingDTO(); t3.setRank(3); t3.setTeamName("Croatia"); t3.setMatchesPlayed(7); t3.setWon(2); t3.setDrawn(4); t3.setLost(1); t1.setGoalsFor(8); t1.setGoalsAgainst(7); t1.setGoalDifference(1); t1.setPoints(10);
        standings.add(t1); standings.add(t2); standings.add(t3);
        return standings;
    }

    private List<GlobalStandingDTO> getMockChampionsLeague() {
        List<GlobalStandingDTO> standings = new ArrayList<>();
        GlobalStandingDTO t1 = new GlobalStandingDTO(); t1.setRank(1); t1.setTeamName("Real Madrid"); t1.setMatchesPlayed(6); t1.setWon(6); t1.setDrawn(0); t1.setLost(0); t1.setGoalDifference(9); t1.setPoints(18);
        GlobalStandingDTO t2 = new GlobalStandingDTO(); t2.setRank(2); t2.setTeamName("Bayern Munich"); t2.setMatchesPlayed(6); t2.setWon(5); t2.setDrawn(1); t2.setLost(0); t1.setGoalDifference(6); t1.setPoints(16);
        GlobalStandingDTO t3 = new GlobalStandingDTO(); t3.setRank(3); t3.setTeamName("PSG"); t3.setMatchesPlayed(6); t3.setWon(2); t3.setDrawn(2); t3.setLost(2); t1.setGoalDifference(1); t1.setPoints(8);
        standings.add(t1); standings.add(t2); standings.add(t3);
        return standings;
    }

    private List<GlobalStandingDTO> getMockBundesliga() {
        List<GlobalStandingDTO> standings = new ArrayList<>();
        GlobalStandingDTO t1 = new GlobalStandingDTO(); t1.setRank(1); t1.setTeamName("Bayer Leverkusen"); t1.setMatchesPlayed(34); t1.setWon(28); t1.setDrawn(6); t1.setLost(0); t1.setGoalDifference(65); t1.setPoints(90);
        GlobalStandingDTO t2 = new GlobalStandingDTO(); t2.setRank(2); t2.setTeamName("VfB Stuttgart"); t2.setMatchesPlayed(34); t2.setWon(23); t2.setDrawn(4); t2.setLost(7); t1.setGoalDifference(39); t1.setPoints(73);
        GlobalStandingDTO t3 = new GlobalStandingDTO(); t3.setRank(3); t3.setTeamName("Bayern Munich"); t3.setMatchesPlayed(34); t3.setWon(23); t3.setDrawn(3); t3.setLost(8); t1.setGoalDifference(49); t1.setPoints(72);
        standings.add(t1); standings.add(t2); standings.add(t3);
        return standings;
    }

    private List<GlobalStandingDTO> getMockLigue1() {
        List<GlobalStandingDTO> standings = new ArrayList<>();
        GlobalStandingDTO t1 = new GlobalStandingDTO(); t1.setRank(1); t1.setTeamName("Paris Saint-Germain"); t1.setMatchesPlayed(34); t1.setWon(22); t1.setDrawn(10); t1.setLost(2); t1.setGoalDifference(48); t1.setPoints(76);
        GlobalStandingDTO t2 = new GlobalStandingDTO(); t2.setRank(2); t2.setTeamName("Monaco"); t2.setMatchesPlayed(34); t2.setWon(20); t2.setDrawn(7); t2.setLost(7); t1.setGoalDifference(26); t1.setPoints(67);
        GlobalStandingDTO t3 = new GlobalStandingDTO(); t3.setRank(3); t3.setTeamName("Brest"); t3.setMatchesPlayed(34); t3.setWon(17); t3.setDrawn(10); t3.setLost(7); t1.setGoalDifference(19); t1.setPoints(61);
        standings.add(t1); standings.add(t2); standings.add(t3);
        return standings;
    }

    private List<GlobalStandingDTO> getMockSerieA() {
        List<GlobalStandingDTO> standings = new ArrayList<>();
        GlobalStandingDTO t1 = new GlobalStandingDTO(); t1.setRank(1); t1.setTeamName("Inter Milan"); t1.setMatchesPlayed(38); t1.setWon(29); t1.setDrawn(7); t1.setLost(2); t1.setGoalDifference(67); t1.setPoints(94);
        GlobalStandingDTO t2 = new GlobalStandingDTO(); t2.setRank(2); t2.setTeamName("AC Milan"); t2.setMatchesPlayed(38); t2.setWon(22); t2.setDrawn(9); t2.setLost(7); t1.setGoalDifference(27); t1.setPoints(75);
        GlobalStandingDTO t3 = new GlobalStandingDTO(); t3.setRank(3); t3.setTeamName("Juventus"); t3.setMatchesPlayed(38); t3.setWon(19); t3.setDrawn(14); t3.setLost(5); t1.setGoalDifference(23); t1.setPoints(71);
        standings.add(t1); standings.add(t2); standings.add(t3);
        return standings;
    }
    
    private List<GlobalStandingDTO> getMockIplStandings() {
        List<GlobalStandingDTO> standings = new ArrayList<>();
        GlobalStandingDTO t1 = new GlobalStandingDTO(); t1.setRank(1); t1.setTeamName("Chennai Super Kings"); t1.setMatchesPlayed(14); t1.setWon(9); t1.setLost(5); t1.setNetRunRate(0.652); t1.setPoints(18);
        GlobalStandingDTO t2 = new GlobalStandingDTO(); t2.setRank(2); t2.setTeamName("Kolkata Knight Riders"); t2.setMatchesPlayed(14); t2.setWon(8); t2.setLost(6); t2.setNetRunRate(0.482); t2.setPoints(16);
        GlobalStandingDTO t3 = new GlobalStandingDTO(); t3.setRank(3); t3.setTeamName("Mumbai Indians"); t3.setMatchesPlayed(14); t3.setWon(8); t3.setLost(6); t3.setNetRunRate(-0.108); t3.setPoints(16);
        GlobalStandingDTO t4 = new GlobalStandingDTO(); t4.setRank(4); t4.setTeamName("Royal Challengers Bengaluru"); t4.setMatchesPlayed(14); t4.setWon(7); t4.setLost(7); t4.setNetRunRate(0.245); t4.setPoints(14);
        standings.add(t1); standings.add(t2); standings.add(t3); standings.add(t4);
        return standings;
    }
}

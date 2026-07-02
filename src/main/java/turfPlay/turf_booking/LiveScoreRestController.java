package turfPlay.turf_booking;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/scores")
public class LiveScoreRestController {

    private final GlobalSportsService globalSportsService;

    public LiveScoreRestController(GlobalSportsService globalSportsService) {
        this.globalSportsService = globalSportsService;
    }

    @GetMapping("/cricket")
    public ResponseEntity<List<GlobalLiveScoreDTO>> getCricketScores() {
        return ResponseEntity.ok(globalSportsService.fetchLiveCricketScores());
    }

    @GetMapping("/football")
    public ResponseEntity<List<GlobalLiveScoreDTO>> getFootballScores() {
        return ResponseEntity.ok(globalSportsService.fetchLiveFootballScores());
    }
}

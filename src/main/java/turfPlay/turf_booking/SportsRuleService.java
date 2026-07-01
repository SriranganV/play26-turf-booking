package turfPlay.turf_booking;


import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SportsRuleService {

    private final SportsRuleRepository sportsRuleRepository;

    public SportsRuleService(SportsRuleRepository sportsRuleRepository) {
        this.sportsRuleRepository = sportsRuleRepository;
    }

    public List<SportsRule> getAllRules() {
        return sportsRuleRepository.findAll();
    }

    public List<SportsRule> getRulesBySportName(String sportName) {
        return sportsRuleRepository.findBySportName(sportName);
    }

    public Optional<SportsRule> getRuleById(Long id) {
        return sportsRuleRepository.findById(id);
    }

    public void saveRule(SportsRule rule) {
        sportsRuleRepository.save(rule);
    }

    public void updateRule(SportsRule rule) {
        sportsRuleRepository.update(rule);
    }

    public void deleteRule(Long id) {
        sportsRuleRepository.deleteById(id);
    }
}

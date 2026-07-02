package turfPlay.turf_booking;

import org.springframework.jdbc.support.KeyHolder;
import java.util.Map;

public class GeneratedKeyExtractor {
    public static Long extractId(KeyHolder keyHolder) {
        if (keyHolder == null) return null;
        Map<String, Object> keys = keyHolder.getKeys();
        if (keys == null || keys.isEmpty()) return null;
        
        Object id = keys.get("id");
        if (id == null) {
            id = keys.get("ID");
        }
        if (id == null) {
            // Find any key that matches 'id' case-insensitively
            for (Map.Entry<String, Object> entry : keys.entrySet()) {
                if (entry.getKey().equalsIgnoreCase("id")) {
                    id = entry.getValue();
                    break;
                }
            }
        }
        if (id == null) {
            // Fallback: take the first key in the map
            id = keys.values().iterator().next();
        }
        
        if (id instanceof Number) {
            return ((Number) id).longValue();
        }
        return null;
    }
}

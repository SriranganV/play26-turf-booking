package turfPlay.turf_booking;

import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class DatabaseMigrationService {

    private static final Logger logger = Logger.getLogger(DatabaseMigrationService.class.getName());
    private final JdbcTemplate jdbcTemplate;

    public DatabaseMigrationService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void migrateDatabase() {
        logger.info("Checking if database migrations are needed...");
        
        try {
            logger.info("Attempting to drop uq_bookings_slot index to allow re-booking cancelled slots...");
            jdbcTemplate.execute("ALTER TABLE bookings DROP INDEX uq_bookings_slot;");
            logger.info("Dropped uq_bookings_slot index.");
        } catch (Exception e) {
            logger.info("Index uq_bookings_slot not found or already dropped.");
        }
        try {
            // Check if column exists by attempting to query it
            jdbcTemplate.execute("SELECT payment_type FROM bookings LIMIT 1");
            logger.info("Bookings table already has payment_type column. No migration needed.");
        } catch (Exception e) {
            logger.info("Bookings table missing split payment columns. Running ALTER TABLE...");
            try {
                jdbcTemplate.execute("""
                    ALTER TABLE bookings 
                    ADD COLUMN total_price DECIMAL(10,2) DEFAULT 0,
                    ADD COLUMN amount_paid DECIMAL(10,2) DEFAULT 0,
                    ADD COLUMN split_link_uuid VARCHAR(100) UNIQUE,
                    ADD COLUMN payment_type VARCHAR(20) DEFAULT 'FULL';
                """);
                
                jdbcTemplate.execute("""
                    CREATE INDEX idx_bookings_split ON bookings(split_link_uuid);
                """);
                logger.info("Database migration successful!");
            } catch (Exception ex) {
                logger.severe("Database migration failed: " + ex.getMessage());
            }
        }
    }
}

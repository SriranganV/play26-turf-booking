package turfPlay.turf_booking;

import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import org.springframework.security.crypto.password.PasswordEncoder;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.logging.Logger;

@Service
public class DatabaseMigrationService {

    private static final Logger logger = Logger.getLogger(DatabaseMigrationService.class.getName());
    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    public DatabaseMigrationService(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
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
        
        seedDatabase();
    }

    private void seedDatabase() {
        logger.info("Checking if database seeding is needed...");

        try {
            // Check if users exist
            Integer userCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
            if (userCount == null || userCount == 0) {
                logger.info("Database is empty. Seeding default users, turfs, and slots...");

                // 1. Seed Users
                String userPass = passwordEncoder.encode("password");
                
                jdbcTemplate.update("INSERT INTO users (full_name, email, password, role, enabled) VALUES (?, ?, ?, ?, ?)",
                        "Demo User", "user@play26.com", userPass, "ROLE_USER", true);
                        
                jdbcTemplate.update("INSERT INTO users (full_name, email, password, role, enabled) VALUES (?, ?, ?, ?, ?)",
                        "System Admin", "admin@play26.com", userPass, "ROLE_ADMIN", true);

                logger.info("Default users seeded: user@play26.com / admin@play26.com (password: password)");

                // 2. Seed Turfs
                jdbcTemplate.update("INSERT INTO turfs (id, name, location, description, price_per_hour, supported_sports, active) VALUES (?, ?, ?, ?, ?, ?, ?)",
                        1L, "Grand Turf Arena", "Chennai, TN", "Premium outdoor AstroTurf ground with night floodlights and seating area.", new BigDecimal("1200.00"), "Cricket, Football", true);

                jdbcTemplate.update("INSERT INTO turfs (id, name, location, description, price_per_hour, supported_sports, active) VALUES (?, ?, ?, ?, ?, ?, ?)",
                        2L, "Star Sports Club", "Bangalore, KA", "Professional indoor 5-a-side pitch with high-grade synthetic grass.", new BigDecimal("1000.00"), "Football, Badminton", true);

                jdbcTemplate.update("INSERT INTO turfs (id, name, location, description, price_per_hour, supported_sports, active) VALUES (?, ?, ?, ?, ?, ?, ?)",
                        3L, "Skyline Rooftop Turf", "Mumbai, MH", "Spectacular rooftop multi-sport arena. Play with a view of the city skyline.", new BigDecimal("1500.00"), "Cricket, Football", true);

                logger.info("Default turfs seeded.");

                // 3. Seed Slots (for today and tomorrow)
                LocalDate today = LocalDate.now();
                LocalDate tomorrow = today.plusDays(1);

                for (LocalDate date : new LocalDate[]{today, tomorrow}) {
                    for (long turfId = 1; turfId <= 3; turfId++) {
                        // Seed slots from 06:00 to 21:00 (1-hour slots)
                        for (int hour = 6; hour <= 20; hour++) {
                            jdbcTemplate.update("INSERT INTO turf_slots (turf_id, slot_date, start_time, end_time, status) VALUES (?, ?, ?, ?, ?)",
                                    turfId, java.sql.Date.valueOf(date), 
                                    java.sql.Time.valueOf(LocalTime.of(hour, 0)), 
                                    java.sql.Time.valueOf(LocalTime.of(hour + 1, 0)), 
                                    "AVAILABLE");
                        }
                    }
                }
                logger.info("Default slots seeded successfully.");
            } else {
                logger.info("Database already contains data. Seeding skipped.");
            }
        } catch (Exception e) {
            logger.severe("Database seeding failed: " + e.getMessage());
        }
    }
}

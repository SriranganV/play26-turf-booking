package turfPlay.turf_booking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class TurfBookingApplication {

	public static void main(String[] args) {
		SpringApplication.run(TurfBookingApplication.class, args);
	}

	@Bean
	public CommandLineRunner run(JdbcTemplate jdbcTemplate) {
		return args -> {
			try {
				jdbcTemplate.execute("ALTER TABLE bookings ADD COLUMN booking_id VARCHAR(50)");
				jdbcTemplate.execute("ALTER TABLE bookings ADD UNIQUE(booking_id)");
				System.out.println("✅ Database schema updated: Added booking_id to bookings table.");
			} catch (Exception e) {
				// Column likely already exists
				System.out.println("ℹ️ Database schema check: booking_id column already exists or error: " + e.getMessage());
			}
		};
	}
}

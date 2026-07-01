package turfPlay.turf_booking;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth

                        // ── Fully public pages ──────────────────────────────
                        .requestMatchers(
                            "/",
                            "/about",
                            "/contact",
                            "/sports",
                            "/rules",
                            "/blogs",
                            "/login",
                            "/register",
                            // Turf listing and detail pages are public (anyone can browse)
                            // The booking BUTTON inside turf-details is login-guarded via
                            // sec:authorize in Thymeleaf, but the page itself is open.
                            "/turfs",
                            "/turfs/**",
                            // Static assets
                            "/css/**",
                            "/js/**",
                            "/images/**",
                            "/*.png",
                            "/*.jpg",
                            "/*.ico"
                        ).permitAll()

                        // ── Admin only ──────────────────────────────────────
                        .requestMatchers("/admin", "/admin/**").hasRole("ADMIN")

                        // ── Must be logged in ───────────────────────────────
                        // /bookings/create/** requires authentication.
                        // Spring Security will redirect to /login, then back.
                        // After login, the POST replay may not work well —
                        // that is why turf-details.html shows a "Login to Book"
                        // link for guests instead of a form they can't submit.
                        .requestMatchers(
                            "/dashboard",
                            "/dashboard/**",
                            "/bookings/**",
                            "/scorecards/**"
                        ).authenticated()

                        // Anything else that isn't matched above requires auth
                        // (safer default than permitAll)
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage("/login")
                        .usernameParameter("email")
                        .defaultSuccessUrl("/dashboard", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .permitAll()
                )
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

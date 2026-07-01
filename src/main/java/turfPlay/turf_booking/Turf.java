package turfPlay.turf_booking;


import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Turf {

    private Long id;
    private String name;
    private String location;
    private String description;
    private BigDecimal pricePerHour;
    private String supportedSports;
    private boolean active;
    private LocalDateTime createdAt;

    public Turf() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(BigDecimal pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public String getSupportedSports() {
        return supportedSports;
    }

    public void setSupportedSports(String supportedSports) {
        this.supportedSports = supportedSports;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
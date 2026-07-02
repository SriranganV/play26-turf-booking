package turfPlay.turf_booking;

import java.time.LocalDateTime;

public class Review {
    private Long id;
    private Long userId;
    private Long turfId;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
    
    // Transient field for UI
    private String userName;

    public Review() {
    }

    public Review(Long userId, Long turfId, int rating, String comment) {
        this.userId = userId;
        this.turfId = turfId;
        this.rating = rating;
        this.comment = comment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTurfId() {
        return turfId;
    }

    public void setTurfId(Long turfId) {
        this.turfId = turfId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}

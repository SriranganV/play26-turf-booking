package turfPlay.turf_booking;

public class GlobalNewsDTO {
    private String id;
    private String title;
    private String summary;
    private String imageUrl;
    private String sourceUrl;
    private String publishedDate;
    private String sportCategory; // e.g. "Football", "Cricket"

    public GlobalNewsDTO() {}

    public GlobalNewsDTO(String id, String title, String summary, String imageUrl, String sourceUrl, String publishedDate, String sportCategory) {
        this.id = id;
        this.title = title;
        this.summary = summary;
        this.imageUrl = imageUrl;
        this.sourceUrl = sourceUrl;
        this.publishedDate = publishedDate;
        this.sportCategory = sportCategory;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getSourceUrl() { return sourceUrl; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }
    public String getPublishedDate() { return publishedDate; }
    public void setPublishedDate(String publishedDate) { this.publishedDate = publishedDate; }
    public String getSportCategory() { return sportCategory; }
    public void setSportCategory(String sportCategory) { this.sportCategory = sportCategory; }
}

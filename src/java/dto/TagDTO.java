package dto;

public class TagDTO {
    private long tagId;
    private String tagName;
    private String description;
    private boolean isActive;
    private int questionCount;
    private int followerCount;

    public TagDTO() {
    }

    public TagDTO(long tagId, String tagName, String description, boolean isActive) {
        this.tagId = tagId;
        this.tagName = tagName;
        this.description = description;
        this.isActive = isActive;
    }

    // Getters and Setters
    public long getTagId() {
        return tagId;
    }

    public void setTagId(long tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(int questionCount) {
        this.questionCount = questionCount;
    }

    public int getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(int followerCount) {
        this.followerCount = followerCount;
    }
}

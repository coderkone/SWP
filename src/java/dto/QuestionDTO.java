package dto;

import java.util.List;

public class QuestionDTO {
    private long questionId;
    private long userId;
    private String title;
    private String body;
    private String codeSnippet;
    private int viewCount;
    private boolean isClosed;
    private String closedReason;
    private String createdAt;
    private String updatedAt;
    private int score;
    private String username;
    private List<String> tags;

    public QuestionDTO() {}

    public QuestionDTO(long questionId, long userId, String title, String body, String codeSnippet,
                       int viewCount, boolean isClosed, String closedReason, String createdAt, 
                       String updatedAt, int score) {
        this.questionId = questionId;
        this.userId = userId;
        this.title = title;
        this.body = body;
        this.codeSnippet = codeSnippet;
        this.viewCount = viewCount;
        this.isClosed = isClosed;
        this.closedReason = closedReason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.score = score;
    }

    // Getters and Setters
    public long getQuestionId() { return questionId; }
    public void setQuestionId(long questionId) { this.questionId = questionId; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public String getCodeSnippet() { return codeSnippet; }
    public void setCodeSnippet(String codeSnippet) { this.codeSnippet = codeSnippet; }

    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }

    public boolean isIsClosed() { return isClosed; }
    public void setIsClosed(boolean isClosed) { this.isClosed = isClosed; }

    public String getClosedReason() { return closedReason; }
    public void setClosedReason(String closedReason) { this.closedReason = closedReason; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
}

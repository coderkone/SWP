package dto;

import java.sql.Timestamp;

public class CommentDTO {
    private long commentId;
    private long userId;
    private long questionId;
    private long answerId;
    private String body;
    private Timestamp createdAt;
    private String authorName;
    private int authorReputation;

    public CommentDTO() {}

    public CommentDTO(long commentId, long userId, long questionId, long answerId, String body, Timestamp createdAt) {
        this.commentId = commentId;
        this.userId = userId;
        this.questionId = questionId;
        this.answerId = answerId;
        this.body = body;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public long getCommentId() { return commentId; }
    public void setCommentId(long commentId) { this.commentId = commentId; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public long getQuestionId() { return questionId; }
    public void setQuestionId(long questionId) { this.questionId = questionId; }

    public long getAnswerId() { return answerId; }
    public void setAnswerId(long answerId) { this.answerId = answerId; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public int getAuthorReputation() { return authorReputation; }
    public void setAuthorReputation(int authorReputation) { this.authorReputation = authorReputation; }
}

package model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class BlogComment {

    private int commentId;
    private int blogId;
    private long userId;
    private Integer parentId; // Integer allows null values
    private String content;
    private Timestamp createdAt;

    // --- Extra fields mapped from JOINs for UI Display ---
    private String username;
    private String userAvatar;

    // --- List to hold child comments (Replies) ---
    private List<BlogComment> replies;

    public BlogComment() {
        // Initialize the list to prevent NullPointerException
        this.replies = new ArrayList<>();
    }

    // --- Getters and Setters ---
    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public int getBlogId() {
        return blogId;
    }

    public void setBlogId(int blogId) {
        this.blogId = blogId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public List<BlogComment> getReplies() {
        return replies;
    }

    public void setReplies(List<BlogComment> replies) {
        this.replies = replies;
    }
}

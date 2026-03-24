package dto;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import model.Question;

/**
 *
 * @author ADMIN
 */
public class QuestionDTO extends Question {
    private String authorName;
    private String authorAvatar;
    private int authorReputation;
    private int answerCount;
    private boolean hasAcceptedAnswer;
    private double popularScore;

    public QuestionDTO() {
    }
 
    public QuestionDTO(long questionId, long userId, String title, String body, String codeSnippet,
            int viewCount, boolean isClosed, Long closedBy, String closedReason, Timestamp closedAt,
            Timestamp createdAt, Timestamp updatedAt, int score, Long acceptedAnswerId,
            String authorName, String authorAvatar, int answerCount) {
        super(questionId, userId, title, body, codeSnippet, viewCount, isClosed, closedBy,
                closedReason, closedAt, createdAt, updatedAt, score, acceptedAnswerId);
        this.authorName = authorName;
        this.authorAvatar = authorAvatar;
        this.answerCount = answerCount;
        this.hasAcceptedAnswer = false;
    }
    private List<String> tags = new ArrayList<>(); // Khởi tạo luôn để không bị Null

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
    public String getAuthorName() { 
        return authorName; 
    }
    public void setAuthorName(String authorName) { 
        this.authorName = authorName; 
    }

    public String getAuthorAvatar() { 
        return authorAvatar; 
    }
    public void setAuthorAvatar(String authorAvatar) { 
        this.authorAvatar = authorAvatar; 
    }

    public int getAuthorReputation() {
        return authorReputation;
    }

    public void setAuthorReputation(int authorReputation) {
        this.authorReputation = authorReputation;
    }

    public int getAnswerCount() { 
        return answerCount; 
    }
    public void setAnswerCount(int answerCount) { 
        this.answerCount = answerCount; 
    }

    public boolean isHasAcceptedAnswer() {
        return hasAcceptedAnswer;
    }

    public void setHasAcceptedAnswer(boolean hasAcceptedAnswer) {
        this.hasAcceptedAnswer = hasAcceptedAnswer;
    }

    public double getPopularScore() {
        return popularScore;
    }

    public void setPopularScore(double popularScore) {
        this.popularScore = popularScore;
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;
import java.sql.Timestamp;
/**
 *
 * @author nguye
 */
public class Question {
    private long questionId;
    private long userId;
    private String title;
    private String body;
    private String codeSnippet;
    private int viewCount;
    private boolean isClosed; // bit trong SQL -> boolean
    private String closedReason;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private int score;

    public Question(long questionId, long userId, String title, String body, String codeSnippet, int viewCount, boolean isClosed, String closedReason, Timestamp createdAt, Timestamp updatedAt, int score) {
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

    public Question() {
    }

    public long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(long questionId) {
        this.questionId = questionId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCodeSnippet() {
        return codeSnippet;
    }

    public void setCodeSnippet(String codeSnippet) {
        this.codeSnippet = codeSnippet;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public boolean isIsClosed() {
        return isClosed;
    }

    public void setIsClosed(boolean isClosed) {
        this.isClosed = isClosed;
    }

    public String getClosedReason() {
        return closedReason;
    }

    public void setClosedReason(String closedReason) {
        this.closedReason = closedReason;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
    
}

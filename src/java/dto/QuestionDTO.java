package dto;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.util.List;
import java.util.ArrayList;
import java.sql.Timestamp;
import model.Question;

/**
 *
 * @author ADMIN
 */
public class QuestionDTO extends Question {
    private String authorName;
    private String authorAvatar;
    private int answerCount;

    public QuestionDTO() {
    }
 
    public QuestionDTO(long questionId, long userId, String title, String body, String codeSnippet, int viewCount, boolean isClosed, String closedReason, Timestamp createdAt, Timestamp updatedAt, int score, String authorName, String authorAvatar, int answerCount) {
        super(questionId, userId, title, body, codeSnippet, viewCount, isClosed, closedReason, createdAt, updatedAt, score);
        this.authorName = authorName;
        this.authorAvatar = authorAvatar;
        this.answerCount = answerCount;
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

    public int getAnswerCount() { 
        return answerCount; 
    }
    public void setAnswerCount(int answerCount) { 
        this.answerCount = answerCount; 
    }
}
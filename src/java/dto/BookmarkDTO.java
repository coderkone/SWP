package dto;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.sql.Timestamp;
/**
 *
 * @author nguye
 */
public class BookmarkDTO {
    private long questionId;
    private String questionTitle; // Lấy từ bảng Questions
    private Timestamp createdAt;
    private Integer collectionId;

    public BookmarkDTO(long questionId, String questionTitle, Timestamp createdAt, Integer collectionId) {
        this.questionId = questionId;
        this.questionTitle = questionTitle;
        this.createdAt = createdAt;
        this.collectionId = collectionId;
    }

    // Getters and Setters
    public long getQuestionId() { return questionId; }
    public void setQuestionId(long questionId) { this.questionId = questionId; }

    public String getQuestionTitle() { return questionTitle; }
    public void setQuestionTitle(String questionTitle) { this.questionTitle = questionTitle; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Integer getCollectionId() { return collectionId; }
    public void setCollectionId(Integer collectionId) { this.collectionId = collectionId; }
}
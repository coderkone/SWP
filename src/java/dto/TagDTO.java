
package dto;


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author Asus
 */

public class TagDTO {
    private long tagId;
    private String tagName;
    private String description;
    private boolean isActive;

    private int questionCount;
    private int followerCount;

    private boolean isFollowed; // them moi de check xem user theo doi tag hay chua


    public TagDTO() {
    }


    public TagDTO(long tagId, String tagName, String description, boolean isActive , boolean isFollowed) {
        this.tagId = tagId;
        this.tagName = tagName;
        this.description = description;
        this.isActive = isActive;
        this.isFollowed = isFollowed;


    

    // Getters and Setters

       
    }


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

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
 
    // them moi de check xem user follow tag do chua
   
    public boolean isFollowed() {
        return isFollowed;
    }

    public void setIsFollowed(boolean isFollowed) {
        this.isFollowed = isFollowed;
    }
public String getShortDescription() {
    if (description == null || description.trim().isEmpty()) {
        return "";
    }
    int dot = description.indexOf('.');
    if (dot != -1) {
        return description.substring(0, dot + 1); // "Java là ngôn ngữ."
    }
    return description; // Không có dấu chấm → trả nguyên
}
    
    
    

}

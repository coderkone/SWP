<<<<<<< HEAD
package dto;

=======
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

/**
 *
 * @author Asus
 */
>>>>>>> Mai
public class TagDTO {
    private long tagId;
    private String tagName;
    private String description;
    private boolean isActive;
<<<<<<< HEAD
    private int questionCount;
    private int followerCount;
=======
    private boolean isFollowed; // them moi de check xem user theo doi tag hay chua
>>>>>>> Mai

    public TagDTO() {
    }

<<<<<<< HEAD
    public TagDTO(long tagId, String tagName, String description, boolean isActive) {
=======
    public TagDTO(long tagId, String tagName, String description, boolean isActive, boolean isFollowed) {
>>>>>>> Mai
        this.tagId = tagId;
        this.tagName = tagName;
        this.description = description;
        this.isActive = isActive;
<<<<<<< HEAD
    }

    // Getters and Setters
=======
        this.isFollowed = isFollowed;
    }

>>>>>>> Mai
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

<<<<<<< HEAD
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
=======
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

    
    
    
>>>>>>> Mai
}

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
public class TagFollow {
    private long id;
    private long userId;
    private long tagId;
    private Timestamp followedAt;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getTagId() {
        return tagId;
    }

    public void setTagId(long tagId) {
        this.tagId = tagId;
    }

    public Timestamp getFollowedAt() {
        return followedAt;
    }

    public void setFollowedAt(Timestamp followedAt) {
        this.followedAt = followedAt;
    }
    
}

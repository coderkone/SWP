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
public class UserBadge {
    private long userId;
    private long badgeId;
    private Timestamp createdAt;

    public UserBadge() {
    }

    public UserBadge(long userId, long badgeId, Timestamp createdAt) {
        this.userId = userId;
        this.badgeId = badgeId;
        this.createdAt = createdAt;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getBadgeId() {
        return badgeId;
    }

    public void setBadgeId(long badgeId) {
        this.badgeId = badgeId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
}

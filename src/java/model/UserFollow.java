/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;
import java.sql.Timestamp;
public class UserFollow {
    private long followerId;
    private long followingId;
    private Timestamp followedAt;

    public UserFollow() {
    }

    public UserFollow(long followerId, long followingId, Timestamp followedAt) {
        this.followerId = followerId;
        this.followingId = followingId;
        this.followedAt = followedAt;
    }

    public long getFollowerId() {
        return followerId;
    }

    public void setFollowerId(long followerId) {
        this.followerId = followerId;
    }

    public long getFollowingId() {
        return followingId;
    }

    public void setFollowingId(long followingId) {
        this.followingId = followingId;
    }

    public Timestamp getFollowedAt() {
        return followedAt;
    }

    public void setFollowedAt(Timestamp followedAt) {
        this.followedAt = followedAt;
    }
    
}

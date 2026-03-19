package model;

import java.sql.Timestamp;

public class User {
    private long userId;
    private String username;
    private String email;
    private String passwordHash;
    private String role;
    private int reputation;
    private String provider;
    private String providerId;
    private Timestamp createdAt;
    private String avatarUrl;
    private String bio;
    private String location;
    private String website;

    public User() {
    }

    public User(long userId, String username, String email, String passwordHash, String role, int reputation, String provider, String providerId, Timestamp createdAt) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.reputation = reputation;
        this.provider = provider;
        this.providerId = providerId;
        this.createdAt = createdAt;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getReputation() {
        return reputation;
    }

    public void setReputation(int reputation) {
        this.reputation = reputation;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    // Helper: Display avatar with default fallback
    public String getDisplayAvatar() {
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            return avatarUrl;
        }
        return "assets/img/default-avatar.png";
    }

    @Override
    public String toString() {
        return "User{" + "userId=" + userId + ", username=" + username + ", email=" + email + ", role=" + role + '}';
    }
}
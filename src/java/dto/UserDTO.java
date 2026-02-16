package dto;

import java.sql.Timestamp;

public class UserDTO {
    private long userId;
    private String username;
    private String email;
    private String role;
    
    private int reputation;
    private Timestamp createdAt; // Ngày tham gia
    private String bio;
    private String location;
    private String website;
    private String avatarUrl;

    public UserDTO() {}

    public UserDTO(long userId, String username, String email, String role) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.role = role;
    }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public int getReputation() { return reputation; }
    public void setReputation(int reputation) { this.reputation = reputation; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    // Helper: Xử lý hiển thị Avatar mặc định (Logic Frontend dùng luôn)
    public String getDisplayAvatar() {
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            return avatarUrl;
        }
        // Link ảnh mặc định (bạn nhớ copy 1 ảnh default.png vào thư mục assets/img)
        return "assets/img/default-avatar.png"; 
    }
}
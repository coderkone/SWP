/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import config.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Badge;
import dto.UserDTO;

public class ProfileDAO extends DBContext {

    // 1. Đếm tổng số câu hỏi của user
    public int countQuestionsByUser(long userId) {
        String sql = "SELECT COUNT(*) FROM Questions WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            st = conn.prepareStatement(sql);
            st.setLong(1, userId);
            rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("Lỗi ở countQuestionsByUser: " + e.getMessage());
        } finally {
            if (rs != null) try { rs.close(); } catch (Exception e) {}
            if (st != null) try { st.close(); } catch (Exception e) {}
            if (conn != null) try { conn.close(); } catch (Exception e) {}
        }
        return 0;
    }

    // 2. Đếm tổng số câu trả lời của user
    public int countAnswersByUser(long userId) {
        String sql = "SELECT COUNT(*) FROM Answers WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            st = conn.prepareStatement(sql);
            st.setLong(1, userId);
            rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("Lỗi ở countAnswersByUser: " + e.getMessage());
        } finally {
            if (rs != null) try { rs.close(); } catch (Exception e) {}
            if (st != null) try { st.close(); } catch (Exception e) {}
            if (conn != null) try { conn.close(); } catch (Exception e) {}
        }
        return 0;
    }

    // 3. Tính tổng lượt xem các câu hỏi của user (Reached)
    public int countTotalViewByUser(long userId) {
        String sql = "SELECT COALESCE(SUM(view_count), 0) FROM Questions WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            st = conn.prepareStatement(sql);
            st.setLong(1, userId);
            rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("Lỗi ở countTotalViewByUser: " + e.getMessage());
        } finally {
            if (rs != null) try { rs.close(); } catch (Exception e) {}
            if (st != null) try { st.close(); } catch (Exception e) {}
            if (conn != null) try { conn.close(); } catch (Exception e) {}
        }
        return 0;
    }

    // 4. Lấy danh sách huy hiệu theo loại (gold, silver, bronze)
    public List<Badge> getBadgesByUserAndType(long userId, String type) {
        List<Badge> list = new ArrayList<>();
        String sql = "SELECT b.badge_id, b.name, b.type, b.description "
                + "FROM Badges b "
                + "JOIN User_Badges ub ON b.badge_id = ub.badge_id "
                + "WHERE ub.user_id = ? AND b.type = ?";

        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            st = conn.prepareStatement(sql);
            st.setLong(1, userId);
            st.setString(2, type);
            rs = st.executeQuery();
            while (rs.next()) {
                Badge badge = new Badge();
                badge.setBadgeId(rs.getLong("badge_id"));
                badge.setName(rs.getString("name"));
                badge.setType(rs.getString("type"));
                badge.setDescription(rs.getString("description"));
                list.add(badge);
            }
        } catch (Exception e) {
            System.out.println("Lỗi ở getBadgesByUserAndType: " + e.getMessage());
        } finally {
            if (rs != null) try { rs.close(); } catch (Exception e) {}
            if (st != null) try { st.close(); } catch (Exception e) {}
            if (conn != null) try { conn.close(); } catch (Exception e) {}
        }
        return list;
    }

    // 5. Hàm rút gọn: Chỉ lấy thông tin cần thiết cho trang Edit Profile
    public dto.UserDTO getUserFullProfile(long userId) {
        String sql = "SELECT u.user_id, u.username, u.email, u.role, u.Reputation, u.created_at, "
                + "p.bio, p.avatar_url, p.location, p.website "
                + "FROM Users u "
                + "LEFT JOIN User_Profile p ON u.user_id = p.user_id "
                + "WHERE u.user_id = ?";

        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            st = conn.prepareStatement(sql);
            st.setLong(1, userId);
            rs = st.executeQuery();
            if (rs.next()) {
                dto.UserDTO dto = new dto.UserDTO();
                // Thông tin từ bảng Users
                dto.setUserId(rs.getLong("user_id"));
                dto.setUsername(rs.getString("username"));
                dto.setEmail(rs.getString("email"));
                dto.setRole(rs.getString("role"));
                dto.setReputation(rs.getInt("Reputation"));
                dto.setCreatedAt(rs.getTimestamp("created_at"));

                // Thông tin từ bảng User_Profile
                dto.setBio(rs.getString("bio"));
                dto.setAvatarUrl(rs.getString("avatar_url"));
                dto.setLocation(rs.getString("location"));
                dto.setWebsite(rs.getString("website")); // Chuỗi JSON chứa các link

                return dto;
            }
        } catch (Exception e) {
            System.out.println("Lỗi ở getUserFullProfile: " + e.getMessage());
        } finally {
            if (rs != null) try { rs.close(); } catch (Exception e) {}
            if (st != null) try { st.close(); } catch (Exception e) {}
            if (conn != null) try { conn.close(); } catch (Exception e) {}
        }
        return null;
    }

    // 6. Hàm cập nhật profile (không thay đổi)
    public boolean updateProfile(long userId, String username, String bio, String location, String websiteJson) {
        Connection conn = null;
        PreparedStatement st1 = null;
        PreparedStatement stCheck = null;
        PreparedStatement st2 = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            // 1. Cập nhật bảng Users (luôn tồn tại)
            String sqlUser = "UPDATE Users SET username = ? WHERE user_id = ?";
            st1 = conn.prepareStatement(sqlUser);
            st1.setString(1, username);
            st1.setLong(2, userId);
            st1.executeUpdate();

            // 2. Kiểm tra xem user đã có bản ghi trong User_Profile chưa
            String sqlCheckStr = "SELECT COUNT(*) FROM User_Profile WHERE user_id = ?";
            boolean exists = false;
            stCheck = conn.prepareStatement(sqlCheckStr);
            stCheck.setLong(1, userId);
            rs = stCheck.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                exists = true;
            }

            // 3. Thực hiện INSERT hoặc UPDATE tùy vào kết quả kiểm tra
            String sqlProfile;
            if (exists) {
                sqlProfile = "UPDATE User_Profile SET bio = ?, location = ?, website = ? WHERE user_id = ?";
            } else {
                sqlProfile = "INSERT INTO User_Profile (bio, location, website, user_id) VALUES (?, ?, ?, ?)";
            }

            st2 = conn.prepareStatement(sqlProfile);
            st2.setString(1, bio);
            st2.setString(2, location);
            st2.setString(3, websiteJson);
            st2.setLong(4, userId);
            st2.executeUpdate();

            conn.commit();
            return true;
        } catch (Exception e) {
            if (conn != null) try { conn.rollback(); } catch (Exception ex) {}
            System.out.println("Lỗi ở updateProfile: " + e.getMessage());
        } finally {
            if (rs != null) try { rs.close(); } catch (Exception e) {}
            if (st1 != null) try { st1.close(); } catch (Exception e) {}
            if (stCheck != null) try { stCheck.close(); } catch (Exception e) {}
            if (st2 != null) try { st2.close(); } catch (Exception e) {}
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (Exception e) {}
        }
        return false;
    }
}
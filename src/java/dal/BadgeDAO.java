/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import config.DBContext;
import dto.BadgeDTO;
import dto.PrivilegeDTO;
import dto.ReputationDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author nguye
 */
public class BadgeDAO extends DBContext {

    // 1. Lấy lịch sử thay đổi điểm uy tín (Tab Reputation)
    public List<ReputationDTO> getReputationHistory(long userId) {
        List<ReputationDTO> list = new ArrayList<>();
        
        // FIX BUG: Dùng Alias (AS) để map cột 'event_type' thành 'action_type' 
        // và 'delta' thành 'value' cho khớp với ReputationDTO và bảng trong Database
        String sql = "SELECT event_type AS action_type, delta AS value, created_at "
                   + "FROM Reputation_History "
                   + "WHERE user_id = ? "
                   + "ORDER BY created_at DESC";
                   
        try (Connection conn = getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                list.add(new ReputationDTO(
                        rs.getString("action_type"),
                        rs.getInt("value"),
                        rs.getTimestamp("created_at")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. Lấy danh sách huy hiệu của user (Tab Badges)
    public List<BadgeDTO> getUserBadgesByReputation(long userReputation, String sort) {
        List<BadgeDTO> list = new ArrayList<>();

        // "newest" bây giờ sẽ ưu tiên hiển thị các huy hiệu khó đạt nhất (yêu cầu điểm cao nhất) lên trước
        String orderBy = "ORDER BY required_reputation DESC";

        // Nếu user chọn lọc theo tên Alphabet
        if ("name".equals(sort)) {
            orderBy = "ORDER BY name ASC";
        }

        // Truy vấn trực tiếp vào bảng Badges, không cần đi qua User_Badges
        String sql = "SELECT name, type, description, required_reputation "
                + "FROM Badges "
                + "WHERE required_reputation <= ? "
                + orderBy;

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, userReputation); // Truyền điểm của user vào để so sánh
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                BadgeDTO badge = new BadgeDTO();
                badge.setName(rs.getString("name"));
                badge.setType(rs.getString("type"));
                badge.setDescription(rs.getString("description"));
                badge.setRequiredReputation(rs.getInt("required_reputation"));

                badge.setEarnedAt(null);

                list.add(badge);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 3. Đếm số lượng huy hiệu theo loại (Dùng cho tab Summary)
    // Trả về Map chứa số lượng: vd. {"Gold": 1, "Silver": 3, "Bronze": 5}
    public Map<String, Integer> getBadgeCounts(long userId) {
        Map<String, Integer> counts = new HashMap<>();
        // Khởi tạo mặc định
        counts.put("gold", 0);
        counts.put("rilver", 0);
        counts.put("bronze", 0);

        String sql = "SELECT b.type, COUNT(*) as count "
                + "FROM User_Badges ub "
                + "JOIN Badges b ON ub.badge_id = b.badge_id "
                + "WHERE ub.user_id = ? "
                + "GROUP BY b.type";
        try {
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String type = rs.getString("type").toLowerCase();
                counts.put(type, rs.getInt("count"));
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return counts;
    }

    // 4. Lấy danh sách Quyền hạn (Privileges) từ bảng System_Rules
    public List<PrivilegeDTO> getAllPrivileges() {
        List<PrivilegeDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM Privileges ORDER BY required_reputation ASC";

        try {
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new PrivilegeDTO(
                        rs.getInt("privilege_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getInt("required_reputation")
                ));
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<BadgeDTO> getAllBadgesForAdmin(String search, String typeFilter) {
        List<BadgeDTO> list = new ArrayList<>();
        // Truy vấn vào bảng Badges gốc (thay vì bảng User_Badges)
        String sql = "SELECT * FROM Badges WHERE name LIKE ?";
        if (typeFilter != null && !typeFilter.isEmpty()) {
            sql += " AND type = ?";
        }

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + (search != null ? search : "") + "%");
            if (typeFilter != null && !typeFilter.isEmpty()) {
                ps.setString(2, typeFilter);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                BadgeDTO b = new BadgeDTO();
                b.setBadgeId(rs.getInt("badge_id"));
                b.setName(rs.getString("name")); // Map 'name' từ DB vào 'badgeName' của DTO
                b.setDescription(rs.getString("description"));
                b.setType(rs.getString("type"));
                b.setRequiredReputation(rs.getInt("required_reputation"));
                list.add(b);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Thêm badge mới
    public boolean insertBadge(BadgeDTO badge) {
        String sql = "INSERT INTO Badges (name, type, description, required_reputation) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, badge.getName());
            ps.setString(2, badge.getType());
            ps.setString(3, badge.getDescription());
            ps.setInt(4, badge.getRequiredReputation());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Cập nhật badge
    public boolean updateBadge(BadgeDTO badge) {
        String sql = "UPDATE Badges SET name=?, type=?, description=?, required_reputation=? WHERE badge_id=?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, badge.getName());
            ps.setString(2, badge.getType());
            ps.setString(3, badge.getDescription());
            ps.setInt(4, badge.getRequiredReputation());
            ps.setInt(5, badge.getBadgeId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Xóa badge
    public boolean deleteBadge(int badgeId) {
        String sql = "DELETE FROM Badges WHERE badge_id=?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, badgeId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Lấy thông tin 1 badge để đưa lên form Edit
    public BadgeDTO getBadgeById(int badgeId) {
        String sql = "SELECT * FROM Badges WHERE badge_id=?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, badgeId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                BadgeDTO b = new BadgeDTO();
                b.setBadgeId(rs.getInt("badge_id"));
                b.setName(rs.getString("name"));
                b.setType(rs.getString("type"));
                b.setDescription(rs.getString("description"));
                b.setRequiredReputation(rs.getInt("required_reputation"));
                return b;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

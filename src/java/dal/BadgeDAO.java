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
        String sql = "SELECT action_type, value, created_at "
                + "FROM Reputation_History "
                + "WHERE user_id = ? "
                + "ORDER BY created_at DESC";
        try {
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new ReputationDTO(
                        rs.getString("action_type"),
                        rs.getInt("value"),
                        rs.getTimestamp("created_at")
                ));
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. Lấy danh sách huy hiệu của user (Tab Badges)
    public List<BadgeDTO> getUserBadges(long userId) {
        List<BadgeDTO> list = new ArrayList<>();
        String sql = "SELECT b.name, b.type, b.description, ub.created_at "
                + "FROM User_Badges ub "
                + "JOIN Badges b ON ub.badge_id = b.badge_id "
                + "WHERE ub.user_id = ? "
                + "ORDER BY ub.created_at DESC";
        try {
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new BadgeDTO(
                        rs.getString("name"),
                        rs.getString("type"),
                        rs.getString("description"),
                        rs.getTimestamp("created_at")
                ));
            }
            conn.close();
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
}

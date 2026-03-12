package dal;

import config.DBContext;
import dto.SystemRuleDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SystemRuleDAO {
    private final DBContext db = new DBContext();

    public int getRuleCount() {
        String sql = "SELECT COUNT(*) FROM System_Rules";
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<SystemRuleDTO> getAllRules(int page, int pageSize) {
        List<SystemRuleDTO> rules = new ArrayList<>();
        String sql = "SELECT r.rule_id, r.title, r.content, r.created_at, r.updated_at, " +
                     "r.created_by, r.updated_by, u.username as createdByUsername " +
                     "FROM System_Rules r " +
                     "INNER JOIN Users u ON r.created_by = u.user_id " +
                     "ORDER BY r.created_at DESC " +
                     "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, (page - 1) * pageSize);
            ps.setInt(2, pageSize);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rules.add(mapResultSetToDTO(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rules;
    }

    public SystemRuleDTO getRuleById(long ruleId) {
        String sql = "SELECT r.rule_id, r.title, r.content, r.created_at, r.updated_at, " +
                     "r.created_by, r.updated_by, u.username as createdByUsername " +
                     "FROM System_Rules r " +
                     "INNER JOIN Users u ON r.created_by = u.user_id " +
                     "WHERE r.rule_id = ?";

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, ruleId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDTO(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean createRule(String title, String content, long createdBy) {
        String sql = "INSERT INTO System_Rules (title, content, created_by) VALUES (?, ?, ?)";

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, content);
            ps.setLong(3, createdBy);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateRule(long ruleId, String title, String content, long updatedBy) {
        String sql = "UPDATE System_Rules SET title = ?, content = ?, " +
                     "updated_at = GETDATE(), updated_by = ? WHERE rule_id = ?";

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, content);
            ps.setLong(3, updatedBy);
            ps.setLong(4, ruleId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteRule(long ruleId) {
        String sql = "DELETE FROM System_Rules WHERE rule_id = ?";

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, ruleId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<SystemRuleDTO> searchRules(String keyword, int limit) {
        List<SystemRuleDTO> rules = new ArrayList<>();
        String sql = "SELECT TOP (?) r.rule_id, r.title, r.content, r.created_at, r.updated_at, " +
                     "r.created_by, r.updated_by, u.username as createdByUsername " +
                     "FROM System_Rules r " +
                     "INNER JOIN Users u ON r.created_by = u.user_id " +
                     "WHERE r.title LIKE ? " +
                     "ORDER BY r.created_at DESC";

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setString(2, "%" + keyword + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rules.add(mapResultSetToDTO(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rules;
    }

    private SystemRuleDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        SystemRuleDTO rule = new SystemRuleDTO();
        rule.setRuleId(rs.getLong("rule_id"));
        rule.setTitle(rs.getString("title"));
        rule.setContent(rs.getString("content"));
        rule.setCreatedAt(rs.getTimestamp("created_at"));
        rule.setUpdatedAt(rs.getTimestamp("updated_at"));
        rule.setCreatedBy(rs.getLong("created_by"));

        long updatedBy = rs.getLong("updated_by");
        rule.setUpdatedBy(rs.wasNull() ? null : updatedBy);

        rule.setCreatedByUsername(rs.getString("createdByUsername"));
        return rule;
    }
}
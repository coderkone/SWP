package dal;

import config.DBContext;
<<<<<<< Updated upstream
=======
import model.Tag;
>>>>>>> Stashed changes
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
<<<<<<< Updated upstream
import java.util.HashMap;
import java.util.List;
import java.util.Map;
=======
import java.util.List;
>>>>>>> Stashed changes

public class TagDAO {
    private final DBContext db = new DBContext();

<<<<<<< Updated upstream
    public List<Map<String, Object>> getAllTags() throws Exception {
        List<Map<String, Object>> tags = new ArrayList<>();
        String sql = "SELECT t.tag_id, t.tag_name, COUNT(qt.question_id) AS question_count " +
                     "FROM Tags t LEFT JOIN Question_Tags qt ON t.tag_id = qt.tag_id " +
                     "GROUP BY t.tag_id, t.tag_name " +
                     "ORDER BY question_count DESC";
        
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Map<String, Object> tag = new HashMap<>();
                tag.put("tagId", rs.getLong("tag_id"));
                tag.put("tagName", rs.getString("tag_name"));
                tag.put("questionCount", rs.getInt("question_count"));
                tags.add(tag);
=======
    // Helper method để map ResultSet sang Object Tag
    private Tag mapTag(ResultSet rs) throws Exception {
        Tag tag = new Tag();
        tag.setTagId(rs.getLong("tag_id"));
        tag.setTagName(rs.getString("tag_name"));
        tag.setDescription(rs.getString("description"));
        tag.setIsActive(rs.getBoolean("IsActive"));
        // Kiểm tra xem cột question_count có tồn tại trong query không
        try {
            tag.setQuestionCount(rs.getInt("question_count"));
        } catch (Exception e) {
            // Bỏ qua nếu query không có count
        }
        return tag;
    }

    public List<Tag> getAllTags() throws Exception {
        List<Tag> tags = new ArrayList<>();
        // Chỉ lấy những Tag đang Active
        String sql = "SELECT t.tag_id, t.tag_name, t.description, t.IsActive, " +
                     "COUNT(qt.question_id) AS question_count " +
                     "FROM Tags t LEFT JOIN Question_Tags qt ON t.tag_id = qt.tag_id " +
                     "WHERE t.IsActive = 1 " + 
                     "GROUP BY t.tag_id, t.tag_name, t.description, t.IsActive " +
                     "ORDER BY question_count DESC";

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                tags.add(mapTag(rs));
>>>>>>> Stashed changes
            }
        }
        return tags;
    }

<<<<<<< Updated upstream
    public List<Map<String, Object>> getTagsByName(String searchName) throws Exception {
        List<Map<String, Object>> tags = new ArrayList<>();
        String sql = "SELECT t.tag_id, t.tag_name, COUNT(qt.question_id) AS question_count " +
                     "FROM Tags t LEFT JOIN Question_Tags qt ON t.tag_id = qt.tag_id " +
                     "WHERE t.tag_name LIKE ? " +
                     "GROUP BY t.tag_id, t.tag_name " +
                     "ORDER BY question_count DESC";
        
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + searchName + "%");
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> tag = new HashMap<>();
                    tag.put("tagId", rs.getLong("tag_id"));
                    tag.put("tagName", rs.getString("tag_name"));
                    tag.put("questionCount", rs.getInt("question_count"));
                    tags.add(tag);
=======
    public List<Tag> getTagsByName(String searchName) throws Exception {
        List<Tag> tags = new ArrayList<>();
        String sql = "SELECT t.tag_id, t.tag_name, t.description, t.IsActive, " +
                     "COUNT(qt.question_id) AS question_count " +
                     "FROM Tags t LEFT JOIN Question_Tags qt ON t.tag_id = qt.tag_id " +
                     "WHERE t.tag_name LIKE ? AND t.IsActive = 1 " +
                     "GROUP BY t.tag_id, t.tag_name, t.description, t.IsActive " +
                     "ORDER BY question_count DESC";

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + searchName + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tags.add(mapTag(rs));
>>>>>>> Stashed changes
                }
            }
        }
        return tags;
    }
<<<<<<< Updated upstream
}
=======
}
>>>>>>> Stashed changes

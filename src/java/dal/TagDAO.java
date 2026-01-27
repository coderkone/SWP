package dal;

import config.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TagDAO {
    private final DBContext db = new DBContext();

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
            }
        }
        return tags;
    }

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
                }
            }
        }
        return tags;
    }
}

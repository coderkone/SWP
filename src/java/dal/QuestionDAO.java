package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import config.DBContext;
import dto.QuestionDTO;

public class QuestionDAO {

    private final DBContext db = new DBContext();

    public long createQuestion(long userId, String title, String body, String codeSnippet, String tagsStr) throws Exception {
        String sql = "INSERT INTO Questions (user_id, title, body, code_snippet, view_count, is_closed, created_at, updated_at, Score) " +
                     "VALUES (?, ?, ?, ?, 0, 0, GETDATE(), GETDATE(), 0)";
        
        long questionId = -1;
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, userId);
            ps.setString(2, title);
            ps.setString(3, body);
            ps.setString(4, codeSnippet);
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    questionId = rs.getLong(1);
                }
            }
        }
        
        // Add tags if provided
        if (questionId > 0 && tagsStr != null && !tagsStr.trim().isEmpty()) {
            addTagsToQuestion(questionId, tagsStr);
        }
        
        return questionId;
    }
    
    private void addTagsToQuestion(long questionId, String tagsStr) throws Exception {
        String[] tagNames = tagsStr.split(",");
        
        try (Connection con = db.getConnection()) {
            // Insert or get existing tags
            for (String tagName : tagNames) {
                tagName = tagName.trim();
                if (tagName.isEmpty()) continue;
                
                // Get or create tag
                long tagId = getOrCreateTag(con, tagName);
                
                // Link tag to question
                String linkSql = "INSERT INTO Question_Tags (question_id, tag_id) VALUES (?, ?)";
                try (PreparedStatement ps = con.prepareStatement(linkSql)) {
                    ps.setLong(1, questionId);
                    ps.setLong(2, tagId);
                    ps.executeUpdate();
                }
            }
        }
    }
    
    private long getOrCreateTag(Connection con, String tagName) throws Exception {
        // Check if tag exists
        String selectSql = "SELECT tag_id FROM Tags WHERE tag_name = ?";
        try (PreparedStatement ps = con.prepareStatement(selectSql)) {
            ps.setString(1, tagName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("tag_id");
                }
            }
        }
        
        // Create new tag
        String insertSql = "INSERT INTO Tags (tag_name) VALUES (?)";
        try (PreparedStatement ps = con.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, tagName);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        return -1;
    }
    
    public List<String> getTagsByQuestionId(long questionId) throws Exception {
        List<String> tags = new ArrayList<>();
        String sql = "SELECT t.tag_name FROM Tags t " +
                     "JOIN Question_Tags qt ON t.tag_id = qt.tag_id " +
                     "WHERE qt.question_id = ?";
        
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, questionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tags.add(rs.getString("tag_name"));
                }
            }
        }
        return tags;
    }

    public QuestionDTO getQuestionById(long questionId) throws Exception {
        String sql = "SELECT q.question_id, q.user_id, q.title, q.body, q.code_snippet, q.view_count, " +
                     "q.is_closed, q.closed_reason, q.created_at, q.updated_at, q.Score, u.username " +
                     "FROM Questions q JOIN Users u ON q.user_id = u.user_id WHERE q.question_id = ?";
        
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, questionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    QuestionDTO q = new QuestionDTO();
                    q.setQuestionId(rs.getLong("question_id"));
                    q.setUserId(rs.getLong("user_id"));
                    q.setTitle(rs.getString("title"));
                    q.setBody(rs.getString("body"));
                    q.setCodeSnippet(rs.getString("code_snippet"));
                    q.setViewCount(rs.getInt("view_count"));
                    q.setIsClosed(rs.getBoolean("is_closed"));
                    q.setClosedReason(rs.getString("closed_reason"));
                    q.setCreatedAt(rs.getString("created_at"));
                    q.setUpdatedAt(rs.getString("updated_at"));
                    q.setScore(rs.getInt("Score"));
                    q.setUsername(rs.getString("username"));
                    
                    // Get tags
                    List<String> tags = getTagsByQuestionId(questionId);
                    q.setTags(tags);
                    
                    return q;
                }
            }
        }
        return null;
    }

    public void incrementViewCount(long questionId) throws Exception {
        String sql = "UPDATE Questions SET view_count = view_count + 1 WHERE question_id = ?";
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, questionId);
            ps.executeUpdate();
        }
    }
}

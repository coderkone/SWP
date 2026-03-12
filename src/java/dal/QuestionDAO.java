package dal;

import config.DBContext;
import dto.QuestionDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class QuestionDAO extends DBContext {

    // 1. Hàm chính lấy danh sách câu hỏi (Search, Filter, Sort, Paging)
    public List<QuestionDTO> getQuestions(int pageIndex, int pageSize, String sortBy, String keyword, String filterType) {
        List<QuestionDTO> list = new ArrayList<>();
        
        // Dùng StringBuilder để nối chuỗi tối ưu hơn
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT q.*, u.username, u.Reputation AS author_reputation, up.avatar_url, ")
           .append("(SELECT COUNT(*) FROM Answers a WHERE a.question_id = q.question_id) as ans_count ")
           .append("FROM Questions q ")
           .append("JOIN Users u ON q.user_id = u.user_id ")
           .append("LEFT JOIN User_Profile up ON u.user_id = up.user_id ")
           .append("WHERE 1=1 ");

        // Xử lý từ khóa tìm kiếm (Search)
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (q.title LIKE ? OR q.body LIKE ?) ");
        }

        // Xử lý bộ lọc (Filter)
        if ("unanswered".equals(filterType)) {
            sql.append(" AND (SELECT COUNT(*) FROM Answers a WHERE a.question_id = q.question_id) = 0 ");
        }

        // Xử lý sắp xếp (Sorting) - Đã thêm case 'voted'
        if ("views".equals(sortBy)) {
            sql.append(" ORDER BY q.view_count DESC ");
        } else if ("active".equals(sortBy)) {
            sql.append(" ORDER BY q.updated_at DESC "); 
        } else if ("voted".equals(sortBy)) {
            sql.append(" ORDER BY q.Score DESC "); // Sắp xếp điểm cao nhất
        } else {
            sql.append(" ORDER BY q.created_at DESC "); // Mặc định là mới nhất
        }

        // Phân trang
        sql.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try {
            Connection conn = getConnection();
            PreparedStatement st = conn.prepareStatement(sql.toString());
            int paramIndex = 1;

            // Set tham số cho Search
            if (keyword != null && !keyword.trim().isEmpty()) {
                st.setString(paramIndex++, "%" + keyword + "%");
                st.setString(paramIndex++, "%" + keyword + "%");
            }

            // Set tham số cho Phân trang
            st.setInt(paramIndex++, (pageIndex - 1) * pageSize);
            st.setInt(paramIndex++, pageSize);

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                QuestionDTO q = mapRow(rs); 
                list.add(q);
            }
            rs.close();
            st.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    // 2. Hàm hỗ trợ Map dữ liệu từ ResultSet sang Object (Giúp code gọn hơn)
    private QuestionDTO mapRow(ResultSet rs) throws SQLException {
        QuestionDTO q = new QuestionDTO();
        q.setQuestionId(rs.getLong("question_id"));
        q.setUserId(rs.getLong("user_id"));
        q.setTitle(rs.getString("title"));
        q.setBody(rs.getString("body"));
        q.setViewCount(rs.getInt("view_count"));
        q.setIsClosed(rs.getBoolean("is_closed"));
        q.setClosedReason(rs.getString("closed_reason"));
        long closedBy = rs.getLong("closed_by");
        if (!rs.wasNull()) {
            q.setClosedBy(closedBy);
        }
        q.setClosedAt(rs.getTimestamp("closed_at"));
        q.setScore(rs.getInt("Score"));
        q.setCreatedAt(rs.getTimestamp("created_at"));
        
        q.setAuthorName(rs.getString("username"));
        q.setAuthorReputation(rs.getInt("author_reputation"));
        q.setAuthorAvatar(rs.getString("avatar_url"));
        q.setAnswerCount(rs.getInt("ans_count"));
        
        // Tự động lấy Tags cho câu hỏi này luôn
        q.setTags(getTagsByQuestionId(q.getQuestionId())); 
        
        return q;
    }

    // 3. Hàm đếm tổng số câu hỏi (Dùng cho phân trang)
    public int getTotalQuestions(String keyword, String filterType) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Questions q WHERE 1=1 ");
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (q.title LIKE ? OR q.body LIKE ?) ");
        }
        if ("unanswered".equals(filterType)) {
             sql.append(" AND (SELECT COUNT(*) FROM Answers a WHERE a.question_id = q.question_id) = 0 ");
        }

        try {
            Connection conn = getConnection();
            PreparedStatement st = conn.prepareStatement(sql.toString());
            int paramIndex = 1;
            
            if (keyword != null && !keyword.trim().isEmpty()) {
                st.setString(paramIndex++, "%" + keyword + "%");
                st.setString(paramIndex++, "%" + keyword + "%");
            }
            
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    // 4. Hàm lấy danh sách Tags của 1 câu hỏi
    public List<String> getTagsByQuestionId(long questionId) {
        List<String> tags = new ArrayList<>();
        String sql = "SELECT t.tag_name FROM Tags t " +
                     "JOIN Question_Tags qt ON t.tag_id = qt.tag_id " +
                     "WHERE qt.question_id = ?";
        try {
            Connection conn = getConnection();
            PreparedStatement st = conn.prepareStatement(sql);
            st.setLong(1, questionId);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                tags.add(rs.getString("tag_name"));
            }
            rs.close();
            st.close();
            conn.close(); 
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tags;
    }
    
    // 5. Hàm thêm Câu hỏi mới kèm Tags (Sử dụng Transaction)
    // Hàm quản lý transaction
    public boolean insertQuestionWithTags(long userId, String title, String body, String tagsInput, int userReputation) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Bắt đầu Transaction

            // Bước 1: Gọi hàm phụ để Insert Question
            long questionId = insertQuestionCore(conn, userId, title, body);

            // Bước 2: Gọi hàm phụ để xử lý Tags, truyền thêm userReputation vào
            if (questionId != -1 && tagsInput != null && !tagsInput.trim().isEmpty()) {
                processTagsForQuestion(conn, questionId, tagsInput, userReputation);
            }

            conn.commit(); // Thành công thì lưu
            return true;

        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            // Ném lỗi ngược lên Controller để nó biết tại sao lỗi (do database hay do điểm uy tín)
            throw e; 
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    // insert câu hỏi mới bảng Questions
    private long insertQuestionCore(Connection conn, long userId, String title, String body) throws SQLException {
        String sql = "INSERT INTO Questions (user_id, title, body) VALUES (?, ?, ?)";
        // Dùng try-with-resources để tự động đóng PreparedStatement và ResultSet
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, userId);
            ps.setString(2, title);
            ps.setString(3, body);
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        return -1;
    }
    // Hàm kiểm tra tag
    public List<String> findNewTags(String tagsInput) {
        List<String> newTags = new ArrayList<>();
        if (tagsInput == null || tagsInput.trim().isEmpty()) return newTags;

        String[] tagsArray = tagsInput.split(",");

        try (Connection conn = getConnection()) {
            for (String tag : tagsArray) {
                String tagName = tag.trim().toLowerCase();
                if (tagName.isEmpty()) continue;

                // Reuse helper method
                long tagId = getTagIdIfExists(conn, tagName);
                if (tagId == -1) {
                    newTags.add(tagName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newTags;
    }
    
    // Helper method: Check if tag exists and return tagId, or -1 if not exists
    private long getTagIdIfExists(Connection conn, String tagName) throws SQLException {
        String sql = "SELECT tag_id FROM Tags WHERE tag_name = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tagName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("tag_id");
                }
            }
        }
        return -1;
    }
    // Xử lý tag và check Reputation
    private void processTagsForQuestion(Connection conn, long questionId, String tagsInput, int userReputation) throws Exception {
        String[] tagsArray = tagsInput.split(",");
        String sqlInsertTag = "INSERT INTO Tags (tag_name) VALUES (?)";
        String sqlInsertQT = "INSERT INTO Question_Tags (question_id, tag_id) VALUES (?, ?)";

        try (PreparedStatement psInsertTag = conn.prepareStatement(sqlInsertTag, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement psInsertQT = conn.prepareStatement(sqlInsertQT)) {

            for (String tag : tagsArray) {
                String tagName = tag.trim().toLowerCase();
                if (tagName.isEmpty()) continue;

                // Reuse helper method to check if tag exists
                long tagId = getTagIdIfExists(conn, tagName);

                // B. Nếu là TAG MỚI HOÀN TOÀN -> Bắt đầu check uy tín
                if (tagId == -1) {
                    // Giả sử mốc uy tín cần thiết là 50 điểm
                    if (userReputation < 50) {
                        // Ném ra Exception để Rollback toàn bộ và báo lỗi
                        throw new Exception("NOT_ENOUGH_REP:" + tagName);
                    }

                    // Nếu đủ điểm uy tín thì mới cho tạo Tag mới
                    psInsertTag.setString(1, tagName);
                    psInsertTag.executeUpdate();
                    try (ResultSet rsNew = psInsertTag.getGeneratedKeys()) {
                        if (rsNew.next()) {
                            tagId = rsNew.getLong(1);
                        }
                    }
                }

                // C. Link Question và Tag
                if (tagId != -1) {
                    psInsertQT.setLong(1, questionId);
                    psInsertQT.setLong(2, tagId);
                    psInsertQT.executeUpdate();
                }
            }
        }
    }
    
    // 6. Lấy câu hỏi theo ID
    public QuestionDTO getQuestionById(long questionId) throws Exception {      
        String sql = "SELECT q.*, u.username, u.Reputation AS author_reputation FROM Questions q " +
                "JOIN Users u ON q.user_id = u.user_id WHERE q.question_id = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, questionId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    QuestionDTO q = mapQuestion(rs);
                    q.setTags(getTagsByQuestionId(questionId));
                    return q;
                }
            }
        }
        return null;
    }
    
    // 7. Tăng lượt xem câu hỏi
    public void incrementViewCount(long questionId) throws Exception {
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE Questions SET view_count = view_count + 1 WHERE question_id = ?")) {
            ps.setLong(1, questionId);
            ps.executeUpdate();
        }
    }

    public boolean deleteQuestion(long questionId) throws Exception {
        String sql = "DELETE FROM Questions WHERE question_id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, questionId);
            return ps.executeUpdate() > 0;
        }
    }
    
    // 8. Toggle accept answer (chuyển đổi câu trả lời được chấp nhận)
    public boolean toggleAcceptAnswer(long questionId, long answerId, long questionOwnerId) throws Exception {
        QuestionDTO q = getQuestionById(questionId);
        if (q == null || q.getUserId() != questionOwnerId) return false;        
        Long current = q.getAcceptedAnswerId();
        Long newValue = (current != null && current == answerId) ? null : answerId;
        return setAcceptedAnswer(questionId, newValue);
    }
    
    // 9. Lấy các câu hỏi liên quan (cùng tags)
    public List<QuestionDTO> getRelatedQuestions(long questionId, int limit) throws Exception {
        List<QuestionDTO> relatedQuestions = new ArrayList<>();
        String sql = "SELECT TOP (?) q.*, u.username, u.Reputation AS author_reputation FROM Questions q " +       
                "JOIN Users u ON q.user_id = u.user_id " +
                "WHERE q.question_id IN (" +
                "  SELECT DISTINCT q2.question_id FROM Questions q2 " +
                "  JOIN Question_Tags qt2 ON q2.question_id = qt2.question_id " +
                "  WHERE qt2.tag_id IN (" +
                "    SELECT qt.tag_id FROM Question_Tags qt WHERE qt.question_id = ?" +
                "  ) AND q2.question_id != ?" +
                ") ORDER BY q.created_at DESC";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setLong(2, questionId);
            ps.setLong(3, questionId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    QuestionDTO q = mapQuestion(rs);
                    q.setTags(getTagsByQuestionId(q.getQuestionId()));
                    relatedQuestions.add(q);
                }
            }
        }
        return relatedQuestions;
    }
    
    // Helper: Map ResultSet sang QuestionDTO (cho getQuestionById)
    private QuestionDTO mapQuestion(ResultSet rs) throws SQLException {
        QuestionDTO q = new QuestionDTO();
        q.setQuestionId(rs.getLong("question_id"));
        q.setUserId(rs.getLong("user_id"));
        q.setTitle(rs.getString("title"));
        q.setBody(rs.getString("body"));
        q.setViewCount(rs.getInt("view_count"));
        q.setIsClosed(rs.getBoolean("is_closed"));
        q.setClosedReason(rs.getString("closed_reason"));
        long closedBy = rs.getLong("closed_by");
        if (!rs.wasNull()) {
            q.setClosedBy(closedBy);
        }
        q.setClosedAt(rs.getTimestamp("closed_at"));
        q.setScore(rs.getInt("Score"));
        q.setCreatedAt(rs.getTimestamp("created_at"));
        q.setUpdatedAt(rs.getTimestamp("updated_at"));
        q.setAuthorName(rs.getString("username"));
        q.setAuthorReputation(rs.getInt("author_reputation"));
        
        // Lấy accepted_answer_id nếu tồn tại
        long acceptedId = rs.getLong("accepted_answer_id");
        if (!rs.wasNull()) {
            q.setAcceptedAnswerId(acceptedId);
        }
        
        return q;
    }
    
    // Helper: Set accepted answer (helper cho toggleAcceptAnswer)
    private boolean setAcceptedAnswer(long questionId, Long answerId) throws Exception {
        String sql = "UPDATE Questions SET accepted_answer_id = ? WHERE question_id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (answerId == null) {
                ps.setNull(1, java.sql.Types.BIGINT);
            } else {
                ps.setLong(1, answerId);
            }
            ps.setLong(2, questionId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateQuestionWithHistory(long questionId, long editorId,
                                             String title, String body, String codeSnippet,
                                             String tagsInput, int userReputation) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            String oldTitle = null;
            String oldBody = null;
            String oldCodeSnippet = null;

            String loadSql = "SELECT title, body, code_snippet FROM Questions WHERE question_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(loadSql)) {
                ps.setLong(1, questionId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }
                    oldTitle = rs.getString("title");
                    oldBody = rs.getString("body");
                    oldCodeSnippet = rs.getString("code_snippet");
                }
            }

            String oldTags = getTagsCsvByQuestionId(conn, questionId);
            insertEditHistory(conn, "question", questionId, oldTitle, oldBody, oldCodeSnippet, oldTags, editorId);

            String updateSql = "UPDATE Questions SET title = ?, body = ?, code_snippet = ?, updated_at = GETDATE() WHERE question_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setString(1, title);
                ps.setString(2, body);
                ps.setString(3, codeSnippet != null ? codeSnippet : "");
                ps.setLong(4, questionId);
                if (ps.executeUpdate() == 0) {
                    conn.rollback();
                    return false;
                }
            }

            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Question_Tags WHERE question_id = ?")) {
                ps.setLong(1, questionId);
                ps.executeUpdate();
            }

            if (tagsInput != null && !tagsInput.trim().isEmpty()) {
                processTagsForQuestion(conn, questionId, normalizeTags(tagsInput), userReputation);
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            throw e;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private String getTagsCsvByQuestionId(Connection conn, long questionId) throws SQLException {
        List<String> tags = new ArrayList<>();
        String sql = "SELECT t.tag_name FROM Tags t "
                + "JOIN Question_Tags qt ON t.tag_id = qt.tag_id "
                + "WHERE qt.question_id = ? ORDER BY t.tag_name";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, questionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tags.add(rs.getString("tag_name"));
                }
            }
        }
        return String.join(",", tags);
    }

    private void insertEditHistory(Connection conn, String postType, long postId,
                                   String title, String body, String codeSnippet, String tags,
                                   long editorId)
            throws SQLException {
        String editedContent = "title=" + (title != null ? title : "")
            + "\nbody=" + (body != null ? body : "")
            + "\ncode=" + (codeSnippet != null ? codeSnippet : "")
            + "\ntags=" + (tags != null ? tags : "");

        String sql = "INSERT INTO Post_Edit_History (post_type, post_id, title, body, code_snippet, tags, editor_id, edited_content, edited_at) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, GETDATE())";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, postType);
            ps.setLong(2, postId);
            ps.setString(3, title != null ? title : "");
            ps.setString(4, body != null ? body : "");
            ps.setString(5, codeSnippet != null ? codeSnippet : "");
            ps.setString(6, tags != null ? tags : "");
            ps.setLong(7, editorId);
            ps.setString(8, editedContent);
            ps.executeUpdate();
        }
    }

    private String normalizeTags(String tagsInput) {
        String[] rawTags = tagsInput.split(",");
        LinkedHashSet<String> uniqueTags = new LinkedHashSet<>();
        for (String tag : rawTags) {
            if (tag == null) {
                continue;
            }
            String clean = tag.trim().toLowerCase();
            if (!clean.isEmpty()) {
                uniqueTags.add(clean);
            }
        }
        return String.join(",", uniqueTags);
    }

    public boolean closeQuestion(long questionId, long closedByUserId, String closeReason) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            int reputation;
            String userSql = "SELECT reputation FROM Users WHERE user_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(userSql)) {
                ps.setLong(1, closedByUserId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }
                    reputation = rs.getInt("reputation");
                }
            }

            if (reputation < 3000) {
                conn.rollback();
                return false;
            }

            String closeSql = "UPDATE Questions "
                    + "SET is_closed = 1, closed_by = ?, closed_reason = ?, closed_at = GETDATE(), updated_at = GETDATE() "
                    + "WHERE question_id = ? AND (is_closed = 0 OR is_closed IS NULL)";

            int updated;
            try (PreparedStatement ps = conn.prepareStatement(closeSql)) {
                ps.setLong(1, closedByUserId);
                ps.setString(2, closeReason);
                ps.setLong(3, questionId);
                updated = ps.executeUpdate();
            }

            if (updated > 0) {
                conn.commit();
                return true;
            }

            conn.rollback();
            return false;
        } catch (Exception e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean isQuestionClosed(long questionId) throws Exception {
        String sql = "SELECT is_closed FROM Questions WHERE question_id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, questionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("is_closed");
                }
            }
        }
        return false;
    }

    public Long getQuestionIdByAnswerId(long answerId) throws Exception {
        String sql = "SELECT question_id FROM Answers WHERE answer_id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, answerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("question_id");
                }
            }
        }
        return null;
    }

}
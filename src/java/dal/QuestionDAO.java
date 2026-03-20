package dal;

import config.DBContext;
import dto.QuestionDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class QuestionDAO extends DBContext {

    // 1. Hàm chính lấy danh sách câu hỏi
    public List<QuestionDTO> getQuestions(int pageIndex, int pageSize, String sortBy, String keyword, String filterType,String tag) {
        List<QuestionDTO> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT q.*, u.username, up.avatar_url, ")
           .append("(SELECT COUNT(*) FROM Answers a WHERE a.question_id = q.question_id) as ans_count ")
           .append("FROM Questions q ")
           .append("JOIN Users u ON q.user_id = u.user_id ")
           .append("LEFT JOIN User_Profile up ON u.user_id = up.user_id ")
           .append("WHERE 1=1 ");

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (q.title LIKE ? OR q.body LIKE ?) ");
        }
        if ("unanswered".equals(filterType)) {
            sql.append(" AND (SELECT COUNT(*) FROM Answers a WHERE a.question_id = q.question_id) = 0 ");
        }
        // Logic lọc theo tag mới thêm
        if (tag != null && !tag.trim().isEmpty()) {
            sql.append(" AND q.question_id IN (SELECT qt.question_id FROM Question_Tags qt JOIN Tags t ON qt.tag_id = t.tag_id WHERE t.tag_name = ?) ");
        }

        if ("views".equals(sortBy)) {
            sql.append(" ORDER BY q.view_count DESC ");
        } else if ("active".equals(sortBy)) {
            sql.append(" ORDER BY q.updated_at DESC "); 
        } else if ("voted".equals(sortBy)) {
            sql.append(" ORDER BY q.Score DESC "); 
        } else {
            sql.append(" ORDER BY q.created_at DESC "); 
        }

        sql.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try {
            Connection conn = getConnection();
            PreparedStatement st = conn.prepareStatement(sql.toString());
            int paramIndex = 1;

            if (keyword != null && !keyword.trim().isEmpty()) {
                st.setString(paramIndex++, "%" + keyword + "%");
                st.setString(paramIndex++, "%" + keyword + "%");
            }
            // Truyền tham số cho tag
            if (tag != null && !tag.trim().isEmpty()) {
                st.setString(paramIndex++, tag);
            }

            st.setInt(paramIndex++, (pageIndex - 1) * pageSize);
            st.setInt(paramIndex++, pageSize);

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs)); 
            }
            rs.close(); st.close(); conn.close();
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
        q.setScore(rs.getInt("Score"));
        q.setCreatedAt(rs.getTimestamp("created_at"));
        
        q.setAuthorName(rs.getString("username"));
        q.setAuthorAvatar(rs.getString("avatar_url"));
        q.setAnswerCount(rs.getInt("ans_count"));
        
        // Tự động lấy Tags cho câu hỏi này luôn
        q.setTags(getTagsByQuestionId(q.getQuestionId())); 
        
        return q;
    }



    // 3. Hàm đếm tổng số câu hỏi (Dùng cho phân trang)
    public int getTotalQuestions(String keyword, String filterType, String tag) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Questions q WHERE 1=1 ");
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (q.title LIKE ? OR q.body LIKE ?) ");
        }
        if ("unanswered".equals(filterType)) {
             sql.append(" AND (SELECT COUNT(*) FROM Answers a WHERE a.question_id = q.question_id) = 0 ");
        }
        if (tag != null && !tag.trim().isEmpty()) {
            sql.append(" AND q.question_id IN (SELECT qt.question_id FROM Question_Tags qt JOIN Tags t ON qt.tag_id = t.tag_id WHERE t.tag_name = ?) ");
        }

        try {
            Connection conn = getConnection();
            PreparedStatement st = conn.prepareStatement(sql.toString());
            int paramIndex = 1;
            
            if (keyword != null && !keyword.trim().isEmpty()) {
                st.setString(paramIndex++, "%" + keyword + "%");
                st.setString(paramIndex++, "%" + keyword + "%");
            }
            if (tag != null && !tag.trim().isEmpty()) {
                st.setString(paramIndex++, tag);
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
    
    // 4. Hàm Search (Optional)
    public List<QuestionDTO> searchQuestions(String keyword) {
        return getQuestions(1, 20, "newest", keyword, "all",null);
    }
    
    // 5. Hàm lấy danh sách Tags của 1 câu hỏi
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
    
    // 6. Hàm thêm Câu hỏi mới kèm Tags (Sử dụng Transaction)
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
        String sqlCheck = "SELECT tag_id FROM Tags WHERE tag_name = ?";

        try (Connection conn = getConnection();
             PreparedStatement psCheck = conn.prepareStatement(sqlCheck)) {

            for (String tag : tagsArray) {
                String tagName = tag.trim().toLowerCase();
                if (tagName.isEmpty()) continue;

                psCheck.setString(1, tagName);
                try (ResultSet rs = psCheck.executeQuery()) {
                    // Nếu rs.next() là false nghĩa là tag này chưa có trong Database
                    if (!rs.next()) {
                        newTags.add(tagName);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newTags;
    }
    // Xử lý tag và check Reputation
    private void processTagsForQuestion(Connection conn, long questionId, String tagsInput, int userReputation) throws Exception {
        String[] tagsArray = tagsInput.split(",");
        String sqlCheck = "SELECT tag_id FROM Tags WHERE tag_name = ?";
        String sqlInsertTag = "INSERT INTO Tags (tag_name) VALUES (?)";
        String sqlInsertQT = "INSERT INTO Question_Tags (question_id, tag_id) VALUES (?, ?)";

        try (PreparedStatement psCheck = conn.prepareStatement(sqlCheck);
             PreparedStatement psInsertTag = conn.prepareStatement(sqlInsertTag, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement psInsertQT = conn.prepareStatement(sqlInsertQT)) {

            for (String tag : tagsArray) {
                String tagName = tag.trim().toLowerCase();
                if (tagName.isEmpty()) continue;

                long tagId = -1;
                
                // A. Check xem tag này đã tồn tại trong DB chưa
                psCheck.setString(1, tagName);
                try (ResultSet rsCheck = psCheck.executeQuery()) {
                    if (rsCheck.next()) {
                        tagId = rsCheck.getLong("tag_id"); // Tag cũ, ai cũng dùng được
                    }
                }

                // B. Nếu là TAG MỚI HOÀN TOÀN -> Bắt đầu check uy tín
                if (tagId == -1) {
                    // Giả sử mốc uy tín cần thiết là 50 điểm (bạn có thể thay đổi số này)
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
    
    // Lấy thông tin chi tiết một câu hỏi theo ID
    public QuestionDTO getQuestionById(long questionId) {
        String sql = "SELECT q.*, u.username, u.Reputation AS author_reputation, up.avatar_url, "
                + "(SELECT COUNT(*) FROM Answers a WHERE a.question_id = q.question_id) as ans_count "
                + "FROM Questions q "
                + "JOIN Users u ON q.user_id = u.user_id "
                + "LEFT JOIN User_Profile up ON u.user_id = up.user_id "
                + "WHERE q.question_id = ? AND ISNULL(q.is_deleted, 0) = 0";
        try {
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, questionId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                QuestionDTO q = mapRow(rs);
                rs.close(); ps.close(); conn.close();
                return q;
            }
            rs.close(); ps.close(); conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Lấy top các tag phổ biến nhất dựa trên số lượng câu hỏi
    public List<String> getPopularTags(int limit) {
        List<String> tags = new ArrayList<>();
        String sql = "SELECT t.tag_name, COUNT(qt.question_id) as count " +
                     "FROM Tags t " +
                     "JOIN Question_Tags qt ON t.tag_id = qt.tag_id " +
                     "GROUP BY t.tag_name " +
                     "ORDER BY count DESC " +
                     "OFFSET 0 ROWS FETCH NEXT ? ROWS ONLY";
        try {
            Connection conn = getConnection();
            PreparedStatement st = conn.prepareStatement(sql);
            st.setInt(1, limit);
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

    // Lấy các câu hỏi liên quan (cùng tag) với câu hỏi cho trước
    public List<QuestionDTO> getRelatedQuestions(long questionId, int limit) {
        List<QuestionDTO> list = new ArrayList<>();
        String sql = "SELECT q.*, u.username, u.Reputation AS author_reputation, up.avatar_url, "
                + "(SELECT COUNT(*) FROM Answers a WHERE a.question_id = q.question_id) as ans_count "
                + "FROM Questions q "
                + "JOIN Users u ON q.user_id = u.user_id "
                + "LEFT JOIN User_Profile up ON u.user_id = up.user_id "
                + "WHERE q.question_id != ? AND ISNULL(q.is_deleted, 0) = 0 "
                + "AND q.question_id IN ("
                + "  SELECT qt2.question_id FROM Question_Tags qt2 "
                + "  WHERE qt2.tag_id IN (SELECT qt1.tag_id FROM Question_Tags qt1 WHERE qt1.question_id = ?)"
                + ") "
                + "ORDER BY q.created_at DESC "
                + "OFFSET 0 ROWS FETCH NEXT ? ROWS ONLY";
        try {
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, questionId);
            ps.setLong(2, questionId);
            ps.setInt(3, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            rs.close(); ps.close(); conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Kiểm tra câu hỏi có bị đóng không
    public boolean isQuestionClosed(long questionId) {
        String sql = "SELECT is_closed FROM Questions WHERE question_id = ?";
        try {
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, questionId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                boolean closed = rs.getBoolean("is_closed");
                rs.close(); ps.close(); conn.close();
                return closed;
            }
            rs.close(); ps.close(); conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Cập nhật câu hỏi kèm lịch sử chỉnh sửa và xử lý tags (dùng Transaction)
    public boolean updateQuestionWithHistory(long questionId, long editorId, String title, String body, String codeSnippet, String tags, int userReputation) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            // Bước 1: Tải dữ liệu cũ để lưu vào lịch sử
            String loadSql = "SELECT title, body FROM Questions WHERE question_id = ?";
            String oldTitle = null, oldBody = null;
            try (PreparedStatement ps = conn.prepareStatement(loadSql)) {
                ps.setLong(1, questionId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }
                    oldTitle = rs.getString("title");
                    oldBody = rs.getString("body");
                }
            }

            // Bước 2: Lưu lịch sử chỉnh sửa
            String oldTags = String.join(",", getTagsByQuestionId(questionId));
            String editedContent = "title=" + (oldTitle != null ? oldTitle : "")
                    + "\nbody=" + (oldBody != null ? oldBody : "")
                    + "\ntags=" + oldTags;
            String historySql = "INSERT INTO Post_Edit_History (post_type, post_id, title, body, code_snippet, tags, editor_id, edited_content, edited_at) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, GETDATE())";
            try (PreparedStatement ps = conn.prepareStatement(historySql)) {
                ps.setString(1, "question");
                ps.setLong(2, questionId);
                ps.setString(3, oldTitle != null ? oldTitle : "");
                ps.setString(4, oldBody != null ? oldBody : "");
                ps.setString(5, "");
                ps.setString(6, oldTags);
                ps.setLong(7, editorId);
                ps.setString(8, editedContent);
                ps.executeUpdate();
            }

            // Bước 3: Cập nhật nội dung câu hỏi
            String updateSql = "UPDATE Questions SET title = ?, body = ?, updated_at = GETDATE() WHERE question_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setString(1, title);
                ps.setString(2, body);
                ps.setLong(3, questionId);
                if (ps.executeUpdate() == 0) {
                    conn.rollback();
                    return false;
                }
            }

            // Bước 4: Cập nhật tags nếu được cung cấp
            if (tags != null && !tags.trim().isEmpty()) {
                String deleteTagsSql = "DELETE FROM Question_Tags WHERE question_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(deleteTagsSql)) {
                    ps.setLong(1, questionId);
                    ps.executeUpdate();
                }
                processTagsForQuestion(conn, questionId, tags, userReputation);
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
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

    // Toggle chấp nhận câu trả lời: nếu đã accepted thì bỏ chọn, nếu chưa thì chọn.
    // Đồng thời cộng/trừ điểm reputation cho tác giả câu trả lời.
    public boolean toggleAcceptAnswer(long questionId, long answerId, long questionOwnerId) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            // Lấy accepted_answer_id hiện tại và tác giả câu trả lời
            Long currentAccepted = null;
            long answerAuthorId = -1;

            String checkSql = "SELECT q.accepted_answer_id, a.user_id AS answer_author_id "
                    + "FROM Questions q JOIN Answers a ON a.answer_id = ? "
                    + "WHERE q.question_id = ? AND q.user_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
                ps.setLong(1, answerId);
                ps.setLong(2, questionId);
                ps.setLong(3, questionOwnerId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }
                    long val = rs.getLong("accepted_answer_id");
                    currentAccepted = rs.wasNull() ? null : val;
                    answerAuthorId = rs.getLong("answer_author_id");
                }
            }

            boolean isToggleOff = currentAccepted != null && currentAccepted == answerId;
            Long newAccepted = isToggleOff ? null : answerId;

            // Cập nhật accepted_answer_id
            String updateSql = "UPDATE Questions SET accepted_answer_id = ? WHERE question_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                if (newAccepted == null) {
                    ps.setNull(1, java.sql.Types.BIGINT);
                } else {
                    ps.setLong(1, newAccepted);
                }
                ps.setLong(2, questionId);
                ps.executeUpdate();
            }

            // Cập nhật cột is_accepted trên bảng Answers
            if (!isToggleOff && currentAccepted != null) {
                // Bỏ accepted cũ
                String clearOld = "UPDATE Answers SET is_accepted = 0 WHERE answer_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(clearOld)) {
                    ps.setLong(1, currentAccepted);
                    ps.executeUpdate();
                }
            }
            String setNew = "UPDATE Answers SET is_accepted = ? WHERE answer_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(setNew)) {
                ps.setBoolean(1, !isToggleOff);
                ps.setLong(2, answerId);
                ps.executeUpdate();
            }

            // Cộng/trừ reputation +15 cho tác giả câu trả lời
            int reputationDelta = isToggleOff ? -15 : 15;
            String reputationSql = "UPDATE Users SET Reputation = Reputation + ? WHERE user_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(reputationSql)) {
                ps.setInt(1, reputationDelta);
                ps.setLong(2, answerAuthorId);
                ps.executeUpdate();
            }

            // Ghi lịch sử reputation (best-effort)
            String logSql = "INSERT INTO Reputation_History (user_id, delta, reason, event_type, related_post_type, related_post_id, actor_user_id, created_at) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, GETDATE())";
            try (PreparedStatement ps = conn.prepareStatement(logSql)) {
                ps.setLong(1, answerAuthorId);
                ps.setInt(2, reputationDelta);
                ps.setString(3, isToggleOff ? "Answer unaccepted" : "Answer accepted");
                ps.setString(4, isToggleOff ? "accept_removed" : "accept");
                ps.setString(5, "answer");
                ps.setLong(6, answerId);
                ps.setLong(7, questionOwnerId);
                ps.executeUpdate();
            } catch (SQLException ignored) {
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
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
}

package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import dto.QuestionDTO;
import config.DBContext;

public class QuestionDAO extends DBContext {

    // 1. Hàm chính lấy danh sách câu hỏi (Search, Filter, Sort, Paging)
    public List<QuestionDTO> getQuestions(int pageIndex, int pageSize, String sortBy, String keyword, String filterType) {
        List<QuestionDTO> list = new ArrayList<>();
        
        // Dùng StringBuilder để nối chuỗi tối ưu hơn
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT q.*, u.username, up.avatar_url, ")
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
    
    // 4. Hàm Search (Optional)
    public List<QuestionDTO> searchQuestions(String keyword) {
        return getQuestions(1, 20, "newest", keyword, "all");
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
}
package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    
    // 4. Hàm Search rút gọn (Optional)
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
}
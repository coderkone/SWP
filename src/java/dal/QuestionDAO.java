package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException; // Sửa lỗi đỏ ResultSet, SQLException
import java.util.ArrayList;
import java.util.List;
import dto.QuestionDTO;
import config.DBContext;

public class QuestionDAO extends DBContext {

    // 1. Hàm lấy danh sách câu hỏi (Phiên bản 5 tham số cho Phân trang)
    public List<QuestionDTO> getQuestions(int pageIndex, int pageSize, String sortBy, String keyword, String filterType) {
        List<QuestionDTO> list = new ArrayList<>();
        
        // Câu lệnh SQL gốc kết nối 3 bảng
        String sql = "SELECT q.*, u.username, up.avatar_url, "
                   + "(SELECT COUNT(*) FROM Answers a WHERE a.question_id = q.question_id) as ans_count "
                   + "FROM Questions q "
                   + "JOIN Users u ON q.user_id = u.user_id "
                   + "LEFT JOIN User_Profile up ON u.user_id = up.user_id "
                   + "WHERE 1=1 ";

        // 1.1 XỬ LÝ TÌM KIẾM
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql += " AND (q.title LIKE ? OR q.body LIKE ?) ";
        }

        // 1.2 XỬ LÝ LỌC (Ví dụ: Lọc bài chưa trả lời)
        if ("unanswered".equals(filterType)) {
            sql += " AND (SELECT COUNT(*) FROM Answers a WHERE a.question_id = q.question_id) = 0 ";
        }

        // 1.3 XỬ LÝ SẮP XẾP
        if ("views".equals(sortBy)) {
            sql += " ORDER BY q.view_count DESC ";
        } else if ("active".equals(sortBy)) {
            // Active có thể hiểu là bài có câu trả lời mới nhất hoặc update mới nhất
            sql += " ORDER BY q.updated_at DESC "; 
        } else {
            // Mặc định: Newest
            sql += " ORDER BY q.created_at DESC "; 
        }

        // 1.4 PHÂN TRANG (Quan trọng)
        sql += " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try {
            Connection conn = getConnection();
            PreparedStatement st = conn.prepareStatement(sql);
            int paramIndex = 1;

            // Set tham số cho Search (nếu có)
            if (keyword != null && !keyword.trim().isEmpty()) {
                st.setString(paramIndex++, "%" + keyword + "%");
                st.setString(paramIndex++, "%" + keyword + "%");
            }

            // Set tham số cho Phân trang (Luôn nằm cuối cùng)
            st.setInt(paramIndex++, (pageIndex - 1) * pageSize); // Bỏ qua bao nhiêu dòng
            st.setInt(paramIndex++, pageSize);                   // Lấy bao nhiêu dòng

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                QuestionDTO q = new QuestionDTO();
                // Map dữ liệu gốc
                q.setQuestionId(rs.getLong("question_id"));
                q.setUserId(rs.getLong("user_id"));
                q.setTitle(rs.getString("title"));
                q.setBody(rs.getString("body"));
                q.setViewCount(rs.getInt("view_count"));
                q.setScore(rs.getInt("Score"));
                q.setCreatedAt(rs.getTimestamp("created_at"));
                
                // Map dữ liệu hiển thị (DTO)
                q.setAuthorName(rs.getString("username"));
                q.setAuthorAvatar(rs.getString("avatar_url"));
                q.setAnswerCount(rs.getInt("ans_count"));

                list.add(q);
            }
            rs.close();
            st.close();
            conn.close();
        } catch (Exception e) {
            System.out.println("Error in getQuestions: " + e.getMessage());
        }
        return list;
    }

    // 2. Hàm đếm tổng số bài viết (Để tính số trang)
    public int getTotalQuestions(String keyword, String filterType) {
        String sql = "SELECT COUNT(*) FROM Questions q WHERE 1=1 ";
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql += " AND (q.title LIKE ? OR q.body LIKE ?) ";
        }
        if ("unanswered".equals(filterType)) {
             sql += " AND (SELECT COUNT(*) FROM Answers a WHERE a.question_id = q.question_id) = 0 ";
        }

        try {
            Connection conn = getConnection();
            PreparedStatement st = conn.prepareStatement(sql);
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
            System.out.println("Error in getTotalQuestions: " + e.getMessage());
        }
        return 0;
    }
}
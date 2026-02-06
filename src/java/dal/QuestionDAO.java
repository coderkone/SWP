package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import dto.QuestionDTO;
import config.DBContext;

public class QuestionDAO extends DBContext {

    public List<QuestionDTO> getQuestions(int pageIndex, int pageSize, String sortBy, String keyword, String filterType) {
        List<QuestionDTO> list = new ArrayList<>();
        
        String sql = "SELECT q.*, u.username, up.avatar_url, "
                   + "(SELECT COUNT(*) FROM Answers a WHERE a.question_id = q.question_id) as ans_count "
                   + "FROM Questions q "
                   + "JOIN Users u ON q.user_id = u.user_id "
                   + "LEFT JOIN User_Profile up ON u.user_id = up.user_id "
                   + "WHERE 1=1 ";

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql += " AND (q.title LIKE ? OR q.body LIKE ?) ";
        }

        if ("unanswered".equals(filterType)) {
            sql += " AND (SELECT COUNT(*) FROM Answers a WHERE a.question_id = q.question_id) = 0 ";
        }

        if ("views".equals(sortBy)) {
            sql += " ORDER BY q.view_count DESC ";
        } else if ("active".equals(sortBy)) {
            sql += " ORDER BY q.updated_at DESC "; 
        } else {
            sql += " ORDER BY q.created_at DESC "; 
        }

        sql += " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try {
            Connection conn = getConnection();
            PreparedStatement st = conn.prepareStatement(sql);
            int paramIndex = 1;

            if (keyword != null && !keyword.trim().isEmpty()) {
                st.setString(paramIndex++, "%" + keyword + "%");
                st.setString(paramIndex++, "%" + keyword + "%");
            }

            st.setInt(paramIndex++, (pageIndex - 1) * pageSize);
            st.setInt(paramIndex++, pageSize);

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
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
                
                q.setTags(new ArrayList<>()); 

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
            e.printStackTrace();
        }
        return 0;
    }
}
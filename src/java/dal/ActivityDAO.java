package dal;

import config.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class ActivityDAO extends DBContext {

//    SUMMARY
    // 1. Lấy dữ liệu cho Biểu đồ 1
    public Map<String, Integer> getActivityByMonth(long userId) {
        Map<String, Integer> activityMap = new LinkedHashMap<>(); 
        String sql = "SELECT FORMAT(created_at, 'yyyy-MM') as month, COUNT(*) as total_posts " +
                     "FROM ( " +
                     "    SELECT created_at FROM Questions WHERE user_id = ? " +
                     "    UNION ALL " +
                     "    SELECT created_at FROM Answers WHERE user_id = ? " +
                     ") AS all_posts " +
                     "GROUP BY FORMAT(created_at, 'yyyy-MM') " +
                     "ORDER BY month DESC " +
                     "OFFSET 0 ROWS FETCH NEXT 6 ROWS ONLY"; // Lấy 6 tháng gần nhất

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    activityMap.put(rs.getString("month"), rs.getInt("total_posts"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return activityMap;
    }

    // 2. Lấy dữ liệu cho Biểu đồ 2
    public Map<String, Integer> getTopTagsByReputation(long userId) {
        Map<String, Integer> tagsMap = new LinkedHashMap<>();
        String sql = "SELECT TOP 5 t.tag_name, SUM(COALESCE(q.Score, 0)) AS total_score " +
                     "FROM Tags t " +
                     "JOIN Question_Tags qt ON t.tag_id = qt.tag_id " +
                     "JOIN Questions q ON qt.question_id = q.question_id " +
                     "WHERE q.user_id = ? " +
                     "GROUP BY t.tag_name " +
                     "ORDER BY total_score DESC";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tagsMap.put(rs.getString("tag_name"), rs.getInt("total_score"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tagsMap;
    }
    
//    QUESTIONS 
    public List<dto.QuestionDTO> getUserQuestions(long userId, int pageIndex, int pageSize) {
        List<dto.QuestionDTO> list = new ArrayList<>();
        String sql = "SELECT q.*, u.username, up.avatar_url, " +
                     "(SELECT COUNT(*) FROM Answers a WHERE a.question_id = q.question_id) as ans_count " +
                     "FROM Questions q " +
                     "JOIN Users u ON q.user_id = u.user_id " +
                     "LEFT JOIN User_Profile up ON u.user_id = up.user_id " +
                     "WHERE q.user_id = ? " +
                     "ORDER BY q.created_at DESC " +
                     "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setInt(2, (pageIndex - 1) * pageSize);
            ps.setInt(3, pageSize);
            try (ResultSet rs = ps.executeQuery()) {
                QuestionDAO qDao = new QuestionDAO(); // Tái sử dụng để lấy tags
                while (rs.next()) {
                    dto.QuestionDTO q = new dto.QuestionDTO();
                    q.setQuestionId(rs.getLong("question_id"));
                    q.setUserId(rs.getLong("user_id"));
                    q.setTitle(rs.getString("title"));
                    q.setBody(rs.getString("body"));
                    q.setViewCount(rs.getInt("view_count"));
                    q.setScore(rs.getInt("Score"));
                    q.setCreatedAt(rs.getTimestamp("created_at"));
                    q.setAnswerCount(rs.getInt("ans_count"));
                    q.setTags(qDao.getTagsByQuestionId(q.getQuestionId()));
                    list.add(q);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public int getTotalUserQuestions(long userId) {
        String sql = "SELECT COUNT(*) FROM Questions WHERE user_id = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }
    
//    ANSWERS
    public List<Map<String, Object>> getUserAnswers(long userId, int pageIndex, int pageSize) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT a.answer_id, a.body, a.Score, a.is_accepted, a.created_at, " +
                     "q.question_id, q.title as question_title " +
                     "FROM Answers a JOIN Questions q ON a.question_id = q.question_id " +
                     "WHERE a.user_id = ? ORDER BY a.created_at DESC " +
                     "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setInt(2, (pageIndex - 1) * pageSize);
            ps.setInt(3, pageSize);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> ans = new HashMap<>();
                    ans.put("answerId", rs.getLong("answer_id"));
                    ans.put("score", rs.getInt("Score"));
                    ans.put("isAccepted", rs.getBoolean("is_accepted"));
                    ans.put("body", rs.getString("body"));
                    ans.put("createdAt", rs.getTimestamp("created_at"));
                    ans.put("questionId", rs.getLong("question_id"));
                    ans.put("questionTitle", rs.getString("question_title"));
                    list.add(ans);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public int getTotalUserAnswers(long userId) {
        String sql = "SELECT COUNT(*) FROM Answers WHERE user_id = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }
    
//    COMMENTS
    public List<Map<String, Object>> getUserComments(long userId, int pageIndex, int pageSize) {
        List<Map<String, Object>> list = new ArrayList<>();
        // Logic: Nếu comment ở question thì lấy title question đó, nếu ở answer thì join lấy title của question chứa answer đó
        String sql = "SELECT c.comment_id, c.body, c.created_at, " +
                     "CASE WHEN c.question_id IS NOT NULL THEN c.question_id ELSE a.question_id END as target_question_id, " +
                     "CASE WHEN c.question_id IS NOT NULL THEN q1.title ELSE q2.title END as target_title " +
                     "FROM Comments c " +
                     "LEFT JOIN Questions q1 ON c.question_id = q1.question_id " +
                     "LEFT JOIN Answers a ON c.answer_id = a.answer_id " +
                     "LEFT JOIN Questions q2 ON a.question_id = q2.question_id " +
                     "WHERE c.user_id = ? ORDER BY c.created_at DESC " +
                     "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setInt(2, (pageIndex - 1) * pageSize);
            ps.setInt(3, pageSize);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> cmt = new HashMap<>();
                    cmt.put("commentId", rs.getLong("comment_id"));
                    cmt.put("body", rs.getString("body"));
                    cmt.put("createdAt", rs.getTimestamp("created_at"));
                    cmt.put("questionId", rs.getLong("target_question_id"));
                    cmt.put("questionTitle", rs.getString("target_title"));
                    list.add(cmt);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public int getTotalUserComments(long userId) {
        String sql = "SELECT COUNT(*) FROM Comments WHERE user_id = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }
    
//    TAGS
    public List<Map<String, Object>> getUserTags(long userId, int pageIndex, int pageSize) {
        List<Map<String, Object>> list = new ArrayList<>();
        // Lấy danh sách Tag, tính tổng điểm và số lượng bài viết của user trong tag đó
        String sql = "SELECT t.tag_name, SUM(COALESCE(q.Score, 0)) AS total_score, COUNT(DISTINCT q.question_id) as post_count " +
                     "FROM Tags t " +
                     "JOIN Question_Tags qt ON t.tag_id = qt.tag_id " +
                     "JOIN Questions q ON qt.question_id = q.question_id " +
                     "WHERE q.user_id = ? " +
                     "GROUP BY t.tag_name " +
                     "ORDER BY total_score DESC " +
                     "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setInt(2, (pageIndex - 1) * pageSize);
            ps.setInt(3, pageSize);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> tag = new HashMap<>();
                    tag.put("tagName", rs.getString("tag_name"));
                    tag.put("score", rs.getInt("total_score"));
                    tag.put("postCount", rs.getInt("post_count"));
                    list.add(tag);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public int getTotalUserTags(long userId) {
        String sql = "SELECT COUNT(DISTINCT t.tag_id) FROM Tags t " +
                     "JOIN Question_Tags qt ON t.tag_id = qt.tag_id " +
                     "JOIN Questions q ON qt.question_id = q.question_id " +
                     "WHERE q.user_id = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

//    FOLLOWS (Bookmarks)
    public List<Map<String, Object>> getUserFollows(long userId, int pageIndex, int pageSize) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT b.created_at as saved_at, q.question_id, q.title, q.Score, q.view_count, " +
                     "(SELECT COUNT(*) FROM Answers a WHERE a.question_id = q.question_id) as ans_count " +
                     "FROM Bookmarks b " +
                     "JOIN Questions q ON b.question_id = q.question_id " +
                     "WHERE b.user_id = ? " +
                     "ORDER BY b.created_at DESC " +
                     "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setInt(2, (pageIndex - 1) * pageSize);
            ps.setInt(3, pageSize);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> fav = new HashMap<>();
                    fav.put("questionId", rs.getLong("question_id"));
                    fav.put("title", rs.getString("title"));
                    fav.put("score", rs.getInt("Score"));
                    fav.put("viewCount", rs.getInt("view_count"));
                    fav.put("answerCount", rs.getInt("ans_count"));
                    fav.put("savedAt", rs.getTimestamp("saved_at"));
                    list.add(fav);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public int getTotalUserFollows(long userId) {
        String sql = "SELECT COUNT(*) FROM Bookmarks WHERE user_id = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

//    TAB: VOTES
    public List<Map<String, Object>> getUserVotes(long userId, int pageIndex, int pageSize) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT v.vote_type, v.created_at, " +
                     "CASE WHEN v.question_id IS NOT NULL THEN v.question_id ELSE a.question_id END as target_question_id, " +
                     "CASE WHEN v.question_id IS NOT NULL THEN q1.title ELSE q2.title END as target_title, " +
                     "CASE WHEN v.question_id IS NOT NULL THEN 'Question' ELSE 'Answer' END as post_type " +
                     "FROM Votes v " +
                     "LEFT JOIN Questions q1 ON v.question_id = q1.question_id " +
                     "LEFT JOIN Answers a ON v.answer_id = a.answer_id " +
                     "LEFT JOIN Questions q2 ON a.question_id = q2.question_id " +
                     "WHERE v.user_id = ? ORDER BY v.created_at DESC " +
                     "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setInt(2, (pageIndex - 1) * pageSize);
            ps.setInt(3, pageSize);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> vote = new HashMap<>();
                    vote.put("voteType", rs.getString("vote_type"));
                    vote.put("createdAt", rs.getTimestamp("created_at"));
                    vote.put("questionId", rs.getLong("target_question_id"));
                    vote.put("title", rs.getString("target_title"));
                    vote.put("postType", rs.getString("post_type"));
                    list.add(vote);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public int getTotalUserVotes(long userId) {
        String sql = "SELECT COUNT(*) FROM Votes WHERE user_id = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }
}

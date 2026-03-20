package dal;

import config.DBContext;
import dto.AnswerDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AnswerDAO extends DBContext {

    public long createAnswer(long questionId, long userId, String body, String codeSnippet) throws Exception {
        String sql = "INSERT INTO Answers (question_id, user_id, body, code_snippet, is_edited, is_accepted, created_at, updated_at, Score) "
                + "VALUES (?, ?, ?, ?, 0, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0)";

        long answerId = -1;

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, questionId);
            ps.setLong(2, userId);
            ps.setString(3, body);
            ps.setString(4, codeSnippet != null ? codeSnippet : "");

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    answerId = rs.getLong(1);
                }
            }
        }

        return answerId;
    }

    public List<AnswerDTO> getAnswersByQuestionId(long questionId) throws Exception {

        String sql = "SELECT a.*, u.username, u.Reputation AS author_reputation FROM Answers a "
                + "JOIN Users u ON a.user_id = u.user_id WHERE a.question_id = ? "
                + "ORDER BY CASE WHEN a.answer_id = (SELECT accepted_answer_id FROM Questions WHERE question_id = ?) THEN 1 ELSE 0 END DESC, a.Score DESC, a.created_at DESC";

        List<AnswerDTO> answers = new ArrayList<>();

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, questionId);
            ps.setLong(2, questionId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    answers.add(mapAnswer(rs));
                }
            }
        }

        return answers;
    }

 

    public List<AnswerDTO> getAnswersByQuestionId(long questionId, int pageIndex, int pageSize) throws Exception {
        return getAnswersByQuestionId(questionId, pageIndex, pageSize, "score_desc");
    }

    public List<AnswerDTO> getAnswersByQuestionId(long questionId,
            int pageIndex,
            int pageSize,
            String sort) throws Exception {
        StringBuilder sqlBuilder = new StringBuilder(
                "SELECT a.*, u.username, u.Reputation AS author_reputation FROM Answers a "
                + "JOIN Users u ON a.user_id = u.user_id WHERE a.question_id = ?");

        String sortClause;
        if ("score_asc".equalsIgnoreCase(sort)) {
            sortClause = "a.Score ASC, a.created_at DESC";
        } else if ("newest".equalsIgnoreCase(sort)) {
            sortClause = "a.created_at DESC, a.Score DESC";
        } else if ("oldest".equalsIgnoreCase(sort)) {
            sortClause = "a.created_at ASC, a.Score DESC";
        } else {
            sortClause = "a.Score DESC, a.created_at DESC";
        }

        sqlBuilder.append(" ORDER BY CASE WHEN a.answer_id = (SELECT accepted_answer_id FROM Questions WHERE question_id = ?) THEN 1 ELSE 0 END DESC, ")
                .append(sortClause)
                .append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        String sql = sqlBuilder.toString();

        List<AnswerDTO> answers = new ArrayList<>();

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            int safePageIndex = Math.max(1, pageIndex);
            int safePageSize = Math.max(1, pageSize);
            int offset = (safePageIndex - 1) * safePageSize;

            ps.setLong(1, questionId);
            ps.setLong(2, questionId);
            ps.setInt(3, offset);
            ps.setInt(4, safePageSize);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AnswerDTO answer = mapAnswer(rs);
                    answers.add(answer);
                }
            }
        }

        return answers;
    }

    public int getTotalAnswersByQuestionId(long questionId) throws Exception {
        String sql = "SELECT COUNT(*) FROM Answers WHERE question_id = ?";

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, questionId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return 0;
    }

    public AnswerDTO getAnswerById(long answerId) throws Exception {
        String sql = "SELECT a.*, u.username, u.Reputation AS author_reputation FROM Answers a "
                + "JOIN Users u ON a.user_id = u.user_id WHERE a.answer_id = ?";

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, answerId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapAnswer(rs);
                }
            }
        }

        return null;
    }

    public boolean updateAnswer(long answerId, String body, String codeSnippet) throws Exception {

        String sql = "UPDATE Answers SET body = ?, code_snippet = ?, is_edited = 1, updated_at = CURRENT_TIMESTAMP WHERE answer_id = ?";

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, body);
            ps.setString(2, codeSnippet != null ? codeSnippet : "");
            ps.setLong(3, answerId);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateAnswerWithHistory(long answerId, long editorId, String body, String codeSnippet) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            String oldBody = null;
            String oldCode = null;
            String loadSql = "SELECT body, code_snippet FROM Answers WHERE answer_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(loadSql)) {
                ps.setLong(1, answerId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }
                    oldBody = rs.getString("body");
                    oldCode = rs.getString("code_snippet");
                }
            }

            insertEditHistory(conn, "answer", answerId, null, oldBody, oldCode, null, editorId);

            String updateSql = "UPDATE Answers SET body = ?, code_snippet = ?, is_edited = 1, updated_at = CURRENT_TIMESTAMP WHERE answer_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setString(1, body);
                ps.setString(2, codeSnippet != null ? codeSnippet : "");
                ps.setLong(3, answerId);
                if (ps.executeUpdate() == 0) {
                    conn.rollback();
                    return false;
                }
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

    public boolean deleteAnswer(long answerId) throws Exception {
        String sql = "DELETE FROM Answers WHERE answer_id = ?";

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, answerId);
            return ps.executeUpdate() > 0;
        }
    }

    private AnswerDTO mapAnswer(ResultSet rs) throws Exception {
        AnswerDTO answer = new AnswerDTO(
                rs.getLong("answer_id"),
                rs.getLong("question_id"),
                rs.getLong("user_id"),
                rs.getString("body"),
                rs.getString("code_snippet"),
                rs.getBoolean("is_edited"),
                rs.getBoolean("is_accepted"),
                rs.getTimestamp("created_at"),
                rs.getTimestamp("updated_at"),
                rs.getInt("Score"),
                rs.getString("username"),
                "",
                rs.getInt("Score")
        );
        answer.setAuthorReputation(rs.getInt("author_reputation"));
        return answer;
    }
}

package dal;

import config.DBContext;
import dto.AnswerDTO;
import model.Answer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AnswerDAO {

    private final DBContext db = new DBContext();

    public long createAnswer(long questionId, long userId, String body, String codeSnippet) throws Exception {
        String sql = "INSERT INTO Answers (question_id, user_id, body, code_snippet, is_edited, is_accepted, created_at, updated_at, Score) " +
                "VALUES (?, ?, ?, ?, 0, 0, GETDATE(), GETDATE(), 0)";

        long answerId = -1;

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, questionId);
            ps.setLong(2, userId);
            ps.setString(3, body);
            ps.setString(4, codeSnippet != null ? codeSnippet : "");

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) answerId = rs.getLong(1);
            }
        }

        return answerId;
    }

    public List<AnswerDTO> getAnswersByQuestionId(long questionId) throws Exception {
        String sql = "SELECT a.*, u.username FROM Answers a " +
                "JOIN Users u ON a.user_id = u.user_id WHERE a.question_id = ? " +
                "ORDER BY CASE WHEN a.answer_id = (SELECT accepted_answer_id FROM Questions WHERE question_id = ?) THEN 1 ELSE 0 END DESC, a.Score DESC, a.created_at DESC";

        List<AnswerDTO> answers = new ArrayList<>();

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, questionId);
            ps.setLong(2, questionId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AnswerDTO answer = mapAnswer(rs);
                    answers.add(answer);
                }
            }
        }

        return answers;
    }

    public AnswerDTO getAnswerById(long answerId) throws Exception {
        String sql = "SELECT a.*, u.username FROM Answers a " +
                "JOIN Users u ON a.user_id = u.user_id WHERE a.answer_id = ?";

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

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
        String sql = "UPDATE Answers SET body = ?, code_snippet = ?, is_edited = 1, updated_at = GETDATE() WHERE answer_id = ?";

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, body);
            ps.setString(2, codeSnippet != null ? codeSnippet : "");
            ps.setLong(3, answerId);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteAnswer(long answerId) throws Exception {
        String sql = "DELETE FROM Answers WHERE answer_id = ?";

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, answerId);
            return ps.executeUpdate() > 0;
        }
    }

    private AnswerDTO mapAnswer(ResultSet rs) throws Exception {
        return new AnswerDTO(
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
                "", // Avatar can be added later
                rs.getInt("Score")
        );
    }
}

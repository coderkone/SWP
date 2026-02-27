package dal;

import config.DBContext;
import dto.CommentDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CommentDAO {
    private final DBContext db = new DBContext();

    /**
     * Insert a new comment on an answer
     */
    public long insertAnswerComment(long userId, long answerId, String body) throws Exception {
        String sql = "INSERT INTO Comments (user_id, answer_id, body, created_at) " +
                "VALUES (?, ?, ?, GETDATE())";

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, userId);
            ps.setLong(2, answerId);
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

    /**
     * Insert a new comment on a question
     */
    public long insertQuestionComment(long userId, long questionId, String body) throws Exception {
        String sql = "INSERT INTO Comments (user_id, question_id, body, created_at) " +
                "VALUES (?, ?, ?, GETDATE())";

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, userId);
            ps.setLong(2, questionId);
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

    /**
     * Get all comments for a specific answer, ordered by creation date (ASC)
     */
    public List<CommentDTO> getCommentsByAnswerId(long answerId) throws Exception {
        List<CommentDTO> comments = new ArrayList<>();

        String sql = "SELECT c.comment_id, c.user_id, c.question_id, c.answer_id, c.body, c.created_at, " +
                    "u.username, u.Reputation " +
                    "FROM Comments c " +
                    "JOIN Users u ON c.user_id = u.user_id " +
                    "WHERE c.answer_id = ? " +
                    "ORDER BY c.created_at ASC";

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, answerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CommentDTO comment = new CommentDTO(
                        rs.getLong("comment_id"),
                        rs.getLong("user_id"),
                        rs.getLong("question_id"),
                        rs.getLong("answer_id"),
                        rs.getString("body"),
                        rs.getTimestamp("created_at")
                    );
                    comment.setAuthorName(rs.getString("username"));
                    comment.setAuthorReputation(rs.getInt("Reputation"));
                    comments.add(comment);
                }
            }
        }

        return comments;
    }

    /**
     * Get all comments for a specific question, ordered by creation date (ASC)
     */
    public List<CommentDTO> getCommentsByQuestionId(long questionId) throws Exception {
        List<CommentDTO> comments = new ArrayList<>();

        String sql = "SELECT c.comment_id, c.user_id, c.question_id, c.answer_id, c.body, c.created_at, " +
                    "u.username, u.Reputation " +
                    "FROM Comments c " +
                    "JOIN Users u ON c.user_id = u.user_id " +
                    "WHERE c.question_id = ? AND c.answer_id IS NULL " +
                    "ORDER BY c.created_at ASC";

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, questionId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CommentDTO comment = new CommentDTO(
                        rs.getLong("comment_id"),
                        rs.getLong("user_id"),
                        rs.getLong("question_id"),
                        rs.getLong("answer_id"),
                        rs.getString("body"),
                        rs.getTimestamp("created_at")
                    );
                    comment.setAuthorName(rs.getString("username"));
                    comment.setAuthorReputation(rs.getInt("Reputation"));
                    comments.add(comment);
                }
            }
        }

        return comments;
    }

    /**
     * Check if answer exists
     */
    public boolean answerExists(long answerId) throws Exception {
        String sql = "SELECT 1 FROM Answers WHERE answer_id = ?";

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, answerId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Check if question exists
     */
    public boolean questionExists(long questionId) throws Exception {
        String sql = "SELECT 1 FROM Questions WHERE question_id = ?";

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, questionId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}

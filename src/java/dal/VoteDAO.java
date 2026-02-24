package dal;

import config.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Vote;

public class VoteDAO {

    private final DBContext db = new DBContext();

    public boolean addVote(long userId, Long questionId, Long answerId, String voteType) throws Exception {
        // Check if user already voted
        String checkSql = "SELECT * FROM Votes WHERE user_id = ? AND " +
                (questionId != null ? "question_id = ? AND answer_id IS NULL" : "answer_id = ? AND question_id IS NULL");

        try (Connection con = db.getConnection();
             PreparedStatement checkPs = con.prepareStatement(checkSql)) {

            checkPs.setLong(1, userId);
            if (questionId != null) {
                checkPs.setLong(2, questionId);
            } else {
                checkPs.setLong(2, answerId);
            }

            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next()) {
                    // User already voted, update the vote
                    String updateSql = "UPDATE Votes SET vote_type = ?, created_at = GETDATE() WHERE user_id = ? AND " +
                            (questionId != null ? "question_id = ?" : "answer_id = ?");

                    try (PreparedStatement updatePs = con.prepareStatement(updateSql)) {
                        updatePs.setString(1, voteType);
                        updatePs.setLong(2, userId);
                        if (questionId != null) {
                            updatePs.setLong(3, questionId);
                        } else {
                            updatePs.setLong(3, answerId);
                        }
                        boolean updated = updatePs.executeUpdate() > 0;
                        
                        // Update score in Questions or Answers table
                        if (updated) {
                            try {
                                if (questionId != null) {
                                    updateQuestionScore(questionId);
                                } else {
                                    updateAnswerScore(answerId);
                                }
                            } catch (Exception e) {
                                // Log but don't fail - vote was saved successfully
                                System.err.println("Warning: Failed to update score after vote: " + e.getMessage());
                            }
                        }
                        return updated;
                    }
                }
            }
        }

        // Insert new vote
        String insertSql = "INSERT INTO Votes (user_id, question_id, answer_id, vote_type, created_at) " +
                "VALUES (?, ?, ?, ?, GETDATE())";

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(insertSql)) {

            ps.setLong(1, userId);
            ps.setObject(2, questionId);
            ps.setObject(3, answerId);
            ps.setString(4, voteType);

            boolean inserted = ps.executeUpdate() > 0;
            
            // Update score in Questions or Answers table
            if (inserted) {
                try {
                    if (questionId != null) {
                        updateQuestionScore(questionId);
                    } else {
                        updateAnswerScore(answerId);
                    }
                } catch (Exception e) {
                    // Log but don't fail - vote was saved successfully
                    System.err.println("Warning: Failed to update score after vote: " + e.getMessage());
                }
            }
            return inserted;
        }
    }

    public int getVoteScore(Long questionId, Long answerId) throws Exception {
        String sql = "SELECT COUNT(CASE WHEN vote_type = 'upvote' THEN 1 END) - " +
                "COUNT(CASE WHEN vote_type = 'downvote' THEN 1 END) as score FROM Votes WHERE " +
                (questionId != null ? "question_id = ? AND answer_id IS NULL" : "answer_id = ? AND question_id IS NULL");

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (questionId != null) {
                ps.setLong(1, questionId);
            } else {
                ps.setLong(1, answerId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("score");
                }
            }
        }

        return 0;
    }

    public String getUserVote(long userId, Long questionId, Long answerId) throws Exception {
        String sql = "SELECT vote_type FROM Votes WHERE user_id = ? AND " +
                (questionId != null ? "question_id = ? AND answer_id IS NULL" : "answer_id = ? AND question_id IS NULL");

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, userId);
            if (questionId != null) {
                ps.setLong(2, questionId);
            } else {
                ps.setLong(2, answerId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("vote_type");
                }
            }
        }

        return null;
    }

    // Cập nhật Score cho Question
    private void updateQuestionScore(Long questionId) throws Exception {
        String sql = "UPDATE Questions SET Score = (" +
                "SELECT COUNT(CASE WHEN vote_type = 'upvote' THEN 1 END) - " +
                "COUNT(CASE WHEN vote_type = 'downvote' THEN 1 END) FROM Votes " +
                "WHERE question_id = ? AND answer_id IS NULL" +
                ") WHERE question_id = ?";

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, questionId);
            ps.setLong(2, questionId);
            ps.executeUpdate();
        }
    }

    // Cập nhật Score cho Answer
    private void updateAnswerScore(Long answerId) throws Exception {
        String sql = "UPDATE Answers SET Score = (" +
                "SELECT COUNT(CASE WHEN vote_type = 'upvote' THEN 1 END) - " +
                "COUNT(CASE WHEN vote_type = 'downvote' THEN 1 END) FROM Votes " +
                "WHERE answer_id = ? AND question_id IS NULL" +
                ") WHERE answer_id = ?";

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, answerId);
            ps.setLong(2, answerId);
            ps.executeUpdate();
        }
    }

    public List<Vote> getVotesByQuestionId(long questionId) throws Exception {
        String sql = "SELECT * FROM Votes WHERE question_id = ? ORDER BY created_at DESC";

        List<Vote> votes = new ArrayList<>();

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, questionId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Vote vote = mapVote(rs);
                    votes.add(vote);
                }
            }
        }

        return votes;
    }

    public List<Vote> getVotesByAnswerId(long answerId) throws Exception {
        String sql = "SELECT * FROM Votes WHERE answer_id = ? ORDER BY created_at DESC";

        List<Vote> votes = new ArrayList<>();

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, answerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Vote vote = mapVote(rs);
                    votes.add(vote);
                }
            }
        }

        return votes;
    }

    private Vote mapVote(ResultSet rs) throws Exception {
        return new Vote(
                rs.getLong("vote_id"),
                rs.getLong("user_id"),
                rs.getObject("question_id") != null ? rs.getLong("question_id") : null,
                rs.getObject("answer_id") != null ? rs.getLong("answer_id") : null,
                rs.getString("vote_type"),
                rs.getTimestamp("created_at")
        );
    }
}

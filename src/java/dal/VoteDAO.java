package dal;

import config.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Vote;

public class VoteDAO {

    private final DBContext db = new DBContext();

    public static class VoteActionResult {

        private final boolean success;
        private final String currentVoteType;
        private final int score;
        private final int voterReputation;

        public VoteActionResult(boolean success, String currentVoteType, int score, int voterReputation) {
            this.success = success;
            this.currentVoteType = currentVoteType;
            this.score = score;
            this.voterReputation = voterReputation;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getCurrentVoteType() {
            return currentVoteType;
        }

        public int getScore() {
            return score;
        }

        public int getVoterReputation() {
            return voterReputation;
        }
    }

    public boolean addVote(long userId, Long questionId, Long answerId, String voteType) throws Exception {
        return submitVote(userId, questionId, answerId, voteType).isSuccess();
    }

    public VoteActionResult submitVote(long voterId, Long questionId, Long answerId, String voteType) throws Exception {
        String dbVoteType = toDbVoteType(voteType);
        boolean isQuestionVote = questionId != null;

        if ((questionId == null && answerId == null) || (questionId != null && answerId != null)) {
            throw new IllegalArgumentException("Must provide exactly one target: questionId or answerId");
        }
        if (!"up".equals(dbVoteType) && !"down".equals(dbVoteType)) {
            throw new IllegalArgumentException("Invalid vote type");
        }

        Connection con = null;
        try {
            con = db.getConnection();
            con.setAutoCommit(false);

            Long postOwnerId = getPostOwnerId(con, questionId, answerId);
            if (postOwnerId == null) {
                con.rollback();
                return new VoteActionResult(false, null, 0, getUserReputation(con, voterId));
            }

            String existingVote = null;
            long existingVoteId = -1L;
            String checkSql = "SELECT vote_id, vote_type FROM Votes WITH (UPDLOCK, HOLDLOCK) WHERE user_id = ? AND "
                    + (isQuestionVote ? "question_id = ? AND answer_id IS NULL" : "answer_id = ? AND question_id IS NULL");
            try (PreparedStatement checkPs = con.prepareStatement(checkSql)) {
                checkPs.setLong(1, voterId);
                checkPs.setLong(2, isQuestionVote ? questionId : answerId);
                try (ResultSet rs = checkPs.executeQuery()) {
                    if (rs.next()) {
                        existingVoteId = rs.getLong("vote_id");
                        existingVote = rs.getString("vote_type");
                    }
                }
            }

            String finalVote = dbVoteType;

            if (existingVote == null) {
                String insertSql = "INSERT INTO Votes (user_id, question_id, answer_id, vote_type, created_at) VALUES (?, ?, ?, ?, GETDATE())";
                try (PreparedStatement ps = con.prepareStatement(insertSql)) {
                    ps.setLong(1, voterId);
                    ps.setObject(2, questionId);
                    ps.setObject(3, answerId);
                    ps.setString(4, dbVoteType);
                    ps.executeUpdate();
                }
                applyReputationForVote(con, voterId, postOwnerId, isQuestionVote, dbVoteType, 1, questionId, answerId);
            } else if (existingVote.equalsIgnoreCase(dbVoteType)) {
                // Same vote clicked again -> remove vote and revert its reputation effects.
                String deleteSql = "DELETE FROM Votes WHERE vote_id = ?";
                try (PreparedStatement ps = con.prepareStatement(deleteSql)) {
                    ps.setLong(1, existingVoteId);
                    ps.executeUpdate();
                }
                applyReputationForVote(con, voterId, postOwnerId, isQuestionVote, existingVote, -1, questionId, answerId);
                finalVote = null;
            } else {
                String updateSql = "UPDATE Votes SET vote_type = ?, created_at = GETDATE() WHERE vote_id = ?";
                try (PreparedStatement ps = con.prepareStatement(updateSql)) {
                    ps.setString(1, dbVoteType);
                    ps.setLong(2, existingVoteId);
                    ps.executeUpdate();
                }
                applyReputationForVote(con, voterId, postOwnerId, isQuestionVote, existingVote, -1, questionId, answerId);
                applyReputationForVote(con, voterId, postOwnerId, isQuestionVote, dbVoteType, 1, questionId, answerId);
            }

            if (isQuestionVote) {
                updateQuestionScore(con, questionId);
            } else {
                updateAnswerScore(con, answerId);
            }

            int score = getVoteScore(con, questionId, answerId);
            int voterReputation = getUserReputation(con, voterId);
            con.commit();
            return new VoteActionResult(true, normalizeVoteType(finalVote), score, voterReputation);
        } catch (Exception e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ignored) {
                }
            }
            throw e;
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    public int getVoteScore(Long questionId, Long answerId) throws Exception {
        try (Connection con = db.getConnection()) {
            return getVoteScore(con, questionId, answerId);
        }
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
                    return normalizeVoteType(rs.getString("vote_type"));
                }
            }
        }

        return null;
    }

    /** Convert app format to DB format - chk_vote_type allows only 'up'/'down'. */
    private String toDbVoteType(String voteType) {
        if (voteType == null) return "";
        if ("upvote".equalsIgnoreCase(voteType)) return "up";
        if ("downvote".equalsIgnoreCase(voteType)) return "down";
        return voteType;
    }

    /** Normalize 'up'/'down' to 'upvote'/'downvote' for JSP compatibility. */
    private String normalizeVoteType(String raw) {
        if (raw == null) return null;
        if ("up".equals(raw) || "upvote".equalsIgnoreCase(raw)) return "upvote";
        if ("down".equals(raw) || "downvote".equalsIgnoreCase(raw)) return "downvote";
        return raw;
    }

    private Long getPostOwnerId(Connection con, Long questionId, Long answerId) throws Exception {
        String sql = questionId != null
                ? "SELECT user_id FROM Questions WHERE question_id = ?"
                : "SELECT user_id FROM Answers WHERE answer_id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, questionId != null ? questionId : answerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("user_id");
                }
            }
        }
        return null;
    }

    private void applyReputationForVote(Connection con,
                                        long voterId,
                                        long postOwnerId,
                                        boolean isQuestionVote,
                                        String dbVoteType,
                                        int sign,
                                        Long questionId,
                                        Long answerId) throws Exception {
        String normalized = normalizeVoteType(dbVoteType);
        String postType = isQuestionVote ? "question" : "answer";
        Long relatedPostId = isQuestionVote ? questionId : answerId;

        if ("upvote".equals(normalized)) {
            int ownerDelta = isQuestionVote ? 5 : 10;
            changeReputation(con, postOwnerId, sign * ownerDelta,
                    isQuestionVote ? "Question upvoted" : "Answer upvoted",
                    "vote_up", postType, relatedPostId, voterId);
            return;
        }

        if ("downvote".equals(normalized)) {
            changeReputation(con, postOwnerId, sign * -2,
                    isQuestionVote ? "Question downvoted" : "Answer downvoted",
                    "vote_down_post_owner", postType, relatedPostId, voterId);
            changeReputation(con, voterId, sign * -1,
                    "Downvote cast",
                    "vote_down_voter", postType, relatedPostId, voterId);
        }
    }

    private void changeReputation(Connection con,
                                  long userId,
                                  int delta,
                                  String reason,
                                  String eventType,
                                  String relatedPostType,
                                  Long relatedPostId,
                                  Long actorUserId) throws Exception {
        if (delta == 0) {
            return;
        }

        String updateSql = "UPDATE Users SET Reputation = Reputation + ? WHERE user_id = ?";
        try (PreparedStatement ps = con.prepareStatement(updateSql)) {
            ps.setInt(1, delta);
            ps.setLong(2, userId);
            ps.executeUpdate();
        }

        // Best-effort logging for profile timeline. If table does not exist yet, keep core flow unaffected.
        String logSql = "INSERT INTO Reputation_History (user_id, delta, reason, event_type, related_post_type, related_post_id, actor_user_id, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, GETDATE())";
        try (PreparedStatement ps = con.prepareStatement(logSql)) {
            ps.setLong(1, userId);
            ps.setInt(2, delta);
            ps.setString(3, reason);
            ps.setString(4, eventType);
            ps.setString(5, relatedPostType);
            ps.setObject(6, relatedPostId);
            ps.setObject(7, actorUserId);
            ps.executeUpdate();
        } catch (SQLException ignored) {
        }
    }

    private int getUserReputation(Connection con, long userId) throws Exception {
        String sql = "SELECT Reputation FROM Users WHERE user_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("Reputation");
                }
            }
        }
        return 0;
    }

    // Cập nhật Score cho Question
    private void updateQuestionScore(Long questionId) throws Exception {
        try (Connection con = db.getConnection()) {
            updateQuestionScore(con, questionId);
        }
    }

    // Cập nhật Score cho Answer
    private void updateAnswerScore(Long answerId) throws Exception {
        try (Connection con = db.getConnection()) {
            updateAnswerScore(con, answerId);
        }
    }

    private void updateQuestionScore(Connection con, Long questionId) throws Exception {
        String sql = "UPDATE Questions SET Score = (" +
                "SELECT COUNT(CASE WHEN vote_type IN ('upvote', 'up') THEN 1 END) - " +
                "COUNT(CASE WHEN vote_type IN ('downvote', 'down') THEN 1 END) FROM Votes " +
                "WHERE question_id = ? AND answer_id IS NULL" +
                ") WHERE question_id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, questionId);
            ps.setLong(2, questionId);
            ps.executeUpdate();
        }
    }

    private void updateAnswerScore(Connection con, Long answerId) throws Exception {
        String sql = "UPDATE Answers SET Score = (" +
                "SELECT COUNT(CASE WHEN vote_type IN ('upvote', 'up') THEN 1 END) - " +
                "COUNT(CASE WHEN vote_type IN ('downvote', 'down') THEN 1 END) FROM Votes " +
                "WHERE answer_id = ? AND question_id IS NULL" +
                ") WHERE answer_id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, answerId);
            ps.setLong(2, answerId);
            ps.executeUpdate();
        }
    }

    private int getVoteScore(Connection con, Long questionId, Long answerId) throws Exception {
        String sql = "SELECT COUNT(CASE WHEN vote_type IN ('upvote', 'up') THEN 1 END) - "
                + "COUNT(CASE WHEN vote_type IN ('downvote', 'down') THEN 1 END) as score FROM Votes WHERE "
                + (questionId != null ? "question_id = ? AND answer_id IS NULL" : "answer_id = ? AND question_id IS NULL");

        try (PreparedStatement ps = con.prepareStatement(sql)) {
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

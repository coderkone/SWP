package dal;
import config.DBContext;
import dto.QuestionDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class QuestionDAO extends DBContext {

    public static final int MIN_BOUNTY_REPUTATION = 100;
    public static final int BOUNTY_DURATION_DAYS = 7;

    private static final Set<String> RECOMMENDATION_STOP_WORDS = new java.util.HashSet<>(Arrays.asList(
            "the", "and", "for", "with", "that", "this", "from", "into", "have", "has",
            "how", "why", "when", "where", "what", "which", "can", "cannot", "cant", "not",
            "are", "was", "were", "your", "you", "use", "using", "used", "get", "got",
            "then", "than", "them", "they", "their", "there", "here", "about", "after",
            "before", "been", "being", "would", "could", "should", "will", "just", "like",
            "make", "made", "need", "want", "help", "question", "questions", "code", "error",
            "java", "jsp", "sql", "html", "css"
    ));

    public QuestionDAO() {
        ensureQuestionBountyColumns();
    }

    public static class BountyPlacementResult {

        private final int updatedReputation;
        private final Timestamp bountyExpiresAt;

        public BountyPlacementResult(int updatedReputation, Timestamp bountyExpiresAt) {
            this.updatedReputation = updatedReputation;
            this.bountyExpiresAt = bountyExpiresAt;
        }

        public int getUpdatedReputation() {
            return updatedReputation;
        }

        public Timestamp getBountyExpiresAt() {
            return bountyExpiresAt;
        }
    }

    private void ensureQuestionBountyColumns() {
        ensureQuestionColumn(
                "bounty_amount",
                "ALTER TABLE Questions ADD bounty_amount INT NOT NULL CONSTRAINT DF_Questions_bounty_amount DEFAULT 0"
        );
        ensureQuestionColumn(
                "bounty_awarder_id",
                "ALTER TABLE Questions ADD bounty_awarder_id BIGINT NULL"
        );
        ensureQuestionColumn(
                "bounty_started_at",
                "ALTER TABLE Questions ADD bounty_started_at DATETIME NULL"
        );
        ensureQuestionColumn(
                "bounty_expires_at",
                "ALTER TABLE Questions ADD bounty_expires_at DATETIME NULL"
        );
    }

    private void ensureQuestionColumn(String columnName, String alterSql) {
        String checkSql = "SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'Questions' AND COLUMN_NAME = ?";

        try (Connection con = getConnection();
                PreparedStatement check = con.prepareStatement(checkSql)) {
            check.setString(1, columnName);
            try (ResultSet rs = check.executeQuery()) {
                if (!rs.next()) {
                    try (Statement st = con.createStatement()) {
                        st.executeUpdate(alterSql);
                    }
                }
            }
        } catch (Exception e) {
            // Keep app booting even if schema auto-fix cannot run.
        }
    }

    // 1. Hàm chính lấy danh sách câu hỏi
    public List<QuestionDTO> getQuestions(int pageIndex, int pageSize, String sortBy, String keyword, String filterType, String tag) {
        List<QuestionDTO> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT q.*, u.username, up.avatar_url, ")
                .append("(SELECT COUNT(*) FROM Answers a WHERE a.question_id = q.question_id) as ans_count ")
                .append("FROM Questions q ")
                .append("JOIN Users u ON q.user_id = u.user_id ")
                .append("LEFT JOIN User_Profile up ON u.user_id = up.user_id ")
                .append("WHERE ISNULL(q.is_deleted, 0) = 0 ");

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (q.title LIKE ? OR q.body LIKE ?) ");
        }
        if ("unanswered".equals(filterType)) {
            sql.append(" AND (SELECT COUNT(*) FROM Answers a WHERE a.question_id = q.question_id) = 0 ");
        }
        // Logic lọc theo tag mới thêm
        if (tag != null && !tag.trim().isEmpty()) {
            sql.append(" AND q.question_id IN (SELECT qt.question_id FROM Question_Tags qt JOIN Tags t ON qt.tag_id = t.tag_id WHERE t.tag_name = ?) ");
        }

        if ("views".equals(sortBy)) {
            sql.append(" ORDER BY q.view_count DESC ");
        } else if ("active".equals(sortBy)) {
            sql.append(" ORDER BY q.updated_at DESC ");
        } else if ("voted".equals(sortBy)) {
            sql.append(" ORDER BY q.Score DESC ");
        } else {
            sql.append(" ORDER BY q.created_at DESC ");
        }

        sql.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try {
            Connection conn = getConnection();
            PreparedStatement st = conn.prepareStatement(sql.toString());
            int paramIndex = 1;

            if (keyword != null && !keyword.trim().isEmpty()) {
                st.setString(paramIndex++, "%" + keyword + "%");
                st.setString(paramIndex++, "%" + keyword + "%");
            }
            // Truyền tham số cho tag
            if (tag != null && !tag.trim().isEmpty()) {
                st.setString(paramIndex++, tag);
            }

            st.setInt(paramIndex++, (pageIndex - 1) * pageSize);
            st.setInt(paramIndex++, pageSize);

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            rs.close();
            st.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. Hàm hỗ trợ Map dữ liệu từ ResultSet sang Object
    private QuestionDTO mapRow(ResultSet rs) throws SQLException {
        QuestionDTO q = new QuestionDTO();
        q.setQuestionId(rs.getLong("question_id"));
        q.setUserId(rs.getLong("user_id"));
        q.setTitle(rs.getString("title"));
        q.setBody(rs.getString("body"));
        q.setCodeSnippet(getNullableString(rs, "code_snippet"));
        q.setViewCount(rs.getInt("view_count"));
        q.setIsClosed(getBooleanOrDefault(rs, "is_closed", false));
        q.setClosedReason(getNullableString(rs, "closed_reason"));
        q.setClosedAt(getNullableTimestamp(rs, "closed_at"));
        q.setScore(rs.getInt("Score"));
        q.setCreatedAt(rs.getTimestamp("created_at"));
        q.setUpdatedAt(getNullableTimestamp(rs, "updated_at"));
        q.setAcceptedAnswerId(getNullableLong(rs, "accepted_answer_id"));
        Integer bountyAmount = getNullableInt(rs, "bounty_amount");
        q.setBountyAmount(bountyAmount != null ? bountyAmount : 0);
        q.setBountyAwarderId(getNullableLong(rs, "bounty_awarder_id"));
        q.setBountyStartedAt(getNullableTimestamp(rs, "bounty_started_at"));
        q.setBountyExpiresAt(getNullableTimestamp(rs, "bounty_expires_at"));

        q.setAuthorName(rs.getString("username"));
        q.setAuthorAvatar(getNullableString(rs, "avatar_url"));
        Integer authorReputation = getNullableInt(rs, "author_reputation");
        if (authorReputation != null) {
            q.setAuthorReputation(authorReputation);
        }
        q.setAnswerCount(rs.getInt("ans_count"));
        q.setTags(getTagsByQuestionId(q.getQuestionId()));

        return q;
    }

    private String getNullableString(ResultSet rs, String column) {
        try {
            return rs.getString(column);
        } catch (SQLException e) {
            return null;
        }
    }

    private java.sql.Timestamp getNullableTimestamp(ResultSet rs, String column) {
        try {
            return rs.getTimestamp(column);
        } catch (SQLException e) {
            return null;
        }
    }

    private Long getNullableLong(ResultSet rs, String column) {
        try {
            long value = rs.getLong(column);
            return rs.wasNull() ? null : value;
        } catch (SQLException e) {
            return null;
        }
    }

    private Integer getNullableInt(ResultSet rs, String column) {
        try {
            int value = rs.getInt(column);
            return rs.wasNull() ? null : value;
        } catch (SQLException e) {
            return null;
        }
    }

    private boolean getBooleanOrDefault(ResultSet rs, String column, boolean defaultValue) {
        try {
            return rs.getBoolean(column);
        } catch (SQLException e) {
            return defaultValue;
        }
    }

    // 3. Hàm đếm tổng số câu hỏi 
    public int getTotalQuestions(String keyword, String filterType, String tag) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Questions q WHERE ISNULL(q.is_deleted, 0) = 0 ");

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (q.title LIKE ? OR q.body LIKE ?) ");
        }
        if ("unanswered".equals(filterType)) {
            sql.append(" AND (SELECT COUNT(*) FROM Answers a WHERE a.question_id = q.question_id) = 0 ");
        }
        if (tag != null && !tag.trim().isEmpty()) {
            sql.append(" AND q.question_id IN (SELECT qt.question_id FROM Question_Tags qt JOIN Tags t ON qt.tag_id = t.tag_id WHERE t.tag_name = ?) ");
        }

        try {
            Connection conn = getConnection();
            PreparedStatement st = conn.prepareStatement(sql.toString());
            int paramIndex = 1;

            if (keyword != null && !keyword.trim().isEmpty()) {
                st.setString(paramIndex++, "%" + keyword + "%");
                st.setString(paramIndex++, "%" + keyword + "%");
            }
            if (tag != null && !tag.trim().isEmpty()) {
                st.setString(paramIndex++, tag);
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

    // 4. Hàm Search 
    public List<QuestionDTO> searchQuestions(String keyword) {
        return getQuestions(1, 20, "newest", keyword, "all", null);
    }

    // 5. Hàm lấy danh sách Tags của 1 câu hỏi
    public List<String> getTagsByQuestionId(long questionId) {
        List<String> tags = new ArrayList<>();
        String sql = "SELECT t.tag_name FROM Tags t "
                + "JOIN Question_Tags qt ON t.tag_id = qt.tag_id "
                + "WHERE qt.question_id = ?";
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

    // 6. Hàm thêm Câu hỏi mới kèm Tags 
    // Hàm quản lý transaction
    public boolean insertQuestionWithTags(long userId, String title, String body, String tagsInput, int userReputation) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false); 

            // Insert Question
            long questionId = insertQuestionCore(conn, userId, title, body);

            // xử lý Tags, truyền thêm userReputation vào
            if (questionId != -1 && tagsInput != null && !tagsInput.trim().isEmpty()) {
                processTagsForQuestion(conn, questionId, tagsInput, userReputation);
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
            e.printStackTrace();
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
        if (tagsInput == null || tagsInput.trim().isEmpty()) {
            return newTags;
        }

        String[] tagsArray = tagsInput.split(",");
        String sqlCheck = "SELECT tag_id FROM Tags WHERE tag_name = ?";

        try (Connection conn = getConnection(); PreparedStatement psCheck = conn.prepareStatement(sqlCheck)) {

            for (String tag : tagsArray) {
                String tagName = tag.trim().toLowerCase();
                if (tagName.isEmpty()) {
                    continue;
                }

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

        try (PreparedStatement psCheck = conn.prepareStatement(sqlCheck); PreparedStatement psInsertTag = conn.prepareStatement(sqlInsertTag, Statement.RETURN_GENERATED_KEYS); PreparedStatement psInsertQT = conn.prepareStatement(sqlInsertQT)) {

            for (String tag : tagsArray) {
                String tagName = tag.trim().toLowerCase();
                if (tagName.isEmpty()) {
                    continue;
                }

                long tagId = -1;

                // Check xem tag này đã tồn tại trong DB chưa
                psCheck.setString(1, tagName);
                try (ResultSet rsCheck = psCheck.executeQuery()) {
                    if (rsCheck.next()) {
                        tagId = rsCheck.getLong("tag_id"); // Tag cũ
                    }
                }

                // TAG MỚI HOÀN TOÀN 
                if (tagId == -1) {
                    if (userReputation < 50) {
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

                // Link Question và Tag
                if (tagId != -1) {
                    psInsertQT.setLong(1, questionId);
                    psInsertQT.setLong(2, tagId);
                    psInsertQT.executeUpdate();
                }
            }
        }
    }

    // Lấy thông tin chi tiết một câu hỏi theo ID
    public QuestionDTO getQuestionById(long questionId) {
        String sql = "SELECT q.*, u.username, u.Reputation AS author_reputation, up.avatar_url, "
                + "(SELECT COUNT(*) FROM Answers a WHERE a.question_id = q.question_id) as ans_count "
                + "FROM Questions q "
                + "JOIN Users u ON q.user_id = u.user_id "
                + "LEFT JOIN User_Profile up ON u.user_id = up.user_id "
                + "WHERE q.question_id = ? AND ISNULL(q.is_deleted, 0) = 0";
        try {
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, questionId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                QuestionDTO q = mapRow(rs);
                rs.close();
                ps.close();
                conn.close();
                return q;
            }
            rs.close();
            ps.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Lấy top các tag phổ biến nhất dựa trên số lượng câu hỏi
    public List<String> getPopularTags(int limit) {
        List<String> tags = new ArrayList<>();
        String sql = "SELECT t.tag_name, COUNT(qt.question_id) as count "
                + "FROM Tags t "
                + "JOIN Question_Tags qt ON t.tag_id = qt.tag_id "
                + "GROUP BY t.tag_name "
                + "ORDER BY count DESC "
                + "OFFSET 0 ROWS FETCH NEXT ? ROWS ONLY";
        try {
            Connection conn = getConnection();
            PreparedStatement st = conn.prepareStatement(sql);
            st.setInt(1, limit);
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

    // Lấy các câu hỏi cùng tag với câu hỏi cho trước
    public List<QuestionDTO> getRelatedQuestions(long questionId, int limit) {
        List<QuestionDTO> list = new ArrayList<>();
        String sql = "SELECT q.*, u.username, u.Reputation AS author_reputation, up.avatar_url, "
                + "(SELECT COUNT(*) FROM Answers a WHERE a.question_id = q.question_id) as ans_count "
                + "FROM Questions q "
                + "JOIN Users u ON q.user_id = u.user_id "
                + "LEFT JOIN User_Profile up ON u.user_id = up.user_id "
                + "WHERE q.question_id != ? AND ISNULL(q.is_deleted, 0) = 0 "
                + "AND q.question_id IN ("
                + "  SELECT qt2.question_id FROM Question_Tags qt2 "
                + "  WHERE qt2.tag_id IN (SELECT qt1.tag_id FROM Question_Tags qt1 WHERE qt1.question_id = ?)"
                + ") "
                + "ORDER BY q.created_at DESC "
                + "OFFSET 0 ROWS FETCH NEXT ? ROWS ONLY";
        try {
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, questionId);
            ps.setLong(2, questionId);
            ps.setInt(3, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            rs.close();
            ps.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Kiểm tra câu hỏi có bị đóng không
    public boolean isQuestionClosed(long questionId) {
        String sql = "SELECT is_closed FROM Questions WHERE question_id = ?";
        try {
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, questionId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                boolean closed = rs.getBoolean("is_closed");
                rs.close();
                ps.close();
                conn.close();
                return closed;
            }
            rs.close();
            ps.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void incrementViewCount(long questionId) {
        String sql = "UPDATE Questions SET view_count = ISNULL(view_count, 0) + 1 WHERE question_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, questionId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BountyPlacementResult placeBounty(long questionId, long actorUserId, int bountyAmount) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            String questionSql = "SELECT question_id, user_id, accepted_answer_id, is_closed, bounty_amount, bounty_expires_at "
                    + "FROM Questions WITH (UPDLOCK, HOLDLOCK) "
                    + "WHERE question_id = ? AND ISNULL(is_deleted, 0) = 0";

            long questionOwnerId;
            Long acceptedAnswerId;
            boolean isClosed;
            int currentBountyAmount;
            Timestamp currentBountyExpiresAt;

            try (PreparedStatement ps = conn.prepareStatement(questionSql)) {
                ps.setLong(1, questionId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new IllegalArgumentException("Question not found");
                    }
                    questionOwnerId = rs.getLong("user_id");
                    acceptedAnswerId = rs.getObject("accepted_answer_id") != null ? rs.getLong("accepted_answer_id") : null;
                    isClosed = rs.getBoolean("is_closed");
                    currentBountyAmount = rs.getObject("bounty_amount") != null ? rs.getInt("bounty_amount") : 0;
                    currentBountyExpiresAt = rs.getTimestamp("bounty_expires_at");
                }
            }

            if (questionOwnerId != actorUserId) {
                throw new IllegalStateException("Only the question owner can add a bounty");
            }
            if (isClosed) {
                throw new IllegalStateException("Cannot add bounty to a closed question");
            }
            if (acceptedAnswerId != null) {
                throw new IllegalStateException("Cannot add bounty to a question that already has an accepted answer");
            }
            if (currentBountyAmount > 0 && currentBountyExpiresAt != null && currentBountyExpiresAt.after(new Timestamp(System.currentTimeMillis()))) {
                throw new IllegalStateException("This question already has an active bounty");
            }

            int currentReputation = getUserReputation(conn, actorUserId);
            if (currentReputation < MIN_BOUNTY_REPUTATION) {
                throw new IllegalStateException("You need at least " + MIN_BOUNTY_REPUTATION + " reputation to add a bounty");
            }
            if (currentReputation < bountyAmount) {
                throw new IllegalStateException("You do not have enough reputation for this bounty amount");
            }

            Timestamp newExpiry = new Timestamp(System.currentTimeMillis() + (BOUNTY_DURATION_DAYS * 24L * 60L * 60L * 1000L));

            try (PreparedStatement ps = conn.prepareStatement("UPDATE Users SET Reputation = Reputation - ? WHERE user_id = ?")) {
                ps.setInt(1, bountyAmount);
                ps.setLong(2, actorUserId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE Questions SET bounty_amount = ?, bounty_awarder_id = ?, bounty_started_at = GETDATE(), bounty_expires_at = ? WHERE question_id = ?")) {
                ps.setInt(1, bountyAmount);
                ps.setLong(2, actorUserId);
                ps.setTimestamp(3, newExpiry);
                ps.setLong(4, questionId);
                ps.executeUpdate();
            }

            insertReputationHistory(conn, actorUserId, -bountyAmount,
                    "Started a bounty on a question", "bounty_start",
                    "question", questionId, actorUserId);

            int updatedReputation = getUserReputation(conn, actorUserId);
            conn.commit();
            return new BountyPlacementResult(updatedReputation, newExpiry);
        } catch (Exception e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    public List<QuestionDTO> getActiveBountyQuestions(String sortBy) {
        List<QuestionDTO> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT q.*, u.username, u.Reputation AS author_reputation, up.avatar_url, ")
                .append("(SELECT COUNT(*) FROM Answers a WHERE a.question_id = q.question_id) as ans_count ")
                .append("FROM Questions q ")
                .append("JOIN Users u ON q.user_id = u.user_id ")
                .append("LEFT JOIN User_Profile up ON u.user_id = up.user_id ")
                .append("WHERE ISNULL(q.is_deleted, 0) = 0 ")
                .append("AND ISNULL(q.bounty_amount, 0) > 0 ")
                .append("AND q.bounty_expires_at IS NOT NULL ")
                .append("AND q.bounty_expires_at > GETDATE() ");

        if ("expiring".equalsIgnoreCase(sortBy)) {
            sql.append("ORDER BY q.bounty_expires_at ASC, q.bounty_amount DESC, q.created_at DESC ");
        } else if ("newest".equalsIgnoreCase(sortBy)) {
            sql.append("ORDER BY q.created_at DESC, q.bounty_amount DESC, q.bounty_expires_at ASC ");
        } else {
            sql.append("ORDER BY q.bounty_amount DESC, q.bounty_expires_at ASC, q.created_at DESC ");
        }

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql.toString());
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private int getUserReputation(Connection conn, long userId) throws SQLException {
        String sql = "SELECT Reputation FROM Users WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("Reputation");
                }
            }
        }
        return 0;
    }

    // Cập nhật câu hỏi kèm lịch sử chỉnh sửa và xử lý tags
    public boolean updateQuestionWithHistory(long questionId, long editorId, String title, String body, String codeSnippet, String tags, int userReputation) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            // Tải dữ liệu cũ để lưu vào lịch sử
            String loadSql = "SELECT title, body FROM Questions WHERE question_id = ?";
            String oldTitle = null, oldBody = null;
            try (PreparedStatement ps = conn.prepareStatement(loadSql)) {
                ps.setLong(1, questionId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }
                    oldTitle = rs.getString("title");
                    oldBody = rs.getString("body");
                }
            }

            // Lưu lịch sử chỉnh sửa
            String oldTags = String.join(",", getTagsByQuestionId(questionId));
            String editedContent = "title=" + (oldTitle != null ? oldTitle : "")
                    + "\nbody=" + (oldBody != null ? oldBody : "")
                    + "\ntags=" + oldTags;
            String historySql = "INSERT INTO Post_Edit_History (post_type, post_id, title, body, code_snippet, tags, editor_id, edited_content, edited_at) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, GETDATE())";
            try (PreparedStatement ps = conn.prepareStatement(historySql)) {
                ps.setString(1, "question");
                ps.setLong(2, questionId);
                ps.setString(3, oldTitle != null ? oldTitle : "");
                ps.setString(4, oldBody != null ? oldBody : "");
                ps.setString(5, "");
                ps.setString(6, oldTags);
                ps.setLong(7, editorId);
                ps.setString(8, editedContent);
                ps.executeUpdate();
            }

            // Cập nhật nội dung câu hỏi
            String updateSql = "UPDATE Questions SET title = ?, body = ?, updated_at = GETDATE() WHERE question_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setString(1, title);
                ps.setString(2, body);
                ps.setLong(3, questionId);
                if (ps.executeUpdate() == 0) {
                    conn.rollback();
                    return false;
                }
            }

            // Cập nhật tags nếu được cung cấp
            if (tags != null && !tags.trim().isEmpty()) {
                String deleteTagsSql = "DELETE FROM Question_Tags WHERE question_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(deleteTagsSql)) {
                    ps.setLong(1, questionId);
                    ps.executeUpdate();
                }
                processTagsForQuestion(conn, questionId, tags, userReputation);
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

    // Toggle chấp nhận câu trả lời: nếu đã accepted thì bỏ chọn, nếu chưa thì chọn.
    // Đồng thời cộng/trừ điểm reputation cho tác giả câu trả lời.
    // Toggle chấp nhận câu trả lời
    private QuestionAcceptState getQuestionAcceptState(Connection conn, long questionId) throws SQLException {
        String sql = "SELECT user_id, accepted_answer_id, bounty_amount, bounty_expires_at FROM Questions WHERE question_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, questionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                Long acceptedId = rs.getObject("accepted_answer_id") != null
                        ? rs.getLong("accepted_answer_id") : null;

                int bountyAmount = rs.getObject("bounty_amount") != null
                        ? rs.getInt("bounty_amount") : 0;

                Timestamp bountyExpiresAt = rs.getTimestamp("bounty_expires_at");

                return new QuestionAcceptState(rs.getLong("user_id"), acceptedId, bountyAmount, bountyExpiresAt);
            }
        }
    }

    private AnswerOwner getAnswerOwner(Connection conn, long answerId, long questionId) throws SQLException {
        String sql = "SELECT user_id FROM Answers WHERE answer_id = ? AND question_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, answerId);
            ps.setLong(2, questionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new AnswerOwner(rs.getLong("user_id"));
            }
        }
    }

    private boolean setAcceptedAnswer(Connection conn, long questionId, Long acceptedAnswerId) throws SQLException {
        String sql = "UPDATE Questions SET accepted_answer_id = ? WHERE question_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (acceptedAnswerId == null) {
                ps.setNull(1, java.sql.Types.BIGINT);
            } else {
                ps.setLong(1, acceptedAnswerId);
            }
            ps.setLong(2, questionId);
            return ps.executeUpdate() > 0;
        }
    }

    private void setAnswerAccepted(Connection conn, long answerId, boolean accepted) throws SQLException {
        String sql = "UPDATE Answers SET is_accepted = ? WHERE answer_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, accepted);
            ps.setLong(2, answerId);
            ps.executeUpdate();
        }
    }

    private boolean clearQuestionBounty(Connection conn, long questionId) throws SQLException {
        String sql = "UPDATE Questions SET bounty_amount = 0, bounty_awarder_id = NULL, bounty_started_at = NULL, bounty_expires_at = NULL WHERE question_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, questionId);
            return ps.executeUpdate() > 0;
        }
    }

    private void updateReputation(Connection conn, long userId, int delta) throws SQLException {
        String sql = "UPDATE Users SET Reputation = Reputation + ? WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, delta);
            ps.setLong(2, userId);
            ps.executeUpdate();
        }
    }

    private void insertReputationHistory(Connection conn, long userId, int delta,
            String reason, String eventType,
            String relatedPostType, long relatedPostId,
            long actorUserId) {
        String sql = "INSERT INTO Reputation_History "
                + "(user_id, delta, reason, event_type, related_post_type, related_post_id, actor_user_id, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, GETDATE())";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setInt(2, delta);
            ps.setString(3, reason);
            ps.setString(4, eventType);
            ps.setString(5, relatedPostType);
            ps.setLong(6, relatedPostId);
            ps.setLong(7, actorUserId);
            ps.executeUpdate();
        } catch (SQLException ignored) {
        }
    }
    
    private static class QuestionAcceptState {
    private final long questionOwnerId;
    private final Long acceptedAnswerId;
    private final int bountyAmount;
    private final Timestamp bountyExpiresAt;

    private QuestionAcceptState(long questionOwnerId, Long acceptedAnswerId, int bountyAmount, Timestamp bountyExpiresAt) {
        this.questionOwnerId = questionOwnerId;
        this.acceptedAnswerId = acceptedAnswerId;
        this.bountyAmount = bountyAmount;
        this.bountyExpiresAt = bountyExpiresAt;
    }

    private boolean hasActiveBounty() {
        return bountyAmount > 0 && bountyExpiresAt != null
                && bountyExpiresAt.after(new Timestamp(System.currentTimeMillis()));
    }
}

private static class AnswerOwner {
    private final long userId;

    private AnswerOwner(long userId) {
        this.userId = userId;
    }
}

    public boolean toggleAcceptAnswer(long questionId, long answerId, long questionOwnerId) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            // 1. Lấy trạng thái question
            QuestionAcceptState state = getQuestionAcceptState(conn, questionId);
            if (state == null || state.questionOwnerId != questionOwnerId) {
                conn.rollback();
                return false;
            }

            // 2. Lấy owner của answer
            AnswerOwner targetAnswerOwner = getAnswerOwner(conn, answerId, questionId);
            if (targetAnswerOwner == null) {
                conn.rollback();
                return false;
            }

            Long currentAccepted = state.acceptedAnswerId;
            boolean isToggleOff = currentAccepted != null && currentAccepted.equals(answerId);
            Long newAccepted = isToggleOff ? null : answerId;

            if (newAccepted != null && state.hasActiveBounty() && targetAnswerOwner.userId == state.questionOwnerId) {
                conn.rollback();
                throw new IllegalStateException("You cannot accept your own answer while this question has an active bounty");
            }

            // 3. Bỏ accepted cũ (nếu có)
            if (currentAccepted != null) {
                setAnswerAccepted(conn, currentAccepted, false);
            }

            // 4. Update question.accepted_answer_id
            if (!setAcceptedAnswer(conn, questionId, newAccepted)) {
                conn.rollback();
                return false;
            }

            // 5. Set accepted mới
            if (newAccepted != null) {
                setAnswerAccepted(conn, newAccepted, true);
            }

            // 6. Trừ điểm answer cũ
            if (currentAccepted != null) {
                AnswerOwner oldOwner = getAnswerOwner(conn, currentAccepted, questionId);
                if (oldOwner != null && oldOwner.userId != state.questionOwnerId) {

                    updateReputation(conn, oldOwner.userId, -15);
                    updateReputation(conn, state.questionOwnerId, -2);

                    insertReputationHistory(conn, oldOwner.userId, -15,
                            "Accepted answer removed", "accept_removed",
                            "answer", currentAccepted, questionOwnerId);

                    insertReputationHistory(conn, state.questionOwnerId, -2,
                            "Acceptance reward removed", "accept_removed_question_owner",
                            "question", questionId, questionOwnerId);
                }
            }

            // 7. Cộng điểm answer mới
            if (newAccepted != null && targetAnswerOwner.userId != state.questionOwnerId) {

                updateReputation(conn, targetAnswerOwner.userId, 15);
                updateReputation(conn, state.questionOwnerId, 2);

                insertReputationHistory(conn, targetAnswerOwner.userId, 15,
                        "Answer accepted", "accept",
                        "answer", newAccepted, questionOwnerId);

                insertReputationHistory(conn, state.questionOwnerId, 2,
                        "Accepted an answer", "accept_question_owner",
                        "question", questionId, questionOwnerId);
            }

            if (newAccepted != null && state.hasActiveBounty()) {
                updateReputation(conn, targetAnswerOwner.userId, state.bountyAmount);
                insertReputationHistory(conn, targetAnswerOwner.userId, state.bountyAmount,
                        "Received bounty award", "bounty_award",
                        "answer", newAccepted, questionOwnerId);

                if (!clearQuestionBounty(conn, questionId)) {
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();
            return true;

        } catch (Exception e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
    //======================================================
    public long getLastInsertedQuestionId() {

    String sql = "SELECT TOP 1 question_id FROM Questions ORDER BY question_id DESC";

    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        if (rs.next()) {
            return rs.getLong("question_id");
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return -1;
}
    private List<String> getTagNamesByQuestionId(long questionId) {

    List<String> tags = new ArrayList<>();

    String sql = "SELECT t.tag_name FROM Tags t "
               + "JOIN Question_Tags qt ON t.tag_id = qt.tag_id "
               + "WHERE qt.question_id = ?";

    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setLong(1, questionId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            tags.add(rs.getString("tag_name"));
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return tags;
}
        // ================= NOTIFICATION =================
    public void createNotificationForNewQuestion(long authorId, long questionId, String title) {

        Set<Long> userFollowers = new HashSet<>();
        Set<Long> tagFollowers = new HashSet<>();

        String username = getUsernameById(authorId);

        // USER FOLLOW
        String sqlUser = "SELECT follower_id FROM UserFollow WHERE following_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlUser)) {

            ps.setLong(1, authorId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                long uid = rs.getLong("follower_id");
                if (uid != authorId) {
                    userFollowers.add(uid);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // TAG FOLLOW
        String sqlTag = """
            SELECT DISTINCT tf.user_id, t.tag_name
            FROM TagFollow tf
            JOIN Question_Tags qt ON tf.tag_id = qt.tag_id
            JOIN Tags t ON t.tag_id = qt.tag_id
            WHERE qt.question_id = ?
        """;

        Map<Long, List<String>> tagMap = new HashMap<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlTag)) {

            ps.setLong(1, questionId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                long uid = rs.getLong("user_id");
                String tagName = rs.getString("tag_name");

                if (uid == authorId) continue;

                tagFollowers.add(uid);
                tagMap.computeIfAbsent(uid, k -> new ArrayList<>()).add(tagName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // INSERT
        String insertSql = "INSERT INTO Notifications (user_id, type, content) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(insertSql)) {

            // USER
            for (Long userId : userFollowers) {
                String content = "User " + username + " vừa đăng bài mới";

                ps.setLong(1, userId);
                ps.setString(2, "user_post");
                ps.setString(3, content);
                ps.executeUpdate();
            }

            // TAG
            for (Long userId : tagFollowers) {
                List<String> tags = tagMap.get(userId);
                String tagStr = String.join(", ", tags);

                String content = "Có một bài đăng liên quan đến tag " + tagStr;

                ps.setLong(1, userId);
                ps.setString(2, "tag_post");
                ps.setString(3, content);
                ps.executeUpdate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private String getUsernameById(long userId) {

    String sql = "SELECT username FROM Users WHERE user_id = ?";

    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setLong(1, userId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getString("username");
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return "Unknown";
}
    

    


/**
 * Extract tags từ danh sách question đã xem
 */
public List<String> extractTagsFromViewed(List<Long> viewedIds) {
    List<String> tags = new ArrayList<>();

    if (viewedIds == null || viewedIds.isEmpty()) {
        return tags;
    }

    try {
        Connection conn = getConnection();

        for (Long qId : viewedIds) {
            tags.addAll(getTagsByQuestionId(qId));
        }

        conn.close();

    } catch (Exception e) {
        e.printStackTrace();
    }

    // remove duplicate + limit
    return tags.stream()
            .distinct()
            .limit(5)
            .toList();
}

/**
 * Remove duplicate question + loại bỏ câu đã xem
 */
private List<QuestionDTO> distinctAndLimit(List<QuestionDTO> list, List<Long> excludeIds, int limit) {
    List<QuestionDTO> result = new ArrayList<>();
    java.util.Set<Long> seen = new java.util.HashSet<>();

    for (QuestionDTO q : list) {
        if (seen.contains(q.getQuestionId())) continue;
        if (excludeIds != null && excludeIds.contains(q.getQuestionId())) continue;

        seen.add(q.getQuestionId());
        result.add(q);

        if (result.size() >= limit) break;
    }

    return result;
}


public List<QuestionDTO> getRecommendedQuestions(List<Long> viewedIds, int limit) {
    if (viewedIds == null || viewedIds.isEmpty()) {
        return getPopularQuestions(0, limit);
    }

    List<String> tags = extractTagsFromViewed(viewedIds);
    List<String> keywords = extractKeywordsFromViewed(viewedIds, 8);
    List<QuestionDTO> recommended = findRecommendedByProfile(tags, keywords, viewedIds, limit);

    if (recommended.size() < limit) {
        List<QuestionDTO> fallback = getPopularQuestions(0, limit * 2);
        recommended.addAll(fallback);
        recommended = distinctAndLimit(recommended, viewedIds, limit);
    }

    return recommended;
}

public List<String> extractKeywordsFromViewed(List<Long> viewedIds, int limit) {
    Map<String, Integer> frequencies = new HashMap<>();

    if (viewedIds == null || viewedIds.isEmpty()) {
        return new ArrayList<>();
    }

    StringBuilder sql = new StringBuilder();
    sql.append("SELECT title, body FROM Questions WHERE question_id IN (");
    appendPlaceholders(sql, viewedIds.size());
    sql.append(") AND ISNULL(is_deleted, 0) = 0");

    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql.toString())) {
        int index = 1;
        for (Long id : viewedIds) {
            ps.setLong(index++, id);
        }

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                collectKeywordFrequency(frequencies, rs.getString("title"));
                collectKeywordFrequency(frequencies, rs.getString("body"));
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

    return frequencies.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder())
                    .thenComparing(Map.Entry::getKey))
            .limit(limit)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
}

private List<QuestionDTO> findRecommendedByProfile(List<String> tags, List<String> keywords,
        List<Long> excludeIds, int limit) {
    List<QuestionDTO> list = new ArrayList<>();
    boolean hasTags = tags != null && !tags.isEmpty();
    boolean hasKeywords = keywords != null && !keywords.isEmpty();

    if (!hasTags && !hasKeywords) {
        return list;
    }

    StringBuilder sql = new StringBuilder();
    sql.append("SELECT TOP (?) q.*, u.username, u.Reputation AS author_reputation, up.avatar_url, ")
            .append("(SELECT COUNT(*) FROM Answers a WHERE a.question_id = q.question_id) as ans_count, ")
            .append("CAST((");

    List<String> scoreParts = new ArrayList<>();
    if (hasTags) {
        StringBuilder tagScore = new StringBuilder();
        tagScore.append("(SELECT COUNT(DISTINCT qt.tag_id) * 8.0 FROM Question_Tags qt ")
                .append("JOIN Tags t ON qt.tag_id = t.tag_id ")
                .append("WHERE qt.question_id = q.question_id AND t.tag_name IN (");
        appendPlaceholders(tagScore, tags.size());
        tagScore.append("))");
        scoreParts.add(tagScore.toString());
    }

    if (hasKeywords) {
        for (int i = 0; i < keywords.size(); i++) {
            scoreParts.add("CASE WHEN q.title LIKE ? THEN 5.0 ELSE 0 END");
            scoreParts.add("CASE WHEN q.body LIKE ? THEN 2.5 ELSE 0 END");
        }
    }

    scoreParts.add("(q.Score * 1.5)");
    scoreParts.add("(q.view_count * 0.08)");
    scoreParts.add("CASE WHEN q.accepted_answer_id IS NOT NULL THEN 2.0 ELSE 0 END");

    sql.append(String.join(" + ", scoreParts))
            .append(") AS FLOAT) AS recommendation_score ")
            .append("FROM Questions q ")
            .append("JOIN Users u ON q.user_id = u.user_id ")
            .append("LEFT JOIN User_Profile up ON u.user_id = up.user_id ")
            .append("WHERE ISNULL(q.is_deleted, 0) = 0 ");

    if (excludeIds != null && !excludeIds.isEmpty()) {
        sql.append("AND q.question_id NOT IN (");
        appendPlaceholders(sql, excludeIds.size());
        sql.append(") ");
    }

    sql.append("AND (");
    List<String> matchParts = new ArrayList<>();
    if (hasTags) {
        StringBuilder tagMatch = new StringBuilder();
        tagMatch.append("EXISTS (SELECT 1 FROM Question_Tags qt ")
                .append("JOIN Tags t ON qt.tag_id = t.tag_id ")
                .append("WHERE qt.question_id = q.question_id AND t.tag_name IN (");
        appendPlaceholders(tagMatch, tags.size());
        tagMatch.append("))");
        matchParts.add(tagMatch.toString());
    }
    if (hasKeywords) {
        for (int i = 0; i < keywords.size(); i++) {
            matchParts.add("q.title LIKE ?");
            matchParts.add("q.body LIKE ?");
        }
    }
    sql.append(String.join(" OR ", matchParts))
            .append(") ")
            .append("ORDER BY recommendation_score DESC, q.view_count DESC, q.created_at DESC");

    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql.toString())) {
        int index = 1;
        ps.setInt(index++, limit);

        if (hasTags) {
            for (String tag : tags) {
                ps.setString(index++, tag);
            }
        }

        if (hasKeywords) {
            for (String keyword : keywords) {
                String pattern = "%" + keyword + "%";
                ps.setString(index++, pattern);
                ps.setString(index++, pattern);
            }
        }

        if (excludeIds != null && !excludeIds.isEmpty()) {
            for (Long id : excludeIds) {
                ps.setLong(index++, id);
            }
        }

        if (hasTags) {
            for (String tag : tags) {
                ps.setString(index++, tag);
            }
        }

        if (hasKeywords) {
            for (String keyword : keywords) {
                String pattern = "%" + keyword + "%";
                ps.setString(index++, pattern);
                ps.setString(index++, pattern);
            }
        }

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                QuestionDTO question = mapRow(rs);
                question.setPopularScore(rs.getDouble("recommendation_score"));
                list.add(question);
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

    return list;
}

private void collectKeywordFrequency(Map<String, Integer> frequencies, String text) {
    if (text == null || text.trim().isEmpty()) {
        return;
    }

    String normalized = text.toLowerCase(Locale.ENGLISH)
            .replaceAll("<[^>]+>", " ")
            .replaceAll("[^a-z0-9#+._-]", " ");

    for (String token : normalized.split("\\s+")) {
        String clean = token.trim();
        if (clean.length() < 3 || clean.length() > 24) {
            continue;
        }
        if (RECOMMENDATION_STOP_WORDS.contains(clean)) {
            continue;
        }
        if (!clean.matches(".*[a-z].*")) {
            continue;
        }
        frequencies.merge(clean, 1, Integer::sum);
    }
}

private void appendPlaceholders(StringBuilder sql, int count) {
    for (int i = 0; i < count; i++) {
        sql.append("?");
        if (i < count - 1) {
            sql.append(",");
        }
    }
}
 public List<QuestionDTO> getPopularQuestions(long excludeQuestionId, int limit) {
        List<QuestionDTO> list = new ArrayList<>();
        String sql = "SELECT q.*, u.username, u.Reputation AS author_reputation, up.avatar_url, "
                + "(SELECT COUNT(*) FROM Answers a WHERE a.question_id = q.question_id) as ans_count, "
                + "CAST((q.Score * 2.0) + (q.view_count / 10.0) - DATEDIFF(DAY, q.created_at, GETDATE()) AS FLOAT) AS popular_score "
                + "FROM Questions q "
                + "JOIN Users u ON q.user_id = u.user_id "
                + "LEFT JOIN User_Profile up ON u.user_id = up.user_id "
                + "WHERE q.question_id <> ? AND ISNULL(q.is_deleted, 0) = 0 "
                + "ORDER BY popular_score DESC, q.view_count DESC, q.created_at DESC "
                + "OFFSET 0 ROWS FETCH NEXT ? ROWS ONLY";
        try {
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, excludeQuestionId);
            ps.setInt(2, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                QuestionDTO question = mapRow(rs);
                question.setPopularScore(rs.getDouble("popular_score"));
                list.add(question);
            }
            rs.close();
            ps.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}

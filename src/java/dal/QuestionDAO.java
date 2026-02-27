package dal;

import config.DBContext;
import dto.QuestionDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class QuestionDAO {
    private final DBContext db = new DBContext();

    // Tạo câu hỏi mới với tags
    public long createQuestion(long userId, String title, String body, String codeSnippet, String tagsStr) throws Exception {
        String sql = "INSERT INTO Questions (user_id, title, body, code_snippet, view_count, is_closed, created_at, updated_at, Score) " +
                "VALUES (?, ?, ?, ?, 0, 0, GETDATE(), GETDATE(), 0)";

        long questionId = -1;

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, userId);
            ps.setString(2, title);
            ps.setString(3, body);
            ps.setString(4, codeSnippet);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    questionId = rs.getLong(1);
                }
            }
        }

        if (questionId > 0 && tagsStr != null && !tagsStr.trim().isEmpty()) {
            addTagsToQuestion(questionId, tagsStr);
        }

        return questionId;
    }

    // Thêm tags cho câu hỏi
    private void addTagsToQuestion(long questionId, String tagsStr) throws Exception {
        String[] tagNames = tagsStr.split(",");

        try (Connection con = db.getConnection()) {
            for (String tagName : tagNames) {
                tagName = tagName.trim();
                if (tagName.isEmpty()) {
                    continue;
                }

                long tagId = getOrCreateTag(con, tagName);

                String linkSql = "INSERT INTO Question_Tags (question_id, tag_id) VALUES (?, ?)";
                try (PreparedStatement ps = con.prepareStatement(linkSql)) {
                    ps.setLong(1, questionId);
                    ps.setLong(2, tagId);
                    ps.executeUpdate();
                }
            }
        }
    }

    // Lấy hoặc tạo tag
    private long getOrCreateTag(Connection con, String tagName) throws Exception {
        String select = "SELECT tag_id FROM Tags WHERE tag_name = ?";

        try (PreparedStatement ps = con.prepareStatement(select)) {
            ps.setString(1, tagName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }

        String insert = "INSERT INTO Tags (tag_name) VALUES (?)";

        try (PreparedStatement ps = con.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, tagName);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }

        return -1;
    }

    // Lấy câu hỏi theo ID
    public QuestionDTO getQuestionById(long questionId) throws Exception {
        String sql = "SELECT q.*, u.username FROM Questions q " +
                "JOIN Users u ON q.user_id = u.user_id WHERE q.question_id = ?";

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, questionId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    QuestionDTO q = mapQuestion(rs);
                    q.setTags(getTagsByQuestionId(questionId));
                    return q;
                }
            }
        }
        return null;
    }

    // Lấy danh sách câu hỏi với tìm kiếm, bộ lọc, sắp xếp, phân trang
    public List<QuestionDTO> getQuestions(int pageIndex, int pageSize, String sortBy, String keyword, String filterType) throws Exception {
        List<QuestionDTO> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT q.*, u.username, up.avatar_url, " +
                "(SELECT COUNT(*) FROM Answers a WHERE a.question_id = q.question_id) as ans_count " +
                "FROM Questions q " +
                "JOIN Users u ON q.user_id = u.user_id " +
                "LEFT JOIN User_Profile up ON u.user_id = up.user_id WHERE 1=1 ");

        // Tìm kiếm (Search)
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (q.title LIKE ? OR q.body LIKE ?) ");
        }

        // Bộ lọc (Filter)
        if ("unanswered".equals(filterType)) {
            sql.append(" AND (SELECT COUNT(*) FROM Answers a WHERE a.question_id = q.question_id) = 0 ");
        }

        // Sắp xếp (Sorting)
        switch (sortBy) {
            case "views" -> sql.append(" ORDER BY q.view_count DESC ");
            case "active" -> sql.append(" ORDER BY q.updated_at DESC ");
            case "voted" -> sql.append(" ORDER BY q.Score DESC ");
            default -> sql.append(" ORDER BY q.created_at DESC ");
        }

        // Phân trang
        sql.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try (Connection con = db.getConnection();
             PreparedStatement st = con.prepareStatement(sql.toString())) {

            int idx = 1;

            // Set tham số cho Search
            if (keyword != null && !keyword.trim().isEmpty()) {
                st.setString(idx++, "%" + keyword + "%");
                st.setString(idx++, "%" + keyword + "%");
            }

            // Set tham số cho Phân trang
            st.setInt(idx++, (pageIndex - 1) * pageSize);
            st.setInt(idx++, pageSize);

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    QuestionDTO q = mapQuestion(rs);
                    q.setAuthorAvatar(rs.getString("avatar_url"));
                    q.setAnswerCount(rs.getInt("ans_count"));
                    q.setTags(getTagsByQuestionId(q.getQuestionId()));
                    list.add(q);
                }
            }
        }

        return list;
    }

    // Đếm tổng số câu hỏi (dùng cho phân trang)
    public int getTotalQuestions(String keyword, String filterType) throws Exception {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Questions q WHERE 1=1 ");

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (q.title LIKE ? OR q.body LIKE ?) ");
        }

        if ("unanswered".equals(filterType)) {
            sql.append(" AND (SELECT COUNT(*) FROM Answers a WHERE a.question_id = q.question_id) = 0 ");
        }

        try (Connection con = db.getConnection();
             PreparedStatement st = con.prepareStatement(sql.toString())) {

            int idx = 1;

            if (keyword != null && !keyword.trim().isEmpty()) {
                st.setString(idx++, "%" + keyword + "%");
                st.setString(idx++, "%" + keyword + "%");
            }

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    // Tìm kiếm câu hỏi (rút gọn)
    public List<QuestionDTO> searchQuestions(String keyword) throws Exception {
        return getQuestions(1, 20, "newest", keyword, "all");
    }

    // Lấy danh sách Tags của một câu hỏi
    private List<String> getTagsByQuestionId(long questionId) throws Exception {
        List<String> tags = new ArrayList<>();
        String sql = "SELECT t.tag_name FROM Tags t " +
                "JOIN Question_Tags qt ON t.tag_id = qt.tag_id " +
                "WHERE qt.question_id = ?";

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, questionId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tags.add(rs.getString("tag_name"));
                }
            }
        }
        return tags;
    }

    // Tăng lượt xem
    public void incrementViewCount(long questionId) throws Exception {
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE Questions SET view_count = view_count + 1 WHERE question_id = ?")) {
            ps.setLong(1, questionId);
            ps.executeUpdate();
        }
    }

    // Map dữ liệu từ ResultSet sang QuestionDTO
    private QuestionDTO mapQuestion(ResultSet rs) throws SQLException {
        QuestionDTO q = new QuestionDTO();
        q.setQuestionId(rs.getLong("question_id"));
        q.setUserId(rs.getLong("user_id"));
        q.setTitle(rs.getString("title"));
        q.setBody(rs.getString("body"));
        q.setViewCount(rs.getInt("view_count"));
        q.setScore(rs.getInt("Score"));
        q.setCreatedAt(rs.getTimestamp("created_at"));
        q.setAuthorName(rs.getString("username"));
        return q;
    }
}

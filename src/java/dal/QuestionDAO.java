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

    // 1. Hàm chính lấy danh sách câu hỏi (Search, Filter, Sort, Paging)
    public List<QuestionDTO> getQuestions(int pageIndex, int pageSize, String sortBy, String keyword, String filterType) {
        List<QuestionDTO> list = new ArrayList<>();
        
        // Dùng StringBuilder để nối chuỗi tối ưu hơn
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT q.*, u.username, up.avatar_url, ")
           .append("(SELECT COUNT(*) FROM Answers a WHERE a.question_id = q.question_id) as ans_count ")
           .append("FROM Questions q ")
           .append("JOIN Users u ON q.user_id = u.user_id ")
           .append("LEFT JOIN User_Profile up ON u.user_id = up.user_id ")
           .append("WHERE 1=1 ");

        // Xử lý từ khóa tìm kiếm (Search)
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (q.title LIKE ? OR q.body LIKE ?) ");
    private final DBContext db = new DBContext();

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
                if (rs.next()) questionId = rs.getLong(1);
            }
        }

        if (questionId > 0 && tagsStr != null && !tagsStr.trim().isEmpty()) {
            addTagsToQuestion(questionId, tagsStr);
        }

        return questionId;
    }

    private void addTagsToQuestion(long questionId, String tagsStr) throws Exception {
        String[] tagNames = tagsStr.split(",");

        try (Connection con = db.getConnection()) {
            for (String tagName : tagNames) {
                tagName = tagName.trim();
                if (tagName.isEmpty()) continue;

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

        // Xử lý bộ lọc (Filter)
        if ("unanswered".equals(filterType)) {
            sql.append(" AND (SELECT COUNT(*) FROM Answers a WHERE a.question_id = q.question_id) = 0 ");
        }

        // Xử lý sắp xếp (Sorting) - Đã thêm case 'voted'
        if ("views".equals(sortBy)) {
            sql.append(" ORDER BY q.view_count DESC ");
        } else if ("active".equals(sortBy)) {
            sql.append(" ORDER BY q.updated_at DESC "); 
        } else if ("voted".equals(sortBy)) {
            sql.append(" ORDER BY q.Score DESC "); // Sắp xếp điểm cao nhất
        } else {
            sql.append(" ORDER BY q.created_at DESC "); // Mặc định là mới nhất
    private long getOrCreateTag(Connection con, String tagName) throws Exception {
        String select = "SELECT tag_id FROM Tags WHERE tag_name = ?";

        try (PreparedStatement ps = con.prepareStatement(select)) {
            ps.setString(1, tagName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        }

        String insert = "INSERT INTO Tags (tag_name) VALUES (?)";

        try (PreparedStatement ps = con.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, tagName);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }

        return -1;
    }

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

    public List<QuestionDTO> getQuestions(int pageIndex, int pageSize, String sortBy, String keyword, String filterType) throws Exception {

        List<QuestionDTO> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT q.*, u.username, up.avatar_url, " +
                        "(SELECT COUNT(*) FROM Answers a WHERE a.question_id = q.question_id) ans_count " +
                        "FROM Questions q " +
                        "JOIN Users u ON q.user_id = u.user_id " +
                        "LEFT JOIN User_Profile up ON u.user_id = up.user_id WHERE 1=1 ");

        if (keyword != null && !keyword.trim().isEmpty())
            sql.append(" AND (q.title LIKE ? OR q.body LIKE ?) ");

        if ("unanswered".equals(filterType))
            sql.append(" AND (SELECT COUNT(*) FROM Answers a WHERE a.question_id = q.question_id)=0 ");

        // Phân trang
        sql.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try {
            Connection conn = getConnection();
            PreparedStatement st = conn.prepareStatement(sql.toString());
            int paramIndex = 1;
        if ("views".equals(sortBy))
            sql.append(" ORDER BY q.view_count DESC ");
        else if ("active".equals(sortBy))
            sql.append(" ORDER BY q.updated_at DESC ");
        else
            sql.append(" ORDER BY q.created_at DESC ");

        sql.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        System.out.println("DEBUG: QuestionDAO.getQuestions() SQL: " + sql.toString());
        System.out.println("DEBUG: pageIndex=" + pageIndex + ", pageSize=" + pageSize + ", sortBy=" + sortBy);

        try (Connection con = db.getConnection();
             PreparedStatement st = con.prepareStatement(sql.toString())) {

            int idx = 1;

            // Set tham số cho Search
            if (keyword != null && !keyword.trim().isEmpty()) {
                st.setString(idx++, "%" + keyword + "%");
                st.setString(idx++, "%" + keyword + "%");
            }

            // Set tham số cho Phân trang
            st.setInt(paramIndex++, (pageIndex - 1) * pageSize);
            st.setInt(paramIndex++, pageSize);

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                QuestionDTO q = mapRow(rs); 
                list.add(q);
            st.setInt(idx++, (pageIndex - 1) * pageSize);
            st.setInt(idx++, pageSize);

            try (ResultSet rs = st.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    QuestionDTO q = mapQuestion(rs);
                    q.setAuthorAvatar(rs.getString("avatar_url"));
                    q.setAnswerCount(rs.getInt("ans_count"));
                    list.add(q);
                    count++;
                    System.out.println("DEBUG: Loaded question " + count + " - ID: " + q.getQuestionId() + ", Title: " + q.getTitle());
                }
                System.out.println("DEBUG: Total questions loaded: " + count);
            }
        }

        return list;
    }
    
    // 2. Hàm hỗ trợ Map dữ liệu từ ResultSet sang Object (Giúp code gọn hơn)
    private QuestionDTO mapRow(ResultSet rs) throws SQLException {
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
        
        // Tự động lấy Tags cho câu hỏi này luôn
        q.setTags(getTagsByQuestionId(q.getQuestionId())); 
        
        return q;
    }

    // 3. Hàm đếm tổng số câu hỏi (Dùng cho phân trang)
    public int getTotalQuestions(String keyword, String filterType) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Questions q WHERE 1=1 ");
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (q.title LIKE ? OR q.body LIKE ?) ");
        }
        if ("unanswered".equals(filterType)) {
             sql.append(" AND (SELECT COUNT(*) FROM Answers a WHERE a.question_id = q.question_id) = 0 ");
        }

        try {
            Connection conn = getConnection();
            PreparedStatement st = conn.prepareStatement(sql.toString());
            int paramIndex = 1;
            
    public int getTotalQuestions(String keyword, String filterType) throws Exception {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Questions q WHERE 1=1 ");

        if (keyword != null && !keyword.trim().isEmpty())
            sql.append(" AND (q.title LIKE ? OR q.body LIKE ?) ");

        if ("unanswered".equals(filterType))
            sql.append(" AND (SELECT COUNT(*) FROM Answers a WHERE a.question_id = q.question_id)=0 ");

        try (Connection con = db.getConnection();
             PreparedStatement st = con.prepareStatement(sql.toString())) {

            int idx = 1;

            if (keyword != null && !keyword.trim().isEmpty()) {
                st.setString(idx++, "%" + keyword + "%");
                st.setString(idx++, "%" + keyword + "%");
            }

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }
    
    // 4. Hàm Search rút gọn (Optional)
    public List<QuestionDTO> searchQuestions(String keyword) {
        return getQuestions(1, 20, "newest", keyword, "all");
    }
    
    // 5. Hàm lấy danh sách Tags của 1 câu hỏi
    public List<String> getTagsByQuestionId(long questionId) {
        List<String> tags = new ArrayList<>();
        String sql = "SELECT t.tag_name FROM Tags t " +
                     "JOIN Question_Tags qt ON t.tag_id = qt.tag_id " +
                     "WHERE qt.question_id = ?";
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
}

    public void incrementViewCount(long questionId) throws Exception {
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE Questions SET view_count = view_count + 1 WHERE question_id = ?")) {
            ps.setLong(1, questionId);
            ps.executeUpdate();
        }
    }

    private QuestionDTO mapQuestion(ResultSet rs) throws Exception {
        QuestionDTO q = new QuestionDTO();
        q.setQuestionId(rs.getLong("question_id"));
        q.setUserId(rs.getLong("user_id"));
        q.setTitle(rs.getString("title"));
        q.setBody(rs.getString("body"));
        q.setViewCount(rs.getInt("view_count"));
        q.setScore(rs.getInt("Score"));
        q.setAuthorName(rs.getString("username"));
        return q;
    }

    private List<String> getTagsByQuestionId(long questionId) throws Exception {
        List<String> tags = new ArrayList<>();

        String sql = "SELECT t.tag_name FROM Tags t " +
                "JOIN Question_Tags qt ON t.tag_id = qt.tag_id WHERE qt.question_id=?";

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, questionId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) tags.add(rs.getString(1));
            }
        }
        return tags;
    }
}

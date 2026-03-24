package dal;

import config.DBContext;
import dto.QuestionDTO;
import dto.UserDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import model.GithubUser;
import model.GoogleUser;
import model.User;
import util.PasswordUtil;
public class UserDAO {

    private final DBContext db = new DBContext();

    public UserDAO() {
        ensureUsersStatusColumn();
    }

    private void ensureUsersStatusColumn() {
        String checkSql = "SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'Users' AND COLUMN_NAME = 'status'";
        String alterSql = "ALTER TABLE Users ADD status VARCHAR(20) NOT NULL CONSTRAINT DF_Users_status DEFAULT 'active'";

        try (Connection con = db.getConnection();
             PreparedStatement check = con.prepareStatement(checkSql);
             ResultSet rs = check.executeQuery()) {
            if (!rs.next()) {
                try (Statement st = con.createStatement()) {
                    st.executeUpdate(alterSql);
                }
            }
        } catch (Exception e) {
            // Keep app booting even when schema auto-fix cannot run
        }
    }

    public boolean emailExists(String email) throws Exception {
        String sql = "SELECT 1 FROM Users WHERE email = ?";
        try (Connection con = db.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean usernameExists(String username) throws Exception {
        String sql = "SELECT 1 FROM Users WHERE username = ?";
        try (Connection con = db.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void register(String username, String email, String rawPassword) throws Exception {
        String sql = "INSERT INTO Users(username, email, password_hash, role, status) VALUES (?, ?, ?, ?, 'active')";
        String hash = PasswordUtil.sha256(rawPassword);

        try (Connection con = db.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, hash);
            ps.setString(4, "member");
            ps.executeUpdate();
        }
    }

    public User loginModel(String email, String rawPassword) throws Exception {
        String sql = "SELECT u.*, p.avatar_url FROM Users u LEFT JOIN User_Profile p ON u.user_id = p.user_id "
                   + "WHERE u.email = ? AND u.password_hash = ?";
        String hash = PasswordUtil.sha256(rawPassword);

        try (Connection con = db.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, hash);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getLong("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    user.setRole(rs.getString("role"));
                    user.setReputation(rs.getInt("Reputation"));
                    user.setAvatarUrl(rs.getString("avatar_url"));
                    return user;
                }
            }
        }
        return null;
    }

    public UserDTO login(String email, String rawPassword) throws Exception {
        String sql = "SELECT u.*, p.avatar_url FROM Users u LEFT JOIN User_Profile p ON u.user_id = p.user_id "
                   + "WHERE u.email = ? AND u.password_hash = ?";
        String hash = PasswordUtil.sha256(rawPassword);

        try (Connection con = db.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, hash);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    UserDTO user = new UserDTO(
                            rs.getLong("user_id"),
                            rs.getString("username"),
                            rs.getString("email"),
                            rs.getString("role")
                    );
                    user.setReputation(rs.getInt("Reputation"));
                    user.setAvatarUrl(rs.getString("avatar_url"));

                    // Thử lấy status, nếu lỗi (không có cột) thì mặc định là active
                    try {
                        user.setStatus(rs.getString("status"));
                    } catch (Exception e) {
                        user.setStatus("active");
                    }
                    
                    return user;
                }
            }
        }
        return null;
    }

    public User loginWithGoogle(GoogleUser gUser) {
        return loginOrRegister(gUser.id, gUser.email, gUser.name, "google");
    }

    public User loginWithGithub(GithubUser gitUser) {
        String displayName = (gitUser.name != null) ? gitUser.name : gitUser.login;
        return loginOrRegister(String.valueOf(gitUser.id), gitUser.email, displayName, "github");
    }

    private User loginOrRegister(String providerId, String email, String name, String providerType) {
        String sqlCheck = "SELECT * FROM Users WHERE email = ?";

        try (Connection con = db.getConnection(); PreparedStatement st = con.prepareStatement(sqlCheck)) {

            st.setString(1, email);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
User u = new User();
                    u.setUserId(rs.getLong("user_id"));
                    u.setUsername(rs.getString("username"));
                    u.setEmail(rs.getString("email"));
                    u.setRole(rs.getString("role"));
                    u.setReputation(rs.getInt("Reputation"));
                    return u;
                } else {
                    return createNewUser(providerId, email, name, providerType);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private User createNewUser(String providerId, String email, String name, String providerType) {
        String sql = "INSERT INTO Users (username, email, password_hash, role, provider, provider_id) VALUES (?, ?, ?, 'member', ?, ?)";

        try (Connection con = db.getConnection(); PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            String safeName = (name != null ? name : "User").replaceAll("\\s+", "") + "_" + (int) (Math.random() * 10000);
            if (safeName.length() > 50) {
                safeName = safeName.substring(0, 50);
            }

            ps.setString(1, safeName);
            ps.setString(2, email);
            ps.setString(3, UUID.randomUUID().toString());
            ps.setString(4, providerType);
            ps.setString(5, providerId);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    User newUser = new User();
                    newUser.setUserId(rs.getLong(1));
                    newUser.setUsername(safeName);
                    newUser.setEmail(email);
                    newUser.setRole("member");
                    newUser.setReputation(0);
                    return newUser;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    // Lấy thông tin chi tiết cho trang Profile
    public UserDTO getUserProfileById(long id) {
        UserDTO user = null;
        // Query join 2 bảng Users và User_Profile
        String sql = "SELECT u.user_id, u.username, u.email, u.role, u.Reputation, u.created_at, "
                + "p.bio, p.location, p.website, p.avatar_url "
                + "FROM Users u "
                + "LEFT JOIN User_Profile p ON u.user_id = p.user_id "
                + "WHERE u.user_id = ?";

        try (Connection con = db.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user = new UserDTO();
                    // Map dữ liệu từ DB vào DTO
                    user.setUserId(rs.getLong("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
user.setRole(rs.getString("role"));
                    user.setReputation(rs.getInt("Reputation"));
                    user.setCreatedAt(rs.getTimestamp("created_at"));

                    // Các trường từ bảng Profile (có thể null)
                    user.setBio(rs.getString("bio"));
                    user.setLocation(rs.getString("location"));
                    user.setWebsite(rs.getString("website"));
                    user.setAvatarUrl(rs.getString("avatar_url"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    // ==================== ADMIN USER MANAGEMENT ====================
    // Lấy tổng số users
    public int getUserCount() {
        String sql = "SELECT COUNT(*) FROM Users";
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Lấy danh sách users với pagination
    public List<UserDTO> getAllUsers(int page, int pageSize) {
        List<UserDTO> users = new ArrayList<>();
        String sql = "SELECT user_id, username, email, role, status, created_at, Reputation "
                   + "FROM Users ORDER BY created_at DESC "
                   + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, (page - 1) * pageSize);
            ps.setInt(2, pageSize);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UserDTO user = new UserDTO();
                    user.setUserId(rs.getLong("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    user.setRole(rs.getString("role"));
                    user.setStatus(rs.getString("status"));
                    user.setCreatedAt(rs.getTimestamp("created_at"));
                    user.setReputation(rs.getInt("Reputation"));
                    users.add(user);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    // Tìm kiếm users
    public List<UserDTO> searchUsers(String keyword, int limit) {
        List<UserDTO> users = new ArrayList<>();
        String sql = "SELECT TOP (?) user_id, username, email, role, status, created_at "
                   + "FROM Users WHERE username LIKE ? OR email LIKE ?";

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setString(2, "%" + keyword + "%");
ps.setString(3, "%" + keyword + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UserDTO user = new UserDTO();
                    user.setUserId(rs.getLong("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    user.setRole(rs.getString("role"));
                    user.setStatus(rs.getString("status"));
                    user.setCreatedAt(rs.getTimestamp("created_at"));
                    users.add(user);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    // Lấy user theo ID (cho edit form)
    public UserDTO getUserById(long userId) {
        String sql = "SELECT user_id, username, email, role, status, created_at, Reputation FROM Users WHERE user_id = ?";

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    UserDTO user = new UserDTO();
                    user.setUserId(rs.getLong("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    user.setRole(rs.getString("role"));
                    user.setStatus(rs.getString("status"));
                    user.setCreatedAt(rs.getTimestamp("created_at"));
                    user.setReputation(rs.getInt("Reputation"));
                    return user;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Tạo user mới (admin)
    public boolean createUser(String username, String email, String rawPassword, String role) {
        String sql = "INSERT INTO Users(username, email, password_hash, role, status) VALUES (?, ?, ?, ?, 'active')";
        String hash = PasswordUtil.sha256(rawPassword);

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, hash);
            ps.setString(4, role);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Update user (role, status)
    public boolean updateUser(long userId, String role, String status) {
        String sql = "UPDATE Users SET role = ?, status = ?, updated_at = GETDATE() WHERE user_id = ?";

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, role);
            ps.setString(2, status);
            ps.setLong(3, userId);
return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Toggle status (active <-> inactive)
    public boolean toggleUserStatus(long userId) {
        String sql = "UPDATE Users SET status = CASE WHEN status = 'active' THEN 'inactive' ELSE 'active' END, "
                   + "updated_at = GETDATE() WHERE user_id = ?";

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Đếm users với filter role và status
    public int getUserCountByFilter(String role, String status) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Users WHERE 1=1");
        List<String> params = new ArrayList<>();

        if (role != null && !role.isEmpty()) {
            sql.append(" AND role = ?");
            params.add(role);
        }
        if (status != null && !status.isEmpty()) {
            sql.append(" AND status = ?");
            params.add(status);
        }

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setString(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Lấy users với pagination + filter
    public List<UserDTO> getUsersByFilter(String role, String status, int page, int pageSize) {
        List<UserDTO> users = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT user_id, username, email, role, status, created_at, Reputation FROM Users WHERE 1=1"
        );
        List<Object> params = new ArrayList<>();

        if (role != null && !role.isEmpty()) {
            sql.append(" AND role = ?");
            params.add(role);
        }
        if (status != null && !status.isEmpty()) {
            sql.append(" AND status = ?");
            params.add(status);
        }
        sql.append(" ORDER BY created_at DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add((page - 1) * pageSize);
        params.add(pageSize);

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof String) {
                    ps.setString(i + 1, (String) param);
                } else {
                    ps.setInt(i + 1, (Integer) param);
                }
            }
try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UserDTO user = new UserDTO();
                    user.setUserId(rs.getLong("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    user.setRole(rs.getString("role"));
                    user.setStatus(rs.getString("status"));
                    user.setCreatedAt(rs.getTimestamp("created_at"));
                    user.setReputation(rs.getInt("Reputation"));
                    users.add(user);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    // Lấy users mới nhất (cho dashboard)
    public List<UserDTO> getNewestUsers(int limit) {
        List<UserDTO> users = new ArrayList<>();
        String sql = "SELECT TOP (?) user_id, username, email, role, status, created_at "
                   + "FROM Users ORDER BY created_at DESC";

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UserDTO user = new UserDTO();
                    user.setUserId(rs.getLong("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    user.setRole(rs.getString("role"));
                    user.setStatus(rs.getString("status"));
                    user.setCreatedAt(rs.getTimestamp("created_at"));
                    users.add(user);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    // Lấy tổng số questions (cho dashboard)
    public int getQuestionCount() {
        String sql = "SELECT COUNT(*) FROM Questions";
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Lấy tổng số answers (cho dashboard)
    public int getAnswerCount() {
        String sql = "SELECT COUNT(*) FROM Answers";
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Lấy số câu hỏi trong tháng hiện tại theo tag (cho dashboard widget)
    public List<Map<String, Object>> getCurrentMonthQuestionCountByTag(int limit) {
        List<Map<String, Object>> stats = new ArrayList<>();
        String sql = "SELECT TOP (?) t.tag_name AS tag_name, COUNT(*) AS question_count "
                + "FROM Questions q "
                + "JOIN Question_Tags qt ON q.question_id = qt.question_id "
                + "JOIN Tags t ON qt.tag_id = t.tag_id "
                + "WHERE YEAR(q.created_at) = YEAR(GETDATE()) AND MONTH(q.created_at) = MONTH(GETDATE()) "
                + "GROUP BY t.tag_name "
                + "ORDER BY question_count DESC, t.tag_name ASC";

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, Math.max(1, limit));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("tagName", rs.getString("tag_name"));
                    item.put("questionCount", rs.getInt("question_count"));
                    stats.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return stats;
    }

    // Lấy xu hướng đăng ký user theo ngày (cho dashboard chart)
    public List<Map<String, Object>> getUserRegistrationTrend(int days) {
List<Map<String, Object>> trend = new ArrayList<>();
        String sql = "SELECT CAST(created_at AS DATE) as reg_date, COUNT(*) as count " +
                     "FROM Users WHERE created_at >= DATEADD(DAY, -?, GETDATE()) " +
                     "GROUP BY CAST(created_at AS DATE) ORDER BY reg_date";

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, days);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("date", rs.getDate("reg_date"));
                    item.put("count", rs.getInt("count"));
                    trend.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return trend;
    }

    // Lấy xu hướng câu hỏi mới theo ngày (cho dashboard chart)
    public List<Map<String, Object>> getQuestionTrend(int days) {
        List<Map<String, Object>> trend = new ArrayList<>();
        String sql = "SELECT CAST(created_at AS DATE) as q_date, COUNT(*) as count " +
                     "FROM Questions WHERE created_at >= DATEADD(DAY, -?, GETDATE()) " +
                     "GROUP BY CAST(created_at AS DATE) ORDER BY q_date";

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, days);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("date", rs.getDate("q_date"));
                    item.put("count", rs.getInt("count"));
                    trend.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return trend;
    }
    public List<String> getReputationChanges(long userId, int limit) {
        List<String> changes = new ArrayList<>();
        String sql = "SELECT TOP (?) delta, reason FROM Reputation_History WHERE user_id = ? ORDER BY created_at DESC, history_id DESC";

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, Math.max(1, limit));
            ps.setLong(2, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int delta = rs.getInt("delta");
                    String reason = rs.getString("reason");
                    String formatted = (delta >= 0 ? "+" : "") + delta + " reputation"
                            + (reason != null && !reason.trim().isEmpty() ? " (" + reason + ")" : "");
                    changes.add(formatted);
                }
            }
        } catch (Exception e) {
            // If the history table is not deployed yet, return empty list to keep profile functional.
}

        return changes;
    }
    public void changPassword(String email, String newPassword) throws Exception{
        String sql = "UPDATE Users SET password_hash = ? WHERE email = ?";
        String hash = PasswordUtil.sha256(newPassword);
        try(Connection con = db.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
            ps.setString(1, hash);
            ps.setString(2, email);
            ps.executeUpdate();
        }
    }
    //==================== USER FOR USER===========================
    public List<UserDTO> getTopUsers() {
    List<UserDTO> list = new ArrayList<>();
    String sql = "SELECT TOP 10 u.user_id, u.username, u.Reputation, "
               + "u.created_at, p.avatar_url "
               + "FROM Users u "
               + "LEFT JOIN User_Profile p ON u.user_id = p.user_id "
               + "WHERE u.role != 'admin' AND u.role != 'bot' "
               + "ORDER BY u.Reputation DESC";
    try (Connection conn = db.getConnection();
         PreparedStatement st = conn.prepareStatement(sql);
         ResultSet rs = st.executeQuery()) {
        while (rs.next()) {
            UserDTO user = new UserDTO();
            user.setUserId(rs.getLong("user_id"));
            user.setUsername(rs.getString("username"));
            user.setReputation(rs.getInt("Reputation"));
            user.setCreatedAt(rs.getTimestamp("created_at"));
            user.setAvatarUrl(rs.getString("avatar_url"));
            list.add(user);
        }
    } catch (Exception e) {
        e.printStackTrace();
        System.out.println("getTopUsers LỖI: " + e.getMessage());
    }
    return list;
}

public List<UserDTO> getAllUsers(String keyword, String sort) {
    List<UserDTO> list = new ArrayList<>();
    StringBuilder sql = new StringBuilder(
        "SELECT u.user_id, u.username, u.Reputation, "
      + "u.created_at, p.avatar_url "
      + "FROM Users u "
      + "LEFT JOIN User_Profile p ON u.user_id = p.user_id "
      + "WHERE u.role != 'admin' AND u.role != 'bot' "
    );
    if (keyword != null && !keyword.trim().isEmpty()) {
        sql.append("AND u.username LIKE ? ");
    }
    if ("date".equals(sort)) {
        sql.append("ORDER BY u.created_at DESC");
    } else if ("reputation".equals(sort)) {
        sql.append("ORDER BY u.Reputation DESC");
    } else {
        sql.append("ORDER BY u.username ASC");
    }
    try (Connection conn = db.getConnection();
         PreparedStatement st = conn.prepareStatement(sql.toString())) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            st.setString(1, "%" + keyword.trim() + "%");
        }
        try (ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                UserDTO user = new UserDTO();
                user.setUserId(rs.getLong("user_id"));
                user.setUsername(rs.getString("username"));
                user.setReputation(rs.getInt("Reputation"));
                user.setCreatedAt(rs.getTimestamp("created_at"));
                user.setAvatarUrl(rs.getString("avatar_url"));
                list.add(user);
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
        System.out.println("getAllUsers LỖI: " + e.getMessage());
    }
    return list;
}
// ===== Check đã follow chưa =====
public boolean isFollowing(long followerId, long followingId) {
    String sql = "SELECT COUNT(*) FROM UserFollow "
               + "WHERE follower_id = ? AND following_id = ?";
    try {
        Connection conn = db.getConnection();
        PreparedStatement st = conn.prepareStatement(sql);
        st.setLong(1, followerId);
        st.setLong(2, followingId);
        ResultSet rs = st.executeQuery();
        if (rs.next()) return rs.getInt(1) > 0;
    } catch (Exception e) {
        e.printStackTrace();
        System.out.println("isFollowing LỖI: " + e.getMessage());
    }
    return false;
}

// ===== Follow user =====
public void followUser(long followerId, long followingId) {
    if (isFollowing(followerId, followingId)) return;
    String sql = "INSERT INTO UserFollow (follower_id, following_id, followed_at) "
               + "VALUES (?, ?, GETDATE())";
    try {
        Connection conn = db.getConnection();
        PreparedStatement st = conn.prepareStatement(sql);
        st.setLong(1, followerId);
        st.setLong(2, followingId);
        st.executeUpdate();
    } catch (Exception e) {
        e.printStackTrace();
        System.out.println("followUser LỖI: " + e.getMessage());
    }
}

// ===== Unfollow user =====
public void unfollowUser(long followerId, long followingId) {
    String sql = "DELETE FROM UserFollow "
               + "WHERE follower_id = ? AND following_id = ?";
    try {
        Connection conn = db.getConnection();
        PreparedStatement st = conn.prepareStatement(sql);
        st.setLong(1, followerId);
        st.setLong(2, followingId);
        st.executeUpdate();
    } catch (Exception e) {
        e.printStackTrace();
        System.out.println("unfollowUser LỖI: " + e.getMessage());
    }
}

// ===== Danh sách TÔI đang follow =====
public List<UserDTO> getFollowingList(long userId) {
    List<UserDTO> list = new ArrayList<>();
    String sql = "SELECT u.user_id, u.username, u.Reputation, "
               + "u.created_at, p.avatar_url "
               + "FROM UserFollow uf "
               + "JOIN Users u ON uf.following_id = u.user_id "
               + "LEFT JOIN User_Profile p ON u.user_id = p.user_id "
               + "WHERE uf.follower_id = ? "
               + "ORDER BY uf.followed_at DESC";
    try {
        Connection conn = db.getConnection();
        PreparedStatement st = conn.prepareStatement(sql);
        st.setLong(1, userId);
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            UserDTO u = new UserDTO();
            u.setUserId(rs.getLong("user_id"));
            u.setUsername(rs.getString("username"));
            u.setReputation(rs.getInt("Reputation"));
            u.setCreatedAt(rs.getTimestamp("created_at"));
            u.setAvatarUrl(rs.getString("avatar_url"));
            list.add(u);
        }
    } catch (Exception e) {
        e.printStackTrace();
        System.out.println("getFollowingList LỖI: " + e.getMessage());
    }
    return list;
}

// ===== Câu hỏi của TARGET user =====
public List<QuestionDTO> getQuestionsByUser(long userId, String filter, int page) {
    List<QuestionDTO> list = new ArrayList<>();
    int pageSize = 10;
    int offset   = (page - 1) * pageSize;

    StringBuilder sql = new StringBuilder(
        "SELECT q.question_id, q.title, q.body, q.Score, "
      + "q.view_count, q.created_at, q.is_closed, "
      + "COUNT(a.answer_id) AS answer_count "
      + "FROM Questions q "
      + "LEFT JOIN Answers a ON q.question_id = a.question_id "
      + "WHERE q.user_id = ? "
      + "GROUP BY q.question_id, q.title, q.body, q.Score, "
      + "q.view_count, q.created_at, q.is_closed "
    );

    if ("newest".equals(filter)) {
        sql.append("ORDER BY q.created_at DESC ");
    } else if ("name".equals(filter)) {
        sql.append("ORDER BY q.title ASC ");
    } else {
        // popular = default
        sql.append("ORDER BY q.Score DESC ");
    }

    sql.append("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

    try {
        Connection conn = db.getConnection();
        PreparedStatement st = conn.prepareStatement(sql.toString());
        st.setLong(1, userId);
        st.setInt(2, offset);
        st.setInt(3, pageSize);
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            QuestionDTO q = new QuestionDTO();
            q.setQuestionId(rs.getLong("question_id"));
            q.setTitle(rs.getString("title"));
            q.setBody(rs.getString("body"));
            q.setScore(rs.getInt("Score"));
            q.setViewCount(rs.getInt("view_count"));
            q.setCreatedAt(rs.getTimestamp("created_at"));
            q.setIsClosed(rs.getBoolean("is_closed"));
            q.setAnswerCount(rs.getInt("answer_count"));
            list.add(q);
        }
    } catch (Exception e) {
        e.printStackTrace();
        System.out.println("getQuestionsByUser LỖI: " + e.getMessage());
    }
    return list;
}

// ===== Đếm câu hỏi của TARGET =====
public int countQuestionsByUser(long userId) {
    String sql = "SELECT COUNT(*) FROM Questions WHERE user_id = ?";
    try {
        Connection conn = db.getConnection();
        PreparedStatement st = conn.prepareStatement(sql);
        st.setLong(1, userId);
        ResultSet rs = st.executeQuery();
        if (rs.next()) return rs.getInt(1);
    } catch (Exception e) {
        e.printStackTrace();
        System.out.println("countQuestionsByUser LỖI: " + e.getMessage());
    }
    return 0;
}
    
}

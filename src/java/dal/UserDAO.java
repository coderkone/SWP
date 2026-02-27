package dal;

import config.DBContext;
import dto.UserDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import util.PasswordUtil;
import model.GithubUser;
import model.GoogleUser;
import model.User;
import java.util.UUID;
public class UserDAO {

    private final DBContext db = new DBContext();

    public boolean emailExists(String email) throws Exception {
        String sql = "SELECT 1 FROM Users WHERE email = ?";
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean usernameExists(String username) throws Exception {
        String sql = "SELECT 1 FROM Users WHERE username = ?";
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void register(String username, String email, String rawPassword) throws Exception {
        String sql = "INSERT INTO Users(username, email, password_hash, role) VALUES (?, ?, ?, ?)";
        String hash = PasswordUtil.sha256(rawPassword);

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, hash);
            ps.setString(4, "member"); // mặc định
            ps.executeUpdate();
        }
    }

    public UserDTO login(String email, String rawPassword) throws Exception {
        String sql = "SELECT user_id, username, email, role, status FROM Users WHERE email = ? AND password_hash = ?";
        String hash = PasswordUtil.sha256(rawPassword);

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
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
                    user.setStatus(rs.getString("status"));
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
        
        try (Connection con = db.getConnection(); 
             PreparedStatement st = con.prepareStatement(sqlCheck)) {
             
            st.setString(1, email);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    u.setUserId(rs.getLong("user_id"));
                    u.setUsername(rs.getString("username"));
                    u.setEmail(rs.getString("email"));
                    u.setRole(rs.getString("role"));
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
        
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            String safeName = (name != null ? name : "User").replaceAll("\\s+", "") + "_" + (int)(Math.random() * 10000);
            if (safeName.length() > 50) safeName = safeName.substring(0, 50);

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

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

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
}

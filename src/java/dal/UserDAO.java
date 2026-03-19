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
        String sql = "INSERT INTO Users(username, email, password_hash, role) VALUES (?, ?, ?, ?)";
        String hash = PasswordUtil.sha256(rawPassword);

        try (Connection con = db.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, hash);
            ps.setString(4, "member"); // mặc định
            ps.executeUpdate();
        }
    }

    public UserDTO login(String email, String rawPassword) throws Exception {
        String sql = "SELECT user_id, username, email, role, Reputation FROM Users WHERE email = ? AND password_hash = ?";
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
}

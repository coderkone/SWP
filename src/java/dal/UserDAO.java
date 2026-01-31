package dal;

import config.DBContext;
import dto.UserDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
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
        String sql = "SELECT user_id, username, email, role FROM Users WHERE email = ? AND password_hash = ?";
        String hash = PasswordUtil.sha256(rawPassword);

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, hash);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new UserDTO(
                            rs.getLong("user_id"),
                            rs.getString("username"),
                            rs.getString("email"),
                            rs.getString("role")
                    );
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
    public void changPassword(String email, String newPassword) throws Exception{
        String sql = "UPDATE Users SET password_hash = ? WHERE email = ?";
        String hash = PasswordUtil.sha256(newPassword);
        try(Connection con = db.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
            ps.setString(1, email);
            ps.setString(2, hash);
            ps.executeUpdate();
        }
    }
}

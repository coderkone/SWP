package dal;

import config.DBContext;
import dto.UserDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import util.PasswordUtil;

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

    public UserDTO getUserById(long userId) throws Exception {
        String sql = "SELECT user_id, username, email, role FROM Users WHERE user_id = ?";
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);

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

    public List<Map<String, Object>> getPopularTagsByUserId(long userId) throws Exception {
        String sql = "SELECT TOP 5 t.tag_id, t.tag_name, COUNT(qt.question_id) as count " +
                     "FROM Tags t " +
                     "INNER JOIN Question_Tags qt ON t.tag_id = qt.tag_id " +
                     "INNER JOIN Questions q ON qt.question_id = q.question_id " +
                     "WHERE q.user_id = ? " +
                     "GROUP BY t.tag_id, t.tag_name " +
                     "ORDER BY count DESC";

        List<Map<String, Object>> tags = new ArrayList<>();
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> tag = new HashMap<>();
                    tag.put("tagId", rs.getLong("tag_id"));
                    tag.put("tagName", rs.getString("tag_name"));
                    tag.put("count", rs.getInt("count"));
                    tags.add(tag);
                }
            }
        }
        return tags;
    }
}

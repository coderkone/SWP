package dal;

import config.DBContext;
import dto.BookmarkDTO; // Import đúng package dto
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Question;

public class BookmarkDAO {

    public List<BookmarkDTO> getBookmarksByUserId(long userId) {
        List<BookmarkDTO> list = new ArrayList<>();
        // Chỉ lấy các trường có trong DTO
        String sql = "SELECT b.*, q.title FROM Bookmarks b "
                + "INNER JOIN Questions q ON b.question_id = q.question_id "
                + "WHERE b.user_id = ? ORDER BY b.created_at DESC";

        try {
            DBContext db = new DBContext();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new BookmarkDTO(
                        rs.getLong("question_id"),
                        rs.getString("title"),
                        rs.getTimestamp("created_at"),
                        rs.getObject("collection_id") != null ? rs.getInt("collection_id") : null,
                        rs.getString("title")
                ));
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void removeBookmark(long userId, int questionId) {
        String sql = "DELETE FROM Bookmarks WHERE user_id = ? AND question_id = ?";
        try {
            DBContext db = new DBContext();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, userId);
            ps.setInt(2, questionId);
            ps.executeUpdate();
            ps.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<BookmarkDTO> getBookmarksByCollection(long userId, int collectionId) {
        List<BookmarkDTO> list = new ArrayList<>();
        String sql = "SELECT b.*, q.title FROM Bookmarks b "
                + "INNER JOIN Questions q ON b.question_id = q.question_id "
                + "WHERE b.user_id = ? AND b.collection_id = ? "
                + "ORDER BY b.created_at DESC";
        try {
            DBContext db = new DBContext();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, userId);
            ps.setInt(2, collectionId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                list.add(new BookmarkDTO(
                        rs.getLong("question_id"),
                        rs.getString("title"), 
                        rs.getTimestamp("created_at"),
                        rs.getObject("collection_id") != null ? rs.getInt("collection_id") : null,
                        rs.getString("title") 
                ));
            }
            rs.close();
            ps.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 1. Hàm lấy TẤT CẢ bài viết đã lưu (Dành cho nút "All saves")
    public List<Question> getAllBookmarks(long userId) {
        List<Question> list = new ArrayList<>();
        // Bỏ qua điều kiện collection_id, chỉ cần đúng user_id là lấy hết
        String sql = "SELECT q.* FROM Questions q "
                + "INNER JOIN Bookmarks b ON q.question_id = b.question_id "
                + "WHERE b.user_id = ? "
                + "ORDER BY b.created_at DESC";
        try {
            DBContext db = new DBContext();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                // Map dữ liệu vào Question entity
            }
            // ... đóng kết nối
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    // 1. Hàm chống trùng lặp: Kiểm tra xem user đã lưu câu hỏi này chưa

    public boolean isBookmarked(long userId, int questionId) {
        String sql = "SELECT 1 FROM Bookmarks WHERE user_id = ? AND question_id = ?";
        try {
            DBContext db = new DBContext();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, userId);
            ps.setInt(2, questionId);
            ResultSet rs = ps.executeQuery();
            boolean exists = rs.next();

            rs.close();
            ps.close();
            conn.close();
            return exists;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

// 2. Hàm di chuyển bài viết (Move)
    public void moveBookmark(long userId, int questionId, String collectionIdStr) {
        String sql = "UPDATE Bookmarks SET collection_id = ? WHERE user_id = ? AND question_id = ?";
        try {
            DBContext db = new DBContext();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            // Nếu chuyển ra "All saves" (thư mục gốc), collection_id sẽ là NULL
            if (collectionIdStr == null || collectionIdStr.trim().isEmpty() || collectionIdStr.equals("0")) {
                ps.setNull(1, java.sql.Types.INTEGER);
            } else {
                ps.setInt(1, Integer.parseInt(collectionIdStr));
            }

            ps.setLong(2, userId);
            ps.setInt(3, questionId);
            ps.executeUpdate();

            ps.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

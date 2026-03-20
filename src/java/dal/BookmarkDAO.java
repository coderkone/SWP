package dal;

import config.DBContext;
import dto.BookmarkDTO; // Import đúng package dto
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Question;

public class BookmarkDAO extends DBContext {

    public List<BookmarkDTO> getBookmarksByUserId(long userId) {
        List<BookmarkDTO> list = new ArrayList<>();
        // Chỉ lấy các trường có trong DTO
        String sql = "SELECT b.*, q.title FROM Bookmarks b "
                + "INNER JOIN Questions q ON b.question_id = q.question_id "
                + "WHERE b.user_id = ? ORDER BY b.created_at DESC";

        try {
            Connection conn = getConnection();
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
            Connection conn = getConnection();
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
            Connection conn = getConnection();
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
            Connection conn = getConnection();
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
            Connection conn = getConnection();
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

    public boolean removeFromCollection(long userId, long questionId) {
        String sql = "UPDATE Bookmarks SET collection_id = NULL WHERE user_id = ? AND question_id = ?";
        try {
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, userId);
            ps.setLong(2, questionId);
            int row = ps.executeUpdate();
            conn.close();
            return row > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
// 2. Hàm di chuyển bài viết (Move)

    public void moveBookmark(long userId, int questionId, String collectionIdStr) {
        String sql = "UPDATE Bookmarks SET collection_id = ? WHERE user_id = ? AND question_id = ?";
        try {
            Connection conn = getConnection();
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

    public int countTotalBookmarks(long userId, String listId) {
        // Sửa lại cho đúng tên bảng Bookmarks
        String sql = "SELECT COUNT(*) FROM Bookmarks WHERE user_id = ?";
        if (listId != null && !listId.isEmpty() && !listId.equals("null")) {
            sql += " AND collection_id = " + listId;
        }
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

// 2. Hàm lấy danh sách Bookmark phân trang
    public List<BookmarkDTO> getBookmarksByPage(long userId, String listId, int page) {
        List<BookmarkDTO> list = new ArrayList<>();
        int offset = (page - 1) * 10;

        // Nối đúng bảng Bookmarks và Questions, sử dụng OFFSET và FETCH
        String sql = "SELECT q.question_id, q.title, b.created_at "
                + "FROM Bookmarks b "
                + "INNER JOIN Questions q ON b.question_id = q.question_id "
                + "WHERE b.user_id = ? ";

        if (listId != null && !listId.isEmpty() && !listId.equals("null")) {
            sql += "AND b.collection_id = ? ";
        }

        sql += "ORDER BY b.created_at DESC OFFSET ? ROWS FETCH NEXT 10 ROWS ONLY";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            int paramIdx = 2;

            if (listId != null && !listId.isEmpty() && !listId.equals("null")) {
                ps.setString(paramIdx++, listId);
            }

            ps.setInt(paramIdx++, offset);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                // Gán dữ liệu vào DTO 
                list.add(new BookmarkDTO(
                        rs.getLong("question_id"),
                        rs.getString("title"),
                        rs.getTimestamp("created_at")
                ));
            }
        } catch (Exception e) {
            System.out.println("Lỗi SQL ở BookmarkDAO: ");
            e.printStackTrace();
        }
        return list;
    }

    // 1. Hàm lấy TẤT CẢ bài viết đã lưu (Dành cho nút "All saves")

    // Kiểm tra user đã bookmark câu hỏi chưa (dùng long để khớp với Controller)
    public boolean checkIfBookmarked(long userId, long questionId) {
        String sql = "SELECT 1 FROM Bookmarks WHERE user_id = ? AND question_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, questionId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

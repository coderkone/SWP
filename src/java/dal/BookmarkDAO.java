package dal;

import config.DBContext;
import dto.BookmarkDTO; // Import đúng package dto
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BookmarkDAO {

    public List<BookmarkDTO> getBookmarksByUserId(long userId) {
        List<BookmarkDTO> list = new ArrayList<>();
        // Chỉ lấy các trường có trong DTO
        String sql = "SELECT b.question_id, q.title, b.created_at, b.collection_id " +
                     "FROM Bookmarks b " +
                     "JOIN Questions q ON b.question_id = q.question_id " +
                     "WHERE b.user_id = ? " +
                     "ORDER BY b.created_at DESC";

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
                    rs.getObject("collection_id") != null ? rs.getInt("collection_id") : null
                ));
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
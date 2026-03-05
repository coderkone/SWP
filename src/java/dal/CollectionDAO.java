package dal;

import config.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Collection;

public class CollectionDAO extends DBContext {

    // 1. Tạo Collection mới
    public void createCollection(long userId, String name) {
        // Dựa trên diagram: bảng Collections có cột user_id, Name, CreatedAt
        String sql = "INSERT INTO Collections (user_id, Name, CreatedAt) VALUES (?, ?, GETDATE())";
        try {
            Connection conn = getConnection();
            PreparedStatement st = conn.prepareStatement(sql);
            st.setLong(1, userId);
            st.setString(2, name);
            st.executeUpdate();
            st.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 2. Lấy danh sách Collection của User (để hiện lên Sidebar)
    public List<Collection> getCollectionsByUserId(long userId) {
        List<Collection> list = new ArrayList<>();
        String sql = "SELECT * FROM Collections WHERE user_id = ? ORDER BY CreatedAt DESC";
        try {
            Connection conn = getConnection();
            PreparedStatement st = conn.prepareStatement(sql);
            st.setLong(1, userId);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Collection c = new Collection();
                c.setCollectionId(rs.getInt("collection_id"));
                c.setUserId(rs.getLong("user_id"));
                c.setName(rs.getString("Name"));
                c.setCreatedAt(rs.getTimestamp("CreatedAt"));
                list.add(c);
            }
            rs.close();
            st.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void deleteCollection(int collectionId, long userId) {
        try {
            Connection conn = getConnection();

            // 1. Xóa các bài viết nằm trong list này trước (để tránh lỗi khóa ngoại)
            String sqlDeleteBookmarks = "DELETE FROM Bookmarks WHERE collection_id = ? AND user_id = ?";
            PreparedStatement st1 = conn.prepareStatement(sqlDeleteBookmarks);
            st1.setInt(1, collectionId);
            st1.setLong(2, userId);
            st1.executeUpdate();
            st1.close();

            // 2. Sau đó mới xóa chính cái List đó
            String sqlDeleteCollection = "DELETE FROM Collections WHERE collection_id = ? AND user_id = ?";
            PreparedStatement st2 = conn.prepareStatement(sqlDeleteCollection);
            st2.setInt(1, collectionId);
            st2.setLong(2, userId); // Thêm userId để chắc chắn không xóa nhầm của người khác
            st2.executeUpdate();
            st2.close();

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

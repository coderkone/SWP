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

    public boolean checkIfBookmarked(long userId, long questionId) {
        String sql = "SELECT COUNT(*) as count FROM Bookmarks WHERE user_id = ? AND question_id = ?";
        
        try {
            DBContext db = new DBContext();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, userId);
            ps.setLong(2, questionId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                boolean bookmarked = rs.getInt("count") > 0;
                conn.close();
                return bookmarked;
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean addBookmark(long userId, long questionId) {
        String sql = "INSERT INTO Bookmarks (user_id, question_id, created_at) VALUES (?, ?, GETDATE())";
        
        try {
            DBContext db = new DBContext();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, userId);
            ps.setLong(2, questionId);
            int result = ps.executeUpdate();
            conn.close();
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean removeBookmark(long userId, long questionId) {
        String sql = "DELETE FROM Bookmarks WHERE user_id = ? AND question_id = ?";
        
        try {
            DBContext db = new DBContext();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, userId);
            ps.setLong(2, questionId);
            int result = ps.executeUpdate();
            conn.close();
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;    }

    // Answer Bookmark Methods
    public boolean checkIfAnswerBookmarked(long userId, long answerId) {
        String sql = "SELECT COUNT(*) as count FROM Answer_Bookmarks WHERE user_id = ? AND answer_id = ?";
        
        try {
            DBContext db = new DBContext();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, userId);
            ps.setLong(2, answerId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                boolean bookmarked = rs.getInt("count") > 0;
                conn.close();
                return bookmarked;
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean addAnswerBookmark(long userId, long answerId) {
        String sql = "INSERT INTO Answer_Bookmarks (user_id, answer_id, created_at) VALUES (?, ?, GETDATE())";
        
        try {
            DBContext db = new DBContext();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, userId);
            ps.setLong(2, answerId);
            int result = ps.executeUpdate();
            conn.close();
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean removeAnswerBookmark(long userId, long answerId) {
        String sql = "DELETE FROM Answer_Bookmarks WHERE user_id = ? AND answer_id = ?";
        
        try {
            DBContext db = new DBContext();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, userId);
            ps.setLong(2, answerId);
            int result = ps.executeUpdate();
            conn.close();
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }    }

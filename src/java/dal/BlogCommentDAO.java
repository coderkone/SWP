package dal;

import config.DBContext;
import model.BlogComment;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Blog;

public class BlogCommentDAO extends DBContext {

    // ==========================================================
    // GET COMMENTS BY BLOG ID (BUILDS THE PARENT-CHILD TREE)
    // ==========================================================
    public List<BlogComment> getCommentTreeByBlogId(int blogId) {
        List<BlogComment> rootComments = new ArrayList<>();
        Map<Integer, BlogComment> commentMap = new HashMap<>();

        // Notice: We only select username, NOT avatar_url, to prevent DB errors.
        // Đổi INNER JOIN thành LEFT JOIN để chống trôi comment khi User bị xóa
        String sql = "SELECT c.*, u.username "
                + "FROM BlogComments c "
                + "LEFT JOIN Users u ON c.user_id = u.user_id "
                + "WHERE c.blog_id = ? "
                + "ORDER BY c.created_at ASC";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, blogId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                BlogComment c = new BlogComment();
                c.setCommentId(rs.getInt("comment_id"));
                c.setBlogId(rs.getInt("blog_id"));
                c.setUserId(rs.getLong("user_id"));

                // Handle parent_id carefully (it can be NULL in database)
                int pId = rs.getInt("parent_id");
                c.setParentId(rs.wasNull() ? null : pId);

                c.setContent(rs.getString("content"));
                c.setCreatedAt(rs.getTimestamp("created_at"));
                c.setUsername(rs.getString("username"));

                // Set a default avatar directly in Java to prevent JSP errors
                c.setUserAvatar("https://cdn-icons-png.flaticon.com/512/149/149071.png");

                // Store in Map for fast lookup
                commentMap.put(c.getCommentId(), c);

                // If it has NO parent, it is a Root Comment
                if (c.getParentId() == null) {
                    rootComments.add(c);
                } // If it HAS a parent, find the parent and add this as a reply
                else {
                    BlogComment parent = commentMap.get(c.getParentId());
                    if (parent != null) {
                        parent.getReplies().add(c);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("===== ERROR IN getCommentTreeByBlogId =====");
            e.printStackTrace();
        }

        return rootComments;
    }

    public boolean insertComment(BlogComment comment) {
        // parent_id có thể là NULL (nếu là comment gốc) hoặc là 1 con số (nếu là reply)
        String sql = "INSERT INTO BlogComments (blog_id, user_id, parent_id, content, created_at) "
                + "VALUES (?, ?, ?, ?, GETDATE())";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, comment.getBlogId());
            ps.setLong(2, comment.getUserId());

            // Xử lý parent_id
            if (comment.getParentId() != null && comment.getParentId() > 0) {
                ps.setInt(3, comment.getParentId());
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }

            ps.setString(4, comment.getContent());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (Exception e) {
            System.out.println("===== ERROR IN insertComment =====");
            e.printStackTrace();
        }
        return false;
    }

    public void increaseCommentCount(int blogId) {
        String sql = "UPDATE Blogs SET comment_count = comment_count + 1 WHERE blog_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, blogId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void increaseViewCount(int blogId) {
        String sql = "UPDATE Blogs SET view_count = view_count + 1 WHERE blog_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, blogId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

// Lấy chi tiết 1 bài viết theo ID (Dùng cho trang Blog Detail)
    public Blog getBlogById(int blogId) {
        String sql = "SELECT b.*, u.username FROM Blogs b "
                + "JOIN Users u ON b.user_id = u.user_id "
                + "WHERE b.blog_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, blogId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Blog b = new Blog();
                b.setBlogId(rs.getInt("blog_id"));
                b.setTitle(rs.getString("title"));
                b.setContent(rs.getString("content"));
                b.setThumbnailUrl(rs.getString("thumbnail_url"));
                b.setCreatedAt(rs.getTimestamp("created_at"));
                b.setViewCount(rs.getInt("view_count"));
                b.setCommentCount(rs.getInt("comment_count"));
                b.setAuthorName(rs.getString("username")); // Lấy tên tác giả từ bảng Users
                return b;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

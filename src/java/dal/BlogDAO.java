package dal;

import config.DBContext;
import model.Blog;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BlogDAO extends DBContext {

    // 1. COUNT TOTAL PUBLISHED BLOGS (For Pagination)
    public int countTotalBlogs(String searchKeyword) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Blogs WHERE status = 1 ");

        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            sql.append(" AND title LIKE ? ");
        }

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
                ps.setString(1, "%" + searchKeyword.trim() + "%");
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("===== ERROR IN countTotalBlogs =====");
            e.printStackTrace();
        }
        return 0;
    }

    // 2. GET BLOGS BY PAGE, SEARCH, AND SORT 
    public List<Blog> getBlogsByPage(int page, String searchKeyword, String sortBy) {
        List<Blog> list = new ArrayList<>();
        int offset = (page - 1) * 9;

        // 1. XÓA u.avatar_url khỏi câu lệnh SELECT
        StringBuilder sql = new StringBuilder(
                "SELECT b.*, u.username "
                + "FROM Blogs b LEFT JOIN Users u ON b.author_id = u.user_id "
                + "WHERE b.status = 1 "
        );

        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            sql.append(" AND b.title LIKE ? ");
        }

        if ("most_viewed".equals(sortBy)) {
            sql.append(" ORDER BY b.view_count DESC, b.created_at DESC ");
        } else if ("oldest".equals(sortBy)) {
            sql.append(" ORDER BY b.created_at ASC ");
        } else {
            sql.append(" ORDER BY b.created_at DESC ");
        }

        sql.append(" OFFSET ? ROWS FETCH NEXT 9 ROWS ONLY");

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;

            if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + searchKeyword.trim() + "%");
            }
            ps.setInt(paramIndex, offset);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Blog blog = new Blog();
                blog.setBlogId(rs.getInt("blog_id"));
                blog.setTitle(rs.getString("title"));
                blog.setContent(rs.getString("content"));
                blog.setThumbnailUrl(rs.getString("thumbnail_url"));
                blog.setAuthorId(rs.getLong("author_id"));
                blog.setCreatedAt(rs.getTimestamp("created_at"));
                blog.setUpdatedAt(rs.getTimestamp("updated_at"));
                blog.setViewCount(rs.getInt("view_count"));
                blog.setCommentCount(rs.getInt("comment_count"));
                blog.setStatus(rs.getInt("status"));

                // 2. Chỉ lấy username, BỎ dòng get avatar_url đi
                blog.setAuthorName(rs.getString("username"));

                list.add(blog);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 3. GET SINGLE BLOG DETAILS
    public Blog getBlogById(int blogId) {
        // 1. XÓA u.avatar_url
        String sql = "SELECT b.*, u.username "
                + "FROM Blogs b LEFT JOIN Users u ON b.author_id = u.user_id "
                + "WHERE b.blog_id = ? AND b.status = 1";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, blogId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Blog blog = new Blog();
                blog.setBlogId(rs.getInt("blog_id"));
                blog.setTitle(rs.getString("title"));
                blog.setContent(rs.getString("content"));
                blog.setThumbnailUrl(rs.getString("thumbnail_url"));
                blog.setAuthorId(rs.getLong("author_id"));
                blog.setCreatedAt(rs.getTimestamp("created_at"));
                blog.setUpdatedAt(rs.getTimestamp("updated_at"));
                blog.setViewCount(rs.getInt("view_count"));
                blog.setCommentCount(rs.getInt("comment_count"));
                blog.setStatus(rs.getInt("status"));

                // 2. BỎ lấy avatar_url
                blog.setAuthorName(rs.getString("username"));

                return blog;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
}

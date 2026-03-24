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

    public List<Blog> getAllBlogs(String sortField, String sortOrder) {
        List<Blog> list = new ArrayList<>();

        // Mặc định sắp xếp theo ngày tạo mới nhất
        String orderBy = "created_at DESC";

        // Whitelist các cột được phép sort để chống SQL Injection
        if (sortField != null && sortOrder != null) {
            String order = sortOrder.equalsIgnoreCase("asc") ? "ASC" : "DESC";
            if (sortField.equals("views")) {
                orderBy = "view_count " + order;
            } else if (sortField.equals("comments")) {
                orderBy = "comment_count " + order;
            }
        }

        String query = "SELECT * FROM Blogs ORDER BY " + orderBy;

        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(query); ResultSet rs = ps.executeQuery()) {

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

                list.add(blog);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean deleteBlog(int blogId) {
        String query = "DELETE FROM Blogs WHERE blog_id = ?";
        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, blogId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean insertBlog(Blog blog) {
        String query = "INSERT INTO Blogs (title, content, thumbnail_url, author_id, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, blog.getTitle());
            ps.setString(2, blog.getContent());
            ps.setString(3, blog.getThumbnailUrl());
            ps.setLong(4, blog.getAuthorId());
            ps.setInt(5, blog.getStatus());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

// Cập nhật bài viết
    public boolean updateBlog(Blog blog) {
        // Lưu ý: Cập nhật lại trường updated_at thành thời gian hiện tại
        String query = "UPDATE Blogs SET title = ?, content = ?, thumbnail_url = ?, status = ?, updated_at = GETDATE() WHERE blog_id = ?";
        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, blog.getTitle());
            ps.setString(2, blog.getContent());
            ps.setString(3, blog.getThumbnailUrl());
            ps.setInt(4, blog.getStatus());
            ps.setInt(5, blog.getBlogId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateBlogStatus(int blogId, int status) {
        String query = "UPDATE Blogs SET status = ?, updated_at = GETDATE() WHERE blog_id = ?";
        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, status);
            ps.setInt(2, blogId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Blog> searchBlogs(String keyword, String sortField, String sortOrder) {
        List<Blog> list = new ArrayList<>();
        // Mặc định sắp xếp theo ngày tạo mới nhất
        String orderBy = "created_at DESC";

        // Whitelist các cột được phép sort để bảo mật
        if (sortField != null && sortOrder != null) {
            String order = sortOrder.equalsIgnoreCase("asc") ? "ASC" : "DESC";
            if (sortField.equals("views")) {
                orderBy = "view_count " + order;
            } else if (sortField.equals("comments")) {
                orderBy = "comment_count " + order;
            }
        }

        // Câu lệnh SQL: Tìm kiếm theo tiêu đề (LIKE) và Sắp xếp
        String query = "SELECT * FROM Blogs WHERE title LIKE ? ORDER BY " + orderBy;

        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {

            // Thêm dấu % để tìm kiếm tương đối
            ps.setString(1, "%" + (keyword != null ? keyword : "") + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Blog blog = new Blog();
                    blog.setBlogId(rs.getInt("blog_id"));
                    blog.setTitle(rs.getString("title"));
                    blog.setCreatedAt(rs.getTimestamp("created_at"));
                    blog.setViewCount(rs.getInt("view_count"));
                    blog.setCommentCount(rs.getInt("comment_count"));
                    blog.setStatus(rs.getInt("status"));
                    list.add(blog);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Blog getBlogByIdForAdmin(int blogId) {
        // 1. XÓA u.avatar_url
        String sql = "SELECT b.*, u.username "
                + "FROM Blogs b LEFT JOIN Users u ON b.author_id = u.user_id "
                + "WHERE b.blog_id = ?";

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
    // Trong BlogDAO.java

    public int getTotalBlogs(String keyword, String status) {
        String query = "SELECT COUNT(*) FROM Blogs WHERE title LIKE ?";
        if (status != null && !status.isEmpty()) {
            query += " AND status = " + status;
        }
        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, "%" + (keyword != null ? keyword : "") + "%");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Blog> getBlogsWithPagination(String keyword, String status, String sortField, String sortOrder, int pageIndex, int pageSize) {
        List<Blog> list = new ArrayList<>();
        // LUÔN ĐẨY HIDDEN XUỐNG DƯỚI (status 1 đứng trước status 0)
        String orderBy = "b.status DESC, b.created_at DESC"; 

        if (sortField != null && !sortField.isEmpty()) {
            String order = (sortOrder != null && sortOrder.equalsIgnoreCase("asc")) ? "ASC" : "DESC";
            if (sortField.equals("views")) {
                orderBy = "b.status DESC, b.view_count " + order;
            } else if (sortField.equals("comments")) {
                orderBy = "b.status DESC, b.comment_count " + order;
            }
        }

        // Đã bổ sung LEFT JOIN với Users để lấy authorName
        String query = "SELECT b.*, u.username "
                     + "FROM Blogs b "
                     + "LEFT JOIN Users u ON b.author_id = u.user_id "
                     + "WHERE b.title LIKE ? ";
        
        if (status != null && !status.isEmpty()) {
            query += " AND b.status = " + status;
        }
        
        query += " ORDER BY " + orderBy + " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (Connection conn = new DBContext().getConnection(); 
             PreparedStatement ps = conn.prepareStatement(query)) {
             
            ps.setString(1, "%" + (keyword != null ? keyword : "") + "%");
            ps.setInt(2, (pageIndex - 1) * pageSize);
            ps.setInt(3, pageSize);
            try (ResultSet rs = ps.executeQuery()) {
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
                    
                    // Lấy username từ bảng Users
                    blog.setAuthorName(rs.getString("username"));
                    
                    list.add(blog);
                }
            }
        } catch (Exception e) {
            System.out.println("===== ERROR IN getBlogsWithPagination =====");
            e.printStackTrace();
        }
        return list;
    }
}

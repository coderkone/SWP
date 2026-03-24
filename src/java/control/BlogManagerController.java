package control;

import dal.BlogDAO;
import model.Blog;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

// Annotation khai báo cấu hình upload file
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
    maxFileSize = 1024 * 1024 * 10,       // 10MB
    maxRequestSize = 1024 * 1024 * 50     // 50MB
)
@WebServlet(name = "BlogManagerController", urlPatterns = {
    "/admin/blogs",
    "/admin/blogs/create",
    "/admin/blogs/edit",
    "/admin/blogs/delete",
    "/admin/blogs/toggle-status",
    "/admin/blogs/search"
})
public class BlogManagerController extends HttpServlet {

    private final BlogDAO blogDAO = new BlogDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        switch (path) {
            case "/admin/blogs/create":
                // Mở trang form trống để thêm bài
                request.getRequestDispatcher("/View/Admin/blog-form.jsp").forward(request, response);
                break;

            case "/admin/blogs/edit":
                // Mở trang form kèm dữ liệu cũ để sửa
                try {
                    int id = Integer.parseInt(request.getParameter("id"));
                    // Lấy bài viết (đã fix lỗi hiển thị cả bài ẩn)
                    Blog blog = blogDAO.getBlogByIdForAdmin(id);
                    request.setAttribute("blog", blog);
                    request.getRequestDispatcher("/View/Admin/blog-form.jsp").forward(request, response);
                } catch (NumberFormatException e) {
                    response.sendRedirect(request.getContextPath() + "/admin/blogs");
                }
                break;

            case "/admin/blogs/search":
            case "/admin/blogs":
            default:
                // Hiển thị danh sách kết hợp: Tìm kiếm, Sắp xếp, Phân trang
                String keyword = request.getParameter("q");
                if(keyword != null) keyword=keyword.trim();
                String status = request.getParameter("status");
                String sort = request.getParameter("sort");
                String order = request.getParameter("order");
                
                int page = 1;
                int pageSize = 20; // 20 bài viết hiển thị trên 1 trang
                
                String pageStr = request.getParameter("page");
                if (pageStr != null && !pageStr.isEmpty()) {
                    try {
                        page = Integer.parseInt(pageStr);
                    } catch (NumberFormatException e) {
                        page = 1;
                    }
                }

                // Gọi DAO lấy dữ liệu
                int totalBlogs = blogDAO.getTotalBlogs(keyword, status);
                int totalPages = (int) Math.ceil((double) totalBlogs / pageSize);
                List<Blog> blogList = blogDAO.getBlogsWithPagination(keyword, status, sort, order, page, pageSize);

                // Đẩy dữ liệu sang JSP
                request.setAttribute("blogList", blogList);
                request.setAttribute("searchKeyword", keyword); 
                request.setAttribute("currentPage", page);
                request.setAttribute("totalPages", totalPages);
                request.setAttribute("selectedStatus", status);
                request.getRequestDispatcher("/View/Admin/blog-manager.jsp").forward(request, response);
                break;
        }
    }

    // ==========================================
    // doPost: XỬ LÝ VIỆC THAY ĐỔI DỮ LIỆU
    // ==========================================
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Đảm bảo tiếng Việt không bị lỗi font khi submit form
        request.setCharacterEncoding("UTF-8");
        String path = request.getServletPath();

        switch (path) {
            case "/admin/blogs/delete":
                try {
                    int deleteId = Integer.parseInt(request.getParameter("id"));
                    blogDAO.deleteBlog(deleteId);
                    response.sendRedirect(request.getContextPath() + "/admin/blogs?success=deleted");
                } catch (NumberFormatException e) {
                    response.sendRedirect(request.getContextPath() + "/admin/blogs");
                }
                break;

            case "/admin/blogs/toggle-status":
                try {
                    int toggleId = Integer.parseInt(request.getParameter("id"));
                    int newStatus = Integer.parseInt(request.getParameter("newStatus"));
                    blogDAO.updateBlogStatus(toggleId, newStatus);
                    response.sendRedirect(request.getContextPath() + "/admin/blogs?success=status_updated");
                } catch (NumberFormatException e) {
                    response.sendRedirect(request.getContextPath() + "/admin/blogs");
                }
                break;

            case "/admin/blogs/create":
                // Gọi hàm xử lý chung (truyền true = tạo mới)
                handleCreateOrUpdate(request, response, true);
                break;

            case "/admin/blogs/edit":
                // Gọi hàm xử lý chung (truyền false = cập nhật)
                handleCreateOrUpdate(request, response, false);
                break;
        }
    }

    // ==========================================
    // HÀM PHỤ TRỢ: XỬ LÝ UPLOAD ẢNH & LƯU DB
    // ==========================================
    private void handleCreateOrUpdate(HttpServletRequest request, HttpServletResponse response, boolean isCreate) 
            throws ServletException, IOException {
        
        try {
            // 1. Đọc các trường text
            String title = request.getParameter("title");
            String content = request.getParameter("content");
            String statusStr = request.getParameter("status");
            int status = (statusStr != null && !statusStr.isEmpty()) ? Integer.parseInt(statusStr) : 1;
            
            // 2. Xử lý file ảnh (Local Upload)
            String thumbnailUrl = request.getParameter("oldThumbnailUrl"); // Mặc định lấy ảnh cũ nếu có
            Part filePart = request.getPart("imageFile"); // Phải khớp với name="imageFile" trong input
            
            if (filePart != null && filePart.getSize() > 0) {
                // Tạo folder lưu ảnh: build/web/assets/img/blog
                String uploadPath = getServletContext().getRealPath("") + File.separator + "assets" + File.separator + "img" + File.separator + "blog";
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                // Chống trùng tên file bằng cách ghép thêm thời gian hiện tại
                String fileName = System.currentTimeMillis() + "_" + Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                
                // Ghi file vào ổ cứng
                filePart.write(uploadPath + File.separator + fileName);
                
                // Lưu chuỗi đường dẫn tương đối để ghi vào Database
                thumbnailUrl = "assets/img/blog/" + fileName;
            }

            // 3. Đóng gói vào Object Blog
            Blog blog = new Blog();
            blog.setTitle(title);
            blog.setContent(content);
            blog.setStatus(status);
            blog.setThumbnailUrl(thumbnailUrl);

            // 4. Lưu vào Database
            if (isCreate) {
                blog.setAuthorId(1); // Mặc định ID người viết (bạn có thể thay bằng Session ID)
                blogDAO.insertBlog(blog);
                response.sendRedirect(request.getContextPath() + "/admin/blogs?success=created");
            } else {
                int id = Integer.parseInt(request.getParameter("id"));
                blog.setBlogId(id);
                blogDAO.updateBlog(blog);
                response.sendRedirect(request.getContextPath() + "/admin/blogs?success=updated");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admin/blogs?error=failed");
        }
    }
}
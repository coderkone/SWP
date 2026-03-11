package control;

import dal.BlogCommentDAO;
import dal.BlogDAO;
import model.BlogComment;
import model.User;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "BlogCommentController", urlPatterns = {"/blog/comment"})
public class BlogCommentController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Kiểm tra đăng nhập
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        String blogIdStr = request.getParameter("blogId");
        
        if (user == null) {
            // Nếu chưa đăng nhập, đá về trang login
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            int blogId = Integer.parseInt(blogIdStr);
            String content = request.getParameter("content");
            String parentIdStr = request.getParameter("parentId"); // Nếu là reply thì sẽ có parentId

            // 2. Tạo đối tượng Comment
            BlogComment comment = new BlogComment();
            comment.setBlogId(blogId);
            comment.setUserId(user.getUserId());
            comment.setContent(content);
            
            if (parentIdStr != null && !parentIdStr.isEmpty()) {
                comment.setParentId(Integer.parseInt(parentIdStr));
            }

            // 3. Lưu vào database
            BlogCommentDAO commentDao = new BlogCommentDAO();
            boolean success = commentDao.insertComment(comment);

            if (success) {
                // 4. Tăng số lượng comment của bài viết lên 1
                BlogCommentDAO blogDao = new BlogCommentDAO();
                blogDao.increaseCommentCount(blogId);
            }

            // 5. Quay trở lại đúng trang bài viết vừa comment
            response.sendRedirect(request.getContextPath() + "/blog/detail?id=" + blogId);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/blog");
        }
    }
}